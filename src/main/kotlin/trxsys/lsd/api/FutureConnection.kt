package trxsys.lsd.api

import java.sql.Connection

interface FutureConnection: Connection {
    fun prepareFutureStatement(sql: String?): PreparedFutureStatement

    fun prepareFutureStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int): PreparedFutureStatement

    fun prepareFutureStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): PreparedFutureStatement

    fun isTrue(condition: () -> Boolean) : FutureCondition
    fun isTrue(condition: String): FutureStatementCondition
}