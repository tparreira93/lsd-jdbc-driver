package trxsys.lsd.api

import trxsys.lsd.future.Future
import java.sql.Statement

interface FutureStatement : Statement, Future<Any> {
    fun addFutureBatch(sql: String)
    fun executeFutureQuery(sql: String): FutureResultSet
    fun executeFutureUpdate(sql: String): FutureResultConsumer<Int>
    fun executeFutureBatch(): FutureResultConsumer<IntArray>
}