package trxsys.lsd.jdbc

import trxsys.lsd.api.*
import trxsys.lsd.future.CachedFuture
import trxsys.lsd.future.Future
import trxsys.lsd.future.FutureUtils
import trxsys.lsd.util.ParameterList
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

class LSDPreparedStatement(private val lsdConnection: LSDConnection, private val backingStatement: PreparedStatement)
    : LSDStatement(lsdConnection, backingStatement), PreparedFutureStatement {
    private var future: Future<*>? = null
    private var executed: Boolean = false
    private var isBatched = false
    private var currentParameters: ParameterList = ParameterList()
    private val parameters = ArrayList<ParameterList>()
    private var futureResultSet: FutureResultSet? = null

    private fun resolveParameters() {
        if (isBatched) {
            for (f in parameters) {
                f.resolve()
                backingStatement.addBatch()
            }
        } else {
            currentParameters.resolve()
        }
    }

    private fun <T> prepareExec(f: () -> T): T {
        prepareResolve()

        return f.invoke()
    }

    private fun prepareResolve() {
        resolveParameters()
    }

    override fun resolve(): Any {
        return future!!.resolve()!!
    }

    override fun dispose() {
        super<LSDStatement>.dispose()

        for (p in parameters) {
            p.clear()
        }

        backingStatement.clearParameters()
    }

    override fun addFutureBatch() {
        isBatched = true
        parameters.add(currentParameters)
        currentParameters = ParameterList()
    }

    override fun executeFutureQuery(): FutureResultSet {
        val futureQuery = CachedFuture { prepareExec { backingStatement.executeQuery() } }

        future = futureQuery
        lsdConnection.addFutureStatement(this)
        futureResultSet = LSDResultSet(futureQuery)

        return futureResultSet!!
    }

    override fun executeFutureUpdate(): FutureResultConsumer<Int> {
        val futureUpdate = CachedFuture { prepareExec { backingStatement.executeUpdate() } }
        future = futureUpdate
        lsdConnection.addFutureStatement(this)

        return FutureResultConsumer(futureUpdate)
    }

    override fun executeFutureBatch(): FutureResultConsumer<IntArray> {
        val futureBatch = CachedFuture { prepareExec { backingStatement.executeBatch() } }
        future = futureBatch
        lsdConnection.addFutureStatement(this)

        return FutureResultConsumer(futureBatch)
    }

    override fun addBatch() {
        backingStatement.addBatch()
    }

    override fun addBatch(sql: String?) {
        throw java.lang.UnsupportedOperationException("Operation not supported in FuturePreparedStatement")
    }

    override fun executeBatch(): IntArray {
        throw java.lang.UnsupportedOperationException("Operation not supported in FuturePreparedStatement")
    }

    private fun addParameter(f: () -> Unit) {
        addParameter(f, false)
    }

    private fun addParameter(f: () -> Unit, realFuture: Boolean) {
        if (isBatched || realFuture) {
            currentParameters.add(FutureUtils.newCachedFuture(f))
        } else {
            f.invoke()
        }
    }

    override fun setFutureInt(parameterIndex: Int, x: Future<Int>) {
        addParameter({ backingStatement.setInt(parameterIndex, x.resolve()) }, true)
    }

    override fun setFutureDouble(parameterIndex: Int, x: Future<Double>) {
        addParameter({ backingStatement.setDouble(parameterIndex, x.resolve()) }, true)
    }

    override fun setFutureFloat(parameterIndex: Int, x: Future<Float>) {
        addParameter({ backingStatement.setFloat(parameterIndex, x.resolve()) }, true)
    }

    override fun setFutureString(parameterIndex: Int, x: Future<String>) {
        addParameter({ backingStatement.setString(parameterIndex, x.resolve()) }, true)
    }

    override fun setFutureObject(parameterIndex: Int, x: Future<Any>) {
        addParameter({ backingStatement.setObject(parameterIndex, x.resolve()) }, true)
    }

    override fun setFutureTimestamp(parameterIndex: Int, x: Future<Timestamp>) {
        addParameter({ backingStatement.setObject(parameterIndex, x.resolve()) }, true)
    }

    override fun executeQuery(): ResultSet {
        throw java.lang.UnsupportedOperationException("Operation not supported in FuturePreparedStatement")
    }

    override fun executeUpdate(): Int {
        throw java.lang.UnsupportedOperationException("Operation not supported in FuturePreparedStatement")
    }

    override fun execute(): Boolean {
        throw java.lang.UnsupportedOperationException("Operation not supported in FuturePreparedStatement")
    }

    override fun setNull(parameterIndex: Int, sqlType: Int) {
        addParameter { backingStatement.setNull(parameterIndex, sqlType) }

    }

    override fun setNull(parameterIndex: Int, sqlType: Int, typeName: String?) {
        addParameter { backingStatement.setNull(parameterIndex, sqlType, typeName) }
    }

    override fun setBoolean(parameterIndex: Int, x: Boolean) {
        addParameter { backingStatement.setBoolean(parameterIndex, x) }
    }

    override fun setByte(parameterIndex: Int, x: Byte) {
        addParameter { backingStatement.setByte(parameterIndex, x) }
    }

    override fun setShort(parameterIndex: Int, x: Short) {
        addParameter { backingStatement.setShort(parameterIndex, x) }
    }

    override fun setObject(parameterIndex: Int, x: Any?, targetSqlType: Int) {
        addParameter { backingStatement.setObject(parameterIndex, x, targetSqlType) }
    }

    override fun setObject(parameterIndex: Int, x: Any?) {
        addParameter { backingStatement.setObject(parameterIndex, x) }
    }

    override fun setObject(parameterIndex: Int, x: Any?, targetSqlType: Int, scaleOrLength: Int) {
        addParameter { backingStatement.setObject(parameterIndex, x, targetSqlType, scaleOrLength) }
    }

    override fun setInt(parameterIndex: Int, x: Int) {
        addParameter { backingStatement.setInt(parameterIndex, x) }
    }

    override fun setLong(parameterIndex: Int, x: Long) {
        addParameter { backingStatement.setLong(parameterIndex, x) }
    }

    override fun setFloat(parameterIndex: Int, x: Float) {
        addParameter { backingStatement.setFloat(parameterIndex, x) }
    }

    override fun setDouble(parameterIndex: Int, x: Double) {
        addParameter { backingStatement.setDouble(parameterIndex, x) }
    }

    override fun setBigDecimal(parameterIndex: Int, x: BigDecimal?) {
        addParameter { backingStatement.setBigDecimal(parameterIndex, x) }
    }

    override fun setString(parameterIndex: Int, x: String?) {
        addParameter { backingStatement.setString(parameterIndex, x) }
    }

    override fun setBytes(parameterIndex: Int, x: ByteArray?) {
        addParameter { backingStatement.setBytes(parameterIndex, x) }
    }

    override fun setDate(parameterIndex: Int, x: Date?) {
        addParameter { backingStatement.setDate(parameterIndex, x) }
    }

    override fun setDate(parameterIndex: Int, x: Date?, cal: Calendar?) {
        addParameter { backingStatement.setDate(parameterIndex, x, cal) }
    }

    override fun setTime(parameterIndex: Int, x: Time?) {
        addParameter { backingStatement.setTime(parameterIndex, x) }
    }

    override fun setTime(parameterIndex: Int, x: Time?, cal: Calendar?) {
        addParameter { backingStatement.setTime(parameterIndex, x, cal) }
    }

    override fun setTimestamp(parameterIndex: Int, x: Timestamp?) {
        addParameter { backingStatement.setTimestamp(parameterIndex, x) }
    }

    override fun setTimestamp(parameterIndex: Int, x: Timestamp?, cal: Calendar?) {
        addParameter { backingStatement.setTimestamp(parameterIndex, x, cal) }
    }

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?, length: Int) {
        addParameter { backingStatement.setAsciiStream(parameterIndex, x, length) }
    }

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?, length: Long) {
        addParameter { backingStatement.setAsciiStream(parameterIndex, x, length) }
    }

    override fun setAsciiStream(parameterIndex: Int, x: InputStream?) {
        addParameter { backingStatement.setAsciiStream(parameterIndex, x) }
    }

    override fun setUnicodeStream(parameterIndex: Int, x: InputStream?, length: Int) {
        @Suppress("DEPRECATION")
        addParameter { backingStatement.setUnicodeStream(parameterIndex, x, length) }
    }

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?, length: Int) {
        addParameter { backingStatement.setBinaryStream(parameterIndex, x, length) }
    }

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?, length: Long) {
        addParameter { backingStatement.setBinaryStream(parameterIndex, x, length) }
    }

    override fun setBinaryStream(parameterIndex: Int, x: InputStream?) {
        addParameter { backingStatement.setBinaryStream(parameterIndex, x) }
    }

    override fun clearParameters() {
        backingStatement.clearParameters()
    }

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?, length: Int) {
        addParameter { backingStatement.setCharacterStream(parameterIndex, reader, length) }
    }

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?, length: Long) {
        addParameter { backingStatement.setCharacterStream(parameterIndex, reader, length) }
    }

    override fun setCharacterStream(parameterIndex: Int, reader: Reader?) {
        addParameter { backingStatement.setCharacterStream(parameterIndex, reader) }
    }

    override fun setRef(parameterIndex: Int, x: Ref?) {
        addParameter { backingStatement.setRef(parameterIndex, x) }
    }

    override fun setBlob(parameterIndex: Int, x: Blob?) {
        addParameter { backingStatement.setBlob(parameterIndex, x) }
    }

    override fun setBlob(parameterIndex: Int, inputStream: InputStream?, length: Long) {
        addParameter { backingStatement.setBlob(parameterIndex, inputStream, length) }
    }

    override fun setBlob(parameterIndex: Int, inputStream: InputStream?) {
        addParameter { backingStatement.setBlob(parameterIndex, inputStream) }
    }

    override fun setClob(parameterIndex: Int, x: Clob?) {
        addParameter { backingStatement.setClob(parameterIndex, x) }
    }

    override fun setClob(parameterIndex: Int, reader: Reader?, length: Long) {
        addParameter { backingStatement.setClob(parameterIndex, reader, length) }
    }

    override fun setClob(parameterIndex: Int, reader: Reader?) {
        addParameter { backingStatement.setClob(parameterIndex, reader) }
    }

    override fun setArray(parameterIndex: Int, x: java.sql.Array?) {
        addParameter { backingStatement.setArray(parameterIndex, x) }
    }

    override fun getMetaData(): ResultSetMetaData {
        return backingStatement.metaData
    }

    override fun setURL(parameterIndex: Int, x: URL?) {
        addParameter { backingStatement.setURL(parameterIndex, x) }
    }

    override fun getParameterMetaData(): ParameterMetaData {
        return backingStatement.parameterMetaData
    }

    override fun setRowId(parameterIndex: Int, x: RowId?) {
        addParameter { backingStatement.setRowId(parameterIndex, x) }
    }

    override fun setNString(parameterIndex: Int, value: String?) {
        addParameter { backingStatement.setNString(parameterIndex, value) }
    }

    override fun setNCharacterStream(parameterIndex: Int, value: Reader?, length: Long) {
        addParameter { backingStatement.setNCharacterStream(parameterIndex, value, length) }
    }

    override fun setNCharacterStream(parameterIndex: Int, value: Reader?) {
        addParameter { backingStatement.setNCharacterStream(parameterIndex, value) }
    }

    override fun setNClob(parameterIndex: Int, value: NClob?) {
        addParameter { backingStatement.setNClob(parameterIndex, value) }
    }

    override fun setNClob(parameterIndex: Int, reader: Reader?, length: Long) {
        addParameter { backingStatement.setNClob(parameterIndex, reader, length) }
    }

    override fun setNClob(parameterIndex: Int, reader: Reader?) {
        addParameter { backingStatement.setNClob(parameterIndex, reader) }
    }

    override fun setSQLXML(parameterIndex: Int, xmlObject: SQLXML?) {
        addParameter { backingStatement.setSQLXML(parameterIndex, xmlObject) }
    }
}