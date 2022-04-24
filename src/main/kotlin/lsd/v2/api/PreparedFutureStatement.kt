package lsd.v2.api

import java.sql.PreparedStatement

interface PreparedFutureStatement : PreparedStatement, FutureStatement {
    fun setFutureInt(parameterIndex: Int, x: Future<Int>)
    fun setFutureDouble(parameterIndex: Int, x: Future<Double>)
    fun setFutureFloat(parameterIndex: Int, x: Future<Float>)
    fun setFutureString(parameterIndex: Int, x: Future<String>)
    fun setFutureObject(parameterIndex: Int, x: Future<Any>)

    fun addFutureBatch()
    fun executeFutureBatch()
}