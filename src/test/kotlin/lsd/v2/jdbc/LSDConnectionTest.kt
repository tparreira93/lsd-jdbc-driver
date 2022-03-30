package lsd.v2.jdbc

import Helper
import org.junit.jupiter.api.Test

class LSDConnectionTest {
    private val helper = Helper()

//    @Test
    fun `This stuff works`() {
        val connection = helper.createLSDConnection()

        val nextIdFuture = connection.prepareFutureStatement(
            "SELECT d_next_o_id FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2 FOR UPDATE"
        )
        val nextIdFutureResultSet = nextIdFuture.executeFutureQuery()

        val futureUpdate = connection.prepareFutureStatement(
            "UPDATE bmsql_district SET d_next_o_id = ? + 1 WHERE d_w_id = ? AND d_id = ?"
        )

        futureUpdate.setFutureInt(1, nextIdFutureResultSet.getFutureInt(1))
        futureUpdate.setInt(2, 1)
        futureUpdate.setInt(3, 1)

        futureUpdate.executeFutureUpdate()

        connection.commit()


        val connection2 = helper.createLSDConnection()

        val taxValue = connection.prepareFutureStatement(
            "SELECT d_tax_value FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2 FOR UPDATE"
        ).executeFutureQuery()

        val futureCondition = connection2.isTrue("? > 0.23")
        futureCondition
            .whenTrue {
                // Execute some statement
            }
            .whenFalse {
                // Execute some other statement
            }

        futureUpdate.setFutureFloat(1, taxValue.getFutureFloat(1))

        connection2.commit()

        val stmt = connection2.prepareFutureStatement(
            "SELECT d_next_o_id FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2123123213121231"
        )
        stmt.then {
            if (futureUpdate.isClosed) {
                println("I am closed")
            } else {
                println("I am open")
            }
        }

        stmt.executeFutureQuery()

        connection2.commit()
    }

    fun someUpdateInLSD() {
        val connection = helper.createLSDConnection()

        val nextIdFutureStatement = connection.prepareFutureStatement(
            "SELECT d_next_o_id FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2 FOR UPDATE"
        )

        val nextIdFutureResultSet = nextIdFutureStatement.executeFutureQuery()

        val futureUpdate = connection.prepareFutureStatement(
            "UPDATE bmsql_district SET d_next_o_id = ? + 1 WHERE d_w_id = ? AND d_id = ?"
        )

        futureUpdate.setFutureInt(1, nextIdFutureResultSet.getFutureInt(1))
        futureUpdate.setInt(2, 1)
        futureUpdate.setInt(3, 2)

        futureUpdate.executeFutureUpdate()

        connection.commit()
    }

    fun someUpdate() {
        val connection = helper.createConnection()

        val nextIdStatement = connection.prepareStatement(
            "SELECT d_next_o_id FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2 FOR UPDATE"
        )

        val nextId = nextIdStatement.executeQuery()

        val updateQuery = connection.prepareStatement(
            "UPDATE bmsql_district SET d_next_o_id = ? + 1 WHERE d_w_id = ? AND d_id = ?"
        )

        updateQuery.setInt(1, nextId.getInt(1))
        updateQuery.setInt(2, 1)
        updateQuery.setInt(3, 2)

        updateQuery.executeUpdate()

        connection.commit()
    }

    @Test
    fun someConditionInLSD() {
        val connection = helper.createLSDConnection()

        val futureTaxValueStatement = connection.prepareFutureStatement(
            "SELECT d_tax FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2 FOR UPDATE"
        )

        val taxValueFutureResultSet = futureTaxValueStatement.executeFutureQuery()

        val futureCondition = connection.isTrue("? > 0.23")
        futureCondition
            .whenTrue {
                // Execute some statement
            }
            .whenFalse {
                // Execute some other statement
            }

        futureCondition.setFutureFloat(1, taxValueFutureResultSet.getFutureFloat(1))

        connection.commit()
    }

    fun someCondition() {
        val connection = helper.createConnection()

        val taxValueStatement = connection.prepareStatement(
            "SELECT d_tax_value FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2 FOR UPDATE"
        )

        val taxValueResultSet = taxValueStatement.executeQuery()

        if (taxValueResultSet.getFloat(1) > 0.23) {
            // Execute some statement
        } else {
            // Execute some other statement
        }

        connection.commit()
    }
}