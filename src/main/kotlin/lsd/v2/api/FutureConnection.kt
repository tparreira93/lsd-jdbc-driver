package lsd.v2.api

import java.sql.Connection

interface FutureConnection: Connection {
    fun prepareFutureStatement(sql: String?): FutureStatement

    fun prepareFutureStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int): FutureStatement

    fun prepareFutureStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): FutureStatement

    fun isTrue(condition: String): FutureStatementCondition
}