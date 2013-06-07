Feature: Logging mechanisms

  Scenario: Ability to keep detailed logs regarding the accesses to the HDFS. 
    Given a DM cluster is deployed in "Flexiant" with sid "ehealth"
    And   a file "thecloud.txt" is uploaded to hdfs cluster
    Then  the file "thecloud.txt" is accessible and is not corrupted
    And   the file "thecloud.txt" can be deleted
    And   the logs monitored read access "70230"











