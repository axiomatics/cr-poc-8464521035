package com.myorg.alfa;

import com.axiomatics.cr.alfa.test.junit.AlfaExtension;
import com.axiomatics.cr.alfa.test.junit.TestRequest;
import com.axiomatics.cr.alfa.test.junit.TestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.axiomatics.cr.alfa.test.junit.matchers.AlfaMatchers.permit;
import static com.axiomatics.cr.alfa.test.junit.matchers.AttributeAssignmentMatcher.withText;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class SchoolPolicyIntegrationTest {

  @RegisterExtension
  public AlfaExtension alfa = new AlfaExtension().withAttributeConnectors();

  @Test
  public void commonRule_shouldNotPermitWhenGradeLevelMisaligned() {
    TestRequest target = alfa.newTestRequest()
        .with("user.userRole", "Teachers")
        .with("user.gradeLevel", "K")
        .with("record.Student", sampleData);

    TestResponse result = target.evaluate();

    assertThat(result, is(not(permit())));
  }

  String sampleData = """
      {
        "recordId": "REC001",
        "recordType": "Current",
        "studentName": "John Smith",
        "grade": 1,
        "house": "Emu",
        "club": "Art",
        "interest": "Piano",
        "class": {
          "name": "1AJ",
          "teacher": "Anna Johnson",
          "location": "building A level 1"
        },
        "reports": [
          {
            "recordId": "REC101",
            "year": 2025,
            "subject": "Math",
            "teacher": "Anna Johnson",
            "comments": "Demonstrates a positive attitude towards learning."
          },
          {
            "recordId": "REC102",
            "year": 2025,
            "subject": "English",
            "teacher": "David Brown",
            "comments": "Excels in creative writing."
          },
          {
            "recordId": "REC103",
            "year": 2025,
            "subject": "Science",
            "teacher": "Emily White",
            "comments": "Shows great curiosity in scientific concepts."
          },
          {
            "recordId": "REC104",
            "year": 2025,
            "subject": "History",
            "teacher": "Michael Green",
            "comments": "Has a keen interest in historical events."
          },
          {
            "recordId": "REC105",
            "year": 2025,
            "subject": "Art",
            "teacher": "Sarah Blue",
            "comments": "Displays exceptional creativity in art projects."
          },
          {
            "recordId": "REC106",
            "year": 2025,
            "subject": "Music",
            "teacher": "Laura Black",
            "comments": "Shows a strong passion for music."
          },
          {
            "recordId": "REC107",
            "year": 2025,
            "subject": "Math",
            "teacher": "Anna Johnson",
            "comments": "Needs improvement in problem-solving skills."
          }
        ],
        "lunchOrders": [
          {
            "recordId": "REC201",
            "date": "2025-01-10",
            "totalItems": 2
          },
          {
            "recordId": "REC202",
            "date": "2025-01-11",
            "totalItems": 6
          }
        ]
      }
              """;
}
