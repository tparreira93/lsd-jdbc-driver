package lsd.v2.jdbc

import Helper
import org.junit.jupiter.api.Test
import java.sql.PreparedStatement

public const val nextOrderId = "SELECT d_next_o_id, d_tax FROM bmsql_district WHERE d_w_id = ? AND d_id = ?"

private const val updateNextOrderId = "UPDATE bmsql_district set d_next_o_id = ? + 1 WHERE d_w_id = ? AND d_id = ?"

private const val oOrder =
    "INSERT INTO bmsql_oorder (o_id, o_d_id, o_w_id, o_c_id, o_entry_d, o_ol_cnt, o_all_local) VALUES (?, 7, 1, 2844, '2022-04-18 15:36:28.625+01', 8, 1 )"

private const val newOrder = "INSERT INTO bmsql_new_order (no_o_id, no_d_id, no_w_id) VALUES (?, 7, 1)"

private const val newOrderLine =
    "INSERT INTO bmsql_order_line (ol_o_id, ol_d_id, ol_w_id, ol_number, ol_i_id, ol_supply_w_id, ol_quantity, ol_amount, ol_dist_info) " +
            "VALUES (?, 7, 1, ?, 8646, 1, 6, 156.12, 'd3lFUIShl9C1R0cQPDJdmMoy')"

internal class LSDPreparedStatementTest{
    private val helper = Helper()

    private fun <T: PreparedStatement> prepareNextOrderId(statement: T) {
        statement.setInt(1, 1)
        statement.setInt(2, 1)
    }

    @Test
    fun `Select statement is resolved at commit time`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        prepareNextOrderId(nextOrderIdStatement)

        nextOrderIdStatement.executeFutureQuery()

        assert(nextOrderIdStatement.resultSet == null)

        connection.commit()

        assert(nextOrderIdStatement.resultSet != null)
    }

    @Test
    fun `Update statement is resolved at commit time`() {
        val futureConnection = helper.createLSDConnection()
        val realConnection = helper.createConnection()

        val prepareStatement = realConnection.prepareStatement(nextOrderId)
        prepareNextOrderId(prepareStatement)
        val nextOrderIdStatement = futureConnection.prepareFutureStatement(nextOrderId)
        prepareNextOrderId(nextOrderIdStatement)

        val initialValue = prepareStatement.executeQuery().let {
            it.next()
            it.getInt(1)
        }

        val resultSet = nextOrderIdStatement.executeFutureQuery()

        assert(nextOrderIdStatement.resultSet == null)

        val incNextOrderId = futureConnection.prepareFutureStatement(updateNextOrderId)
        incNextOrderId.setFutureInt(1, resultSet.getFutureInt(1))
        incNextOrderId.setInt(2, 1)
        incNextOrderId.setInt(3, 1)
        incNextOrderId.executeFutureUpdate()

        assert(initialValue == prepareStatement.executeQuery().let {
            it.next()
            it.getInt(1)
        })

        futureConnection.commit()

        val currentValue = prepareStatement.executeQuery().let {
            it.next()
            it.getInt(1)
        }

        assert(initialValue + 1 == currentValue)
    }
    @Test
    fun `Insert statement is resolved at commit time`() {
        val futureConnection = helper.createLSDConnection()
        val realConnection = helper.createConnection()

        val prepareStatement = realConnection.prepareStatement(nextOrderId)
        prepareNextOrderId(prepareStatement)
        val nextOrderIdStatement = futureConnection.prepareFutureStatement(nextOrderId)
        prepareNextOrderId(nextOrderIdStatement)

        val resultSet = nextOrderIdStatement.executeFutureQuery()

        assert(nextOrderIdStatement.resultSet == null)

        val incNextOrderId = futureConnection.prepareFutureStatement(updateNextOrderId)
        incNextOrderId.setFutureInt(1, resultSet.getFutureInt(1))
        incNextOrderId.setInt(2, 1)
        incNextOrderId.setInt(3, 1)
        incNextOrderId.executeFutureUpdate()

        val orderId = prepareStatement.executeQuery().let {
            it.next()
            it.getInt(1)
        }

        val insertOrderStatement = futureConnection.prepareFutureStatement(oOrder)
        insertOrderStatement.setFutureInt(1, resultSet.getFutureInt(1))
        insertOrderStatement.executeFutureUpdate()

        futureConnection.commit()

        val selectOrder = realConnection.prepareStatement("SELECT o_id FROM bmsql_oorder WHERE o_id = ?")
        selectOrder.setInt(1, orderId)

        val order = selectOrder.executeQuery().let {
            it.next()
            it.getInt(1)
        }

        assert(orderId == order)
    }

    @Test
    fun `Batched statement is resolved at commit time`() {
        val futureConnection = helper.createLSDConnection()
        val realConnection = helper.createConnection()

        val prepareStatement = realConnection.prepareStatement(nextOrderId)
        prepareNextOrderId(prepareStatement)
        val nextOrderIdStatement = futureConnection.prepareFutureStatement(nextOrderId)
        prepareNextOrderId(nextOrderIdStatement)

        val resultSet = nextOrderIdStatement.executeFutureQuery()

        assert(nextOrderIdStatement.resultSet == null)

        val incNextOrderId = futureConnection.prepareFutureStatement(updateNextOrderId)
        incNextOrderId.setFutureInt(1, resultSet.getFutureInt(1))
        incNextOrderId.setInt(2, 1)
        incNextOrderId.setInt(3, 1)
        incNextOrderId.executeFutureUpdate()

        val orderId = prepareStatement.executeQuery().let {
            it.next()
            it.getInt(1)
        }

        val insertOrderStatement = futureConnection.prepareFutureStatement(oOrder)
        insertOrderStatement.setFutureInt(1, resultSet.getFutureInt(1))
        insertOrderStatement.executeFutureUpdate()


        val newOrderStatement = futureConnection.prepareFutureStatement(newOrder)
        newOrderStatement.setFutureInt(1, resultSet.getFutureInt(1))
        newOrderStatement.executeFutureUpdate()


        val lineBatch = futureConnection.prepareFutureStatement(newOrderLine)

        val records = 300
        for (line in 1..records) {
            lineBatch.setFutureInt(1, resultSet.getFutureInt(1))
            lineBatch.setInt(2, line)
            lineBatch.addFutureBatch()
        }
        lineBatch.executeFutureBatch()

        futureConnection.commit()

        val countNewOrderLines =
            realConnection.prepareStatement("SELECT COUNT(ol_o_id) FROM bmsql_order_line WHERE ol_o_id = ? AND ol_w_id = ?")
        countNewOrderLines.setInt(1, orderId)
        countNewOrderLines.setInt(2, 1)

        val insertedNewOrderLine = countNewOrderLines.executeQuery().let {
            it.next()
            it.getInt(1)
        }

        assert(records == insertedNewOrderLine)
    }
}