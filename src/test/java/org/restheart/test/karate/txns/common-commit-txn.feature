@ignore
Feature: feature that aborts a txn

# call as follows 
# def txn = call read('common-commit-txn.feature') { sid: <sessionId>, txn: txnNum }

Background:
* url baseUrl

@requires-mongodb-4 @requires-replica-set
Scenario: check session and abort txn
    Given path '/_sessions/' + sid + '/_txns'
    When method GET
    Then status 200
    And match response.currentTxn.status == 'IN'
    And match response.currentTxn.id == txn

    Given path '/_sessions/' + sid + '/_txns/' + txn
    And request {}
    When method PATCH
    Then status 200

    Given path '/_sessions/' + sid + '/_txns'
    When method GET
    Then status 200
    And match response.currentTxn.status == 'COMMITTED'
    And match response.currentTxn.id == txn