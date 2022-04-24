package lsd.v2.jdbc

import Helper
import org.junit.jupiter.api.Test

class LSDConnectionTest {
    private val helper = Helper()

    @Test
    fun `Batched statement test`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement("SELECT d_tax, d_next_o_id FROM bmsql_district WHERE d_w_id = ? AND d_id = ? FOR UPDATE")
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val nextOrderId = nextOrderIdStatement.executeFutureQuery()

        nextOrderIdStatement.afterQueryExecution {
            println("Select result:" + it.get())
        }

        val incNextOrderId = connection.prepareFutureStatement("UPDATE bmsql_district set d_next_o_id = ? + 1 WHERE d_w_id = ? AND d_id = ?")
        incNextOrderId.setFutureInt(1, nextOrderId.getFutureInt(2))
        incNextOrderId.setInt(2, 1)
        incNextOrderId.setInt(3, 1)
        incNextOrderId.executeFutureUpdate()

        val insertOrderStatement = connection.prepareFutureStatement("INSERT INTO bmsql_oorder (o_id, o_d_id, o_w_id, o_c_id, o_entry_d, o_ol_cnt, o_all_local) VALUES (?, 7, 1, 2844, '2022-04-18 15:36:28.625+01', 8, 1 )")
        insertOrderStatement.setFutureInt(1, nextOrderId.getFutureInt(2))
        insertOrderStatement.executeFutureUpdate()

        val newOrderStatement = connection.prepareFutureStatement("INSERT INTO bmsql_new_order (no_o_id, no_d_id, no_w_id) VALUES (?, 7, 1)")
        newOrderStatement.setFutureInt(1, nextOrderId.getFutureInt(2))
        newOrderStatement.executeFutureUpdate()

        newOrderStatement.afterUpdateExecution {
            println("Update result:" + it.get())
        }

        val lineBatch = connection.prepareFutureStatement("INSERT INTO bmsql_order_line (ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, ol_supply_w_id, ol_quantity, ol_amount, ol_dist_info) " +
                "VALUES (?, 7, 1, ?, 8646, 1, 6, 156.12, 'd3lFUIShl9C1R0cQPDJdmMoy')")

        for (line in 1..300) {
            lineBatch.setFutureInt(1, nextOrderId.getFutureInt(2))
            lineBatch.setInt(2, line)
            lineBatch.addFutureBatch()
        }
        lineBatch.executeFutureBatch()

        lineBatch.afterBatchExecution {
            println("Batch result:" + it.get())
            if (it.get()[0] != 0) {
                throw java.lang.RuntimeException("Something went wrong")
            }
        }

        connection.commit()
    }

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

        val taxValue = connection2.prepareFutureStatement(
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

        val stmt = connection2.prepareFutureStatement(
            "SELECT d_next_o_id FROM bmsql_district WHERE d_w_id = 1 AND d_id = 2123123213121231"
        )
        stmt.afterQueryExecution {
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