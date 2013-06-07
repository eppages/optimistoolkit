Feature: Data Integrity and Data Deletion

  Scenario: Ensure data integrity during elasticity or node failure and data deletion.
    Given a DM cluster is deployed in "Flexiant" with sid "ehealth"
    And   a file "thecloud.txt" is uploaded to hdfs cluster
    When  a DataNode is going down
    Then  the file "thecloud.txt" is accessible and is not corrupted
    And   the file "thecloud.txt" can be deleted











