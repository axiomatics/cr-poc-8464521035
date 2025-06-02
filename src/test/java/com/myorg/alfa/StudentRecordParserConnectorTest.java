package com.myorg.alfa;

import com.axiomatics.cr.alfa.test.junit.AlfaExtension;
import com.axiomatics.cr.alfa.test.junit.AttributeConnector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class StudentRecordParserConnectorTest {

  @RegisterExtension
  public AlfaExtension alfa = new AlfaExtension();

  @Test
  public void shouldGetStudentHouseFromStudentRecord() {
    AttributeConnector target = alfa.newAttributeTest("studentRecordParser");

    List<String> result = target.lookup("record.student.house").by("record.Student", sampleData);

    assertThat(result, hasItem("Emu"));
  }

  @Test
  public void shouldGetStudentGradeFromStudentRecord() {
    AttributeConnector target = alfa.newAttributeTest("studentRecordParser");

    List<String> result = target.lookup("record.student.grade").by("record.Student", sampleData);

    assertThat(result, hasItem("1"));
  }

  @Test
  public void shouldGetStudentRecordTypeFromStudentRecord() {
    AttributeConnector target = alfa.newAttributeTest("studentRecordParser");

    List<String> result = target.lookup("record.student.recordType").by("record.Student", sampleData);

    assertThat(result, hasItem("Current"));
  }

  @Test
  public void shouldGetMathReportsIds() {
    AttributeConnector target = alfa.newAttributeTest("studentRecordParser");

    List<String> result = target.lookup("record.student.mathReportsIds").by("record.Student", sampleData);

    assertThat(result, contains("REC101", "REC107"));
  }

  @Test
  public void shouldGetMusicReportsIds() {
    AttributeConnector target = alfa.newAttributeTest("studentRecordParser");

    List<String> result = target.lookup("record.student.musicReportsIds").by("record.Student", sampleData);

    assertThat(result, contains("REC106"));
  }

  @Test
  public void shouldGetBigLunchOrdersIds() {
    AttributeConnector target = alfa.newAttributeTest("studentRecordParser");

    List<String> result = target.lookup("record.student.bigLunchOrdersIds").by("record.Student", sampleData);

    assertThat(result, contains("REC202"));
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
