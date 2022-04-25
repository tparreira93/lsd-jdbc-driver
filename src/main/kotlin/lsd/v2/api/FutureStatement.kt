package lsd.v2.api

import java.sql.Statement

interface FutureStatement : Statement, Future<Any> {
    fun executeFutureQuery(): FutureResultSet

    fun executeFutureUpdate()

    fun afterQueryExecution(f: (OperationResultSet) -> Unit)

    fun afterUpdateExecution(f: (UpdateOperationResult) -> Unit)

    fun afterBatchExecution(f: (BatchOperationResult) -> Unit)
}