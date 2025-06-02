package com.myorg.alfa;

import com.axiomatics.cr.alfa.test.junit.AlfaExtension;
import com.axiomatics.cr.alfa.test.junit.TestRequest;
import com.axiomatics.cr.alfa.test.junit.TestResponse;

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

import java.util.stream.Stream;

public class SchoolPolicyUnitTest {

    @RegisterExtension
    public AlfaExtension alfa = new AlfaExtension()
            // Uncomment the following like to enable evaluation trace
            // .withEvaluationTrace()
            .withMainPolicy("school.Main");

    @Test
    public void commonRule_shouldNotPermitWhenGradeLevelMisaligned() {
        TestRequest target = alfa.newTestRequest()
                .with("user.userRole", "Teachers")
                .with("user.gradeLevel", "K")
                .with("record.student.grade", "1");

        TestResponse result = target.evaluate();

        assertThat(result, is(not(permit())));
    }

    @ParameterizedTest
    @MethodSource("houseProvider")
    public void accessRulesRow14_shouldPermitRead(String house) {
        TestRequest target = alfa.newTestRequest()
                .with("user.userRole", "Teacher")
                .with("user.gradeLevel", "K")
                .with("user.house", house)
                .with("record.student.grade", "K")
                .with("record.student.house", "Kangaroo")
                .with("action.operation", "Read");

        TestResponse result = target.evaluate();

        assertThat(result, is(permit()));
    }

    @ParameterizedTest
    @MethodSource("houseProvider")
    public void accessRulesRow14_shouldPermitSearch(String house) {
        var grade = "K";
        TestRequest target = alfa.newTestRequest()
                .with("user.userRole", "Teacher")
                .with("user.gradeLevel", grade)
                .with("user.house", house)
                .with("record.student.grade", grade)
                .with("record.student.house", "Kangaroo")
                .with("action.operation", "Search");

        TestResponse result = target.evaluate();

        assertThat(result, is(permit()));
    }

    @ParameterizedTest
    @MethodSource("kpkHouseProvider")
    public void accessRulesRow17_shouldPermitMusicTeacherReadRecordsWithRedactedReports(String house) {
        var grade = "3";
        TestRequest target = alfa.newTestRequest()
                .with("user.userRole", "Teacher")
                .with("user.gradeLevel", grade)
                .with("user.house", house)
                .with("user.subject", "Music")
                .with("record.student.grade", grade)
                .with("record.student.house", "Koala")
                .with("record.student.mathReportsIds", "REC12345")
                .with("action.operation", "Read");

        TestResponse result = target.evaluate();

        assertThat(result, is(permit()));

        assertThat(result.getObligationsSize(), is(2));

        var obligationsIterator = result.getObligations().iterator();

        var obligationAssignment = obligationsIterator.next();
        assertThat(obligationAssignment.getId(), is("record.filtering.fieldToRemove"));
        assertThat(obligationAssignment.getValue(), is("report.comments"));

        obligationAssignment = obligationsIterator.next();
        assertThat(obligationAssignment.getId(), is("record.filtering.reportsToRedact"));
        assertThat(obligationAssignment.getValue(), is("REC12345"));
    }

    static Stream<String> houseProvider() {
        return Stream.of("Kangaroo", "Dingo", "Platypus", "Koala", "Wombat", "Emu");
    }

    static Stream<String> notDingoHouseProvider() {
        return Stream.of("Kangaroo", "Dingo", "Platypus", "Koala", "Wombat", "Emu");
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
                .flatMap(house -> notMathSubjectProvider().map(subject -> Arguments.of(house, subject)));
    }

}
