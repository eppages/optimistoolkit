Feature: Cloud Provider legal constraints validation

  Scenario Outline: Ability to filter providers based on their capabilities and features descriptions
    Given a DM cluster is deployed in "Flexiant" with sid "ehealth"
    When  the SD/DO calls the DM with arguments the sid: "ehealth" and the "<iprovider>"
    Then  the result of check legal should be "<legalAssessment>"

    Examples:
      | iprovider | legalAssessment |
      | atos      | true            |
      | umea      | false           |











