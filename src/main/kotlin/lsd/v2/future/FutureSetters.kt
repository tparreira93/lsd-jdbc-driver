package lsd.v2.future

import java.sql.PreparedStatement
import java.sql.Timestamp

abstract class BaseFutureSetter<T>(val preparedStatement: PreparedStatement, val future: Future<T>) : Future<Unit>
abstract class IndexFutureSetter<T>(preparedStatement: PreparedStatement, val columnIndex: Int, future: Future<T>) : BaseFutureSetter<T>(preparedStatement, future)

class FutureSetIntIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<Int>) : IndexFutureSetter<Int>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setInt(columnIndex, future.resolve())
    }
}
class FutureSetStringIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<String>) : IndexFutureSetter<String>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setString(columnIndex, future.resolve())
    }
}

class FutureSetLongIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<Long>) : IndexFutureSetter<Long>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setLong(columnIndex, future.resolve())
    }
}

class FutureSetFloatIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<Float>) : IndexFutureSetter<Float>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setFloat(columnIndex, future.resolve())
    }
}
class FutureSetDoubleIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<Double>) : IndexFutureSetter<Double>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setDouble(columnIndex, future.resolve())
    }
}
class FutureSetObjectIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<Any>) : IndexFutureSetter<Any>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setObject(columnIndex, future.resolve())
    }
}

class FutureSetTimestampIndex(preparedStatement: PreparedStatement, columnIndex: Int, future: Future<Timestamp>) : IndexFutureSetter<Timestamp>(preparedStatement, columnIndex, future) {
    override fun resolve() {
        preparedStatement.setTimestamp(columnIndex, future.resolve())
    }
}