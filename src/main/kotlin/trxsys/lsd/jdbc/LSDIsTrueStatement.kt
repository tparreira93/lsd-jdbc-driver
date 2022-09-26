package trxsys.lsd.jdbc

import trxsys.lsd.api.*
import trxsys.lsd.future.Future

class LSDIsTrueStatement(connection: FutureConnection, condition: String) : FutureStatementCondition {
    private val backingStatement: PreparedFutureStatement = connection.prepareFutureStatement(condition)
    private val futureResultSet = backingStatement.executeFutureQuery()
    private var futureTrueBranch: Runnable? = null
    private var futureFalseBranch: Runnable? = null

    override fun whenTrue(function: Runnable): FutureCondition {
        futureTrueBranch = function

        return this
    }

    override fun whenFalse(function: Runnable): FutureCondition {
        futureFalseBranch = function

        return this
    }

    override fun setInt(index: Int, x: Int) {
        backingStatement.setInt(index, x)
    }

    override fun setFloat(index: Int, x: Float) {
        backingStatement.setFloat(index, x)
    }

    override fun setString(index: Int, x: String) {
        backingStatement.setString(index, x)
    }

    override fun setDouble(index: Int, x: Double) {
        backingStatement.setDouble(index, x)
    }

    override fun setFutureInt(parameterIndex: Int, x: Future<Int>) {
        backingStatement.setFutureInt(parameterIndex, x)
    }

    override fun setFutureDouble(parameterIndex: Int, x: Future<Double>) {
        backingStatement.setFutureDouble(parameterIndex, x)
    }

    override fun setFutureFloat(parameterIndex: Int, x: Future<Float>) {
        backingStatement.setFutureFloat(parameterIndex, x)
    }

    override fun setFutureString(parameterIndex: Int, x: Future<String>) {
        backingStatement.setFutureString(parameterIndex, x)
    }

    override fun resolve(): Boolean {
        val resultSet = futureResultSet.resolve()

        val result = resultSet.getBoolean(1)

        if (result) {
            futureTrueBranch?.run()
        }

        if (!result) {
            futureFalseBranch?.run()
        }

        return result
    }

    override fun dispose() {
        backingStatement.dispose()

        futureTrueBranch = null
        futureFalseBranch = null
    }
}