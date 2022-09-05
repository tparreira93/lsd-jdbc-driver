package trxsys.lsd.api

import trxsys.lsd.future.Future

interface FutureStatementCondition : FutureCondition {
    fun setInt(index: Int, x: Int)
    fun setFloat(index: Int, x: Float)
    fun setString(index: Int, x: String)
    fun setDouble(index: Int, x: Double)

    fun setFutureInt(parameterIndex: Int, x: Future<Int>)
    fun setFutureDouble(parameterIndex: Int, x: Future<Double>)
    fun setFutureFloat(parameterIndex: Int, x: Future<Float>)
    fun setFutureString(parameterIndex: Int, x: Future<String>)
}