package lsd.v2.jdbc

import lsd.v2.api.*

class LSDIsTrue(private val backingStatement: PreparedFutureStatement) : FutureStatementCondition {
    private var futureTrueBranch: Future<Unit>? = null
    private var futureFalseBranch: Future<Unit>? = null

    override fun whenTrue(future: () -> Unit): FutureCondition {
        futureTrueBranch = FutureRunner { future.invoke() }

        return this
    }

    override fun whenFalse(future: () -> Unit): FutureCondition {
        futureFalseBranch = FutureRunner { future.invoke() }

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
        backingStatement.resolve()

        val result = if (!backingStatement.resultSet.isClosed) {
            backingStatement.resultSet.getBoolean(1)
        } else {
            false
        }

        if (result) {
            futureTrueBranch?.resolve()
        }

        if (!result) {
            futureFalseBranch?.resolve()
        }

        return result
    }

    override fun dispose() {
        backingStatement.dispose()
        futureTrueBranch?.dispose()
        futureFalseBranch?.dispose()

        futureTrueBranch = null
        futureFalseBranch = null
    }
}