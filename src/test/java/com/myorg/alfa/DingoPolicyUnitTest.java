package com.myorg.alfa;

import com.axiomatics.cr.alfa.test.junit.AlfaExtension;
import com.axiomatics.cr.alfa.test.junit.ObligationResponse;
import com.axiomatics.cr.alfa.test.junit.TestRequest;
import com.axiomatics.cr.alfa.test.junit.TestResponse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.axiomatics.cr.alfa.test.junit.matchers.AlfaMatchers.permit;
import static com.axiomatics.cr.alfa.test.junit.matchers.AlfaMatchers.deny;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DingoPolicyUnitTest {

        @RegisterExtension
        public AlfaExtension alfa = new AlfaExtension()
                        // Uncomment the following like to enable evaluation trace
                        // .withEvaluationTrace()
                        .withMainPolicy("school.Main");

        /**
         * Creates a base TestRequest for the Dingo policy tests.
         * This method sets up a request with common parameters:
         * - Student Record house as "Dingo"
         * - User role as "Teacher" with a specific house and subject
         * - Action operation as "Read"
         * - User and Student Record grade levels aligned to a given value.
         *
         * @param house
         * @param subject
         * @param grade
         * @return
         */
        TestRequest createBaseReadTestRequest(String house, String subject, String grade) {
                return alfa.newTestRequest()
                                .with("user.userRole", "Teacher")
                                .with("user.house", house)
                                .with("user.subject", subject)
                                .with("action.operation", "Read")
                                .with("record.student.house", "Dingo")
                                .with("user.gradeLevel", grade)
                                .with("record.student.grade", grade);
        }

        /**
         * Creates a base TestRequest for the Dingo policy tests.
         * This method sets up a request with common parameters:
         * - Student Record house as "Dingo"
         * - User role as "Teacher" with a specific house and subject
         * - Action operation as "Search"
         * - User and Student Record grade levels aligned to a given value.
         *
         * @param house
         * @param subject
         * @param grade
         * @return
         */
        TestRequest createBaseSearchTestRequest(String house, String subject, String grade) {
                return alfa.newTestRequest()
                                .with("user.userRole", "Teacher")
                                .with("user.house", house)
                                .with("user.subject", subject)
                                .with("action.operation", "Search")
                                .with("record.student.house", "Dingo")
                                .with("user.gradeLevel", grade)
                                .with("record.student.grade", grade);
        }

        @ParameterizedTest
        @MethodSource("notDingoNoMathProvider")
        public void accessRulesRow15_shouldPermitReadingWithRedactedFields(String house, String subject) {
                // Any grade level is allowed for this test
                var grade = "5";
                // Student Record type must be "Current"
                // Environment access point must be "Intranet"
                TestRequest target = createBaseReadTestRequest(house, subject, grade)
                                .with("record.student.recordType", "Current")
                                .with("environment.accessPoint", "Intranet");

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap, hasEntry("record.filtering.fieldToRemove", "student.interest"));
        }

        @ParameterizedTest
        @MethodSource("kpkHouseNoMathProvider")
        public void accessRulesRow16_shouldPermitReadingWithRedactedFields(String house, String subject) {
                // Any grade level is allowed for this test
                var grade = "6";
                // Student Record type must be "Current"
                // Environment access point must be "Intranet"
                TestRequest target = createBaseReadTestRequest(house, subject, grade)
                                .with("record.student.recordType", "Current")
                                .with("environment.accessPoint", "Intranet");

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());

                assertThat(obligationMap, hasEntry("record.filtering.fieldToRemove", "student.interest"));
                assertThat(obligationMap, hasEntry("record.filtering.fieldToKeep", "student.recordId"));
        }

        @ParameterizedTest
        @MethodSource("kpkHouseAnySubjectProvider")
        public void accessRulesRow18_shouldPermitReadingWithFilteredMusicReports(String house, String subject) {
                // Grade level must be "K" for this test case
                var grade = "K";
                TestRequest target = createBaseReadTestRequest(house, subject, grade)
                                .with("record.student.musicReportsIds", "REC12345");

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap, hasEntry("record.filtering.reportsToRemove", "REC12345"));
        }

        @ParameterizedTest
        @MethodSource("kpkHouseAnySubjectProvider")
        public void accessRulesRow19_shouldPermitKTeacherReadingWithFilteredBigLunchOrders(String house,
                        String subject) {
                // Grade level must be "K" for this test case
                var grade = "K";
                TestRequest target = createBaseReadTestRequest(house, subject, grade)
                                .with("record.student.bigLunchOrdersIds", "REC67890");

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap, hasEntry("record.filtering.lunchOrdersToRemove", "REC67890"));
        }

        @ParameterizedTest
        @MethodSource("kpkHouseAnySubjectProvider")
        public void accessRulesRow19_shouldPermitKTeacherReadingWithFilteredMusicReportsAndBigLunchOrders(String house,
                        String subject) {
                // Grade level must be "K" for this test case
                var grade = "K";
                TestRequest target = createBaseReadTestRequest(house, subject, grade)
                                .with("record.student.musicReportsIds", "REC12345")
                                .with("record.student.bigLunchOrdersIds", "REC67890");

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap, hasEntry("record.filtering.reportsToRemove", "REC12345"));
                assertThat(obligationMap, hasEntry("record.filtering.lunchOrdersToRemove", "REC67890"));
        }

        @ParameterizedTest
        @MethodSource("kpkHouseAnySubjectProvider")
        public void accessRulesRow19_shouldPermitKTeacherReadingWhenNothingToRedactOrFilter(String house,
                        String subject) {
                // Grade level must be "K" for this test case
                var grade = "K";
                // No music reports or big lunch orders to filter, so
                // - "record.student.musicReportsIds" must be empty
                // - "record.student.bigLunchOrdersIds" must be empty
                TestRequest target = createBaseReadTestRequest(house, subject, grade);

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap.size(), is(0));
        }

        @ParameterizedTest
        @MethodSource("kpkHouseAnySubjectProvider")
        public void accessRulesRow20_shouldNotPermitSearch(String house, String subject) {
                // Grade level must be "K" for this test case
                var grade = "K";
                // We need to ensure these are values that, for a Read operation,
                // would result in a permit with filtering obligations.
                TestRequest target = createBaseSearchTestRequest(house, subject, grade)
                                .with("record.student.musicReportsIds", "REC12345")
                                .with("record.student.bigLunchOrdersIds", "REC67890");

                TestResponse result = target.evaluate();

                assertThat(result, is(not(permit())));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap.size(), is(0));
        }

        @ParameterizedTest
        @MethodSource("anyHouseAnySubjectProvider")
        public void accessRulesRow20_shouldPermitSearch(String house, String subject) {
                // We need to ensure request values that, for a Read operation,
                // would _not_ result in a permit with filtering obligations,
                // even in the presence of records that may require filtering in some cases.
                // In this case, we chose a grade level that is not "K".
                var grade = "1";
                // But we add some values for music reports and big lunch orders,
                // that are that may require filtering is some other contexts.
                TestRequest target = createBaseSearchTestRequest(house, subject, grade)
                                .with("record.student.musicReportsIds", "REC12345")
                                .with("record.student.bigLunchOrdersIds", "REC67890");

                TestResponse result = target.evaluate();

                assertThat(result, is(permit()));

                var obligationMap = obligationsToMap(result.getObligations());
                assertThat(obligationMap.size(), is(0));
        }

        static Map<String, String> obligationsToMap(Iterable<ObligationResponse> obligations) {
                return StreamSupport.stream(obligations.spliterator(), false)
                                .collect(Collectors.toMap(
                                                ObligationResponse::getId, // key mapper function (gets ID)
                                                ObligationResponse::getValue // value mapper function (gets value)
                                ));
        }

        static Stream<String> houseProvider() {
                return Stream.of("Kangaroo", "Dingo", "Platypus", "Koala", "Wombat", "Emu");
        }

        static Stream<String> notDingoHouseProvider() {
                return Stream.of("Kangaroo", "Platypus", "Koala", "Wombat", "Emu");
        }

        static Stream<String> kpkHouseProvider() {
                return Stream.of("Kangaroo", "Platypus", "Koala");
        }

        static Stream<String> subjectProvider() {
                return Stream.of("Math", "English", "Physics", "Music");
        }

        static Stream<String> notMathSubjectProvider() {
                return Stream.of("English", "Physics", "Music");
        }

        static Stream<Arguments> notDingoNoMathProvider() {
                return notDingoHouseProvider()
                                .flatMap(house -> notMathSubjectProvider()
                                                .map(subject -> Arguments.of(house, subject)));
        }

        static Stream<Arguments> kpkHouseNoMathProvider() {
                return kpkHouseProvider()
                                .flatMap(house -> notMathSubjectProvider()
                                                .map(subject -> Arguments.of(house, subject)));
        }

        static Stream<Arguments> kpkHouseAnySubjectProvider() {
                return kpkHouseProvider()
                                .flatMap(house -> subjectProvider().map(subject -> Arguments.of(house, subject)));
        }

        static Stream<Arguments> anyHouseAnySubjectProvider() {
                return houseProvider()
                                .flatMap(house -> subjectProvider().map(subject -> Arguments.of(house, subject)));
        }
}
