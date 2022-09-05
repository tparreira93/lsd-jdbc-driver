package trxsys.lsd.jdbc

import Helper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class LSDConnectionTest {
    private val helper = Helper()

    @Test
    fun `True branch is executed as expected with is-true statement`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val result = nextOrderIdStatement.executeFutureQuery()

        val condition = connection.isTrue("? > 0")

        condition.setFutureInt(1, result.getFutureInt(1))

        condition.whenTrue {
            assert(true)
        }.whenFalse {
            fail("Condition should be true!")
        }

        connection.commit()
    }

    @Test
    fun `True branch is executed as expected with simple is-true`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val result = nextOrderIdStatement.executeFutureQuery()

        val condition = connection.isTrue { result.getFutureInt(1).resolve() > 0 }

        condition.whenTrue {
            assert(true)
        }.whenFalse {
            fail("Condition should be true!")
        }

        connection.commit()
    }

    @Test
    fun `False branch is executed as expected is-true statement`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val result = nextOrderIdStatement.executeFutureQuery()

        val condition = connection.isTrue("? = 0")

        condition.setFutureInt(1, result.getFutureInt(1))

        condition.whenTrue {
            fail("Condition should be true!")
        }.whenFalse {
            assert(true)
        }

        connection.commit()
    }

    @Test
    fun `False branch is executed as expected with simple is-true`() {
        val connection = helper.createLSDConnection()

        val nextOrderIdStatement = connection.prepareFutureStatement(nextOrderId)
        nextOrderIdStatement.setInt(1, 1)
        nextOrderIdStatement.setInt(2, 1)

        val result = nextOrderIdStatement.executeFutureQuery()

        val condition = connection.isTrue { result.getFutureInt(1).resolve() == 0 }

        condition.whenTrue {
            fail("Condition should be true!")
        }.whenFalse {
            assert(true)
        }

        connection.commit()
    }
}