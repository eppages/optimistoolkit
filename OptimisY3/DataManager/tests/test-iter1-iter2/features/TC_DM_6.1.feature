Feature: Automatic deployment on federated providers

  Scenario: Deploy DataNode to a federated provider
    Given a DM cluster is deployed in "Flexiant" with sid "ehealth"
    When risk Assesment module suggests federation
    Then create a federation VM in "atos"
    And  the new DataNode is attached to the cluster





