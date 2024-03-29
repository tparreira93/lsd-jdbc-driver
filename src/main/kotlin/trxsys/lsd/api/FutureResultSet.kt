package trxsys.lsd.api

import trxsys.lsd.future.Future
import trxsys.lsd.future.ResultChain
import java.sql.ResultSet
import java.sql.Timestamp

interface FutureResultSet: Future<ResultSet>, ResultChain<ResultSet>, ResultSet {
    fun getFutureObject(columnIndex: Int): Future<Any>

    fun getFutureObject(columnLabel: String?): Future<Any>

    fun getFutureString(columnIndex: Int): Future<String>

    fun getFutureString(columnLabel: String?): Future<String>

    fun getFutureInt(columnIndex: Int): Future<Int>

    fun getFutureInt(columnLabel: String?): Future<Int>

    fun getFutureLong(columnIndex: Int): Future<Long>

    fun getFutureLong(columnLabel: String?): Future<Long>

    fun getFutureFloat(columnIndex: Int): Future<Float>

    fun getFutureFloat(columnLabel: String?): Future<Float>

    fun getFutureDouble(columnIndex: Int): Future<Double>

    fun getFutureDouble(columnLabel: String?): Future<Double>

    fun getFutureTimestamp(columnIndex: Int): Future<Timestamp>

    fun getFutureTimestamp(columnLabel: String?): Future<Timestamp>

    fun ifEmpty(function: Runnable)
}