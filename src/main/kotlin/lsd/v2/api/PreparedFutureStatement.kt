package lsd.v2.api

import java.sql.PreparedStatement
import java.sql.Timestamp

interface PreparedFutureStatement : PreparedStatement, FutureStatement {
    fun setFutureInt(parameterIndex: Int, x: Future<Int>)
    fun setFutureDouble(parameterIndex: Int, x: Future<Double>)
    fun setFutureFloat(parameterIndex: Int, x: Future<Float>)
    fun setFutureString(parameterIndex: Int, x: Future<String>)
    fun setFutureObject(parameterIndex: Int, x: Future<Any>)
    fun setFutureTimestamp(parameterIndex: Int, x: Future<Timestamp>)

    fun addFutureBatch()
    fun executeFutureBatch()
}