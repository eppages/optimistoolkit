Feature: Distributed Object Storage System add-on to HDFS

  Scenario: Create an object, get the object and delete the object
    Given a DM cluster is deployed in "Flexiant" with sid "ehealth"
    When a new object is created and stored to odoss
    Then the object retrieved is the same
    And  the object can be deleted 










