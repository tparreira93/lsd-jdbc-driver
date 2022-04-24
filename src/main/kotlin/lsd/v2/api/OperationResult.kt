package lsd.v2.api

import java.sql.ResultSet

interface OperationResult<T> {
    fun get(): T

    fun abort(exception: java.lang.Exception) {
        throw exception
    }

    companion object {
        fun fromResult(result: Any?): OperationResult<*>? {
            when (result) {
                is Int -> {
                    return UpdateOperationResult(result)
                }
                is IntArray -> {
                    return BatchOperationResult(result)
                }
                is ResultSet -> {
                    return OperationResultSet(result)
                }
            }

            return null
        }
    }
}

class OperationResultSet(private val resultSet: ResultSet) : OperationResult<ResultSet> {
    override fun get(): ResultSet {
        return resultSet
    }
}

class BatchOperationResult(private val batchResult: IntArray) : OperationResult<IntArray> {
    override fun get(): IntArray {
        return batchResult
    }
}

class UpdateOperationResult(private val updateResult: Int) : OperationResult<Int> {
    override fun get(): Int {
        return updateResult
    }
}