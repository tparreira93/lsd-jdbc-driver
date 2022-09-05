package lsd.v2.future

import lsd.v2.api.FutureResultSet
import java.sql.Timestamp

abstract class BaseFutureGetter<T>(val futureResultSet: FutureResultSet) : Future<T>
abstract class IndexFutureGetter<T>(futureResultSet: FutureResultSet, val columnIndex: Int) : BaseFutureGetter<T>(futureResultSet)
abstract class NameFutureGetter<T>(futureResultSet: FutureResultSet, val columnName: String) : BaseFutureGetter<T>(futureResultSet)

class FutureGetIntIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<Int>(futureResultSet, columnIndex) {
    override fun resolve(): Int {
        return futureResultSet.resolve().getInt(columnIndex)
    }
}
class FutureGetIntName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<Int>(futureResultSet, columnName) {
    override fun resolve(): Int {
        return futureResultSet.resolve().getInt(columnName)
    }
}

class FutureGetStringIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<String>(futureResultSet, columnIndex) {
    override fun resolve(): String {
        return futureResultSet.resolve().getString(columnIndex)
    }
}
class FutureGetStringName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<String>(futureResultSet, columnName) {
    override fun resolve(): String {
        return futureResultSet.resolve().getString(columnName)
    }
}

class FutureGetLongIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<Long>(futureResultSet, columnIndex) {
    override fun resolve(): Long {
        return futureResultSet.resolve().getLong(columnIndex)
    }
}
class FutureGetLongName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<Long>(futureResultSet, columnName) {
    override fun resolve(): Long {
        return futureResultSet.resolve().getLong(columnName)
    }
}

class FutureGetFloatIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<Float>(futureResultSet, columnIndex) {
    override fun resolve(): Float {
        return futureResultSet.resolve().getFloat(columnIndex)
    }
}
class FutureGetFloatName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<Float>(futureResultSet, columnName) {
    override fun resolve(): Float {
        return futureResultSet.resolve().getFloat(columnName)
    }
}

class FutureGetDoubleIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<Double>(futureResultSet, columnIndex) {
    override fun resolve(): Double {
        return futureResultSet.resolve().getDouble(columnIndex)
    }
}
class FutureGetDoubleName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<Double>(futureResultSet, columnName) {
    override fun resolve(): Double {
        return futureResultSet.resolve().getDouble(columnName)
    }
}

class FutureGetObjectIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<Any>(futureResultSet, columnIndex) {
    override fun resolve(): Any {
        return futureResultSet.resolve().getObject(columnIndex)
    }
}
class FutureGetObjectName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<Any>(futureResultSet, columnName) {
    override fun resolve(): Any {
        return futureResultSet.resolve().getObject(columnName)
    }
}

class FutureGetTimestampIndex(futureResultSet: FutureResultSet, columnIndex: Int) : IndexFutureGetter<Timestamp>(futureResultSet, columnIndex) {
    override fun resolve(): Timestamp {
        return futureResultSet.resolve().getTimestamp(columnIndex)
    }
}
class FutureGetTimestampName(futureResultSet: FutureResultSet, columnName: String) : NameFutureGetter<Timestamp>(futureResultSet, columnName) {
    override fun resolve(): Timestamp {
        return futureResultSet.resolve().getTimestamp(columnName)
    }
}