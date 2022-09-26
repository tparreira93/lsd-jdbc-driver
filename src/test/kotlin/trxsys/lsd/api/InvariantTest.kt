package trxsys.lsd.api

import Helper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import trxsys.lsd.api.FutureInvariant.Companion.futureIs
import trxsys.lsd.api.InvariantChain.Companion.and
import trxsys.lsd.jdbc.nextOrderId

class InvariantTest {
    private val helper = Helper()

    @Test
    fun `False branch is executed as expected is-true statement`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val result = nextOrderIdStatement.executeFutureQuery()

        val futureInt = result.getFutureInt(1)
        val condition = connection.isTrue(and(
            futureIs(futureInt) { it == 0 }
        ))

        condition.whenTrue {
            fail("Condition should be true!")
        }.whenFalse {
            assert(true)
        }

        connection.commit()
    }

    @Test
    fun `True branch is executed as expected is-true statement`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val result = nextOrderIdStatement.executeFutureQuery()

        val futureInt = result.getFutureInt(1)
        val condition = connection.isTrue(and(
            futureIs(futureInt) { it != 0 }
        ))

        condition.whenTrue {
            assert(true)
        }.whenFalse {
            fail("Condition should be true!")
        }

        connection.commit()
    }
}