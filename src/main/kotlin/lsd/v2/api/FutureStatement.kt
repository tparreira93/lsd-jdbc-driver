package lsd.v2.api

import java.sql.Statement

interface FutureStatement : Statement, Future<Boolean> {
    fun executeFutureQuery(): FutureResultSet

    fun executeFutureUpdate()

    fun then(future: () -> Unit)

    fun abort(exception: java.lang.Exception)
}