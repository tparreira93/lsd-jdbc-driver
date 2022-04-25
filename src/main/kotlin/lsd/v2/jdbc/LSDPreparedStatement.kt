package lsd.v2.jdbc

import lsd.v2.RollbackException
import lsd.v2.api.*
import lsd.v2.util.ParameterList
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Date
import java.util.*
import kotlin.collections.ArrayList

class LSDPreparedStatement(private val lsdConnection: LSDConnection, private val backingStatement: PreparedStatement) :
    PreparedFutureStatement,
    PreparedStatement {
    private var future: Future<*>? = null
    private var executed: Boolean = false
    private var isBatched = false
    private var currentParameters: ParameterList = ParameterList()
    private val parameters = ArrayList<ParameterList>()
    private var futureResultSet: FutureResultSet? = null

    private var afterQueryExec: ((res: OperationResultSet) -> Unit)? = null
    private var afterBatchExec: ((res: BatchOperationResult) -> Unit)? = null
    private var afterUpdateExec: ((res: UpdateOperationResult) -> Unit)? = null

    override fun toString(): String {
        return backingStatement.toString()
    }

    private fun executeAfterOp(result: Any?) {
        when (val opResult = OperationResult.fromResult(result)) {
            is OperationResultSet -> {
                afterQueryExec?.invoke(opResult)
            }
            is BatchOperationResult -> {
                afterBatchExec?.invoke(opResult)
            }
            is UpdateOperationResult -> {
                afterUpdateExec?.invoke(opResult)
            }
        }
    }

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

    private fun prepareExec(f: () -> Any): Any {
        prepareResolve()
        val result = f.invoke()
        afterResolve(result)

        return result
    }

    private fun prepareResolve() {
        resolveParameters()
    }

    private fun afterResolve(result: Any) {
        if (hasOpenResultSet()) {
            if (!backingStatement.resultSet.next())
                backingStatement.resultSet.close()
        }

        executeAfterOp(result)

        if (lsdConnection.hasRolledBack()) {
            throw RollbackException()
        }
    }

    override fun resolve(): Any {
        return future!!.resolve()!!
    }

    override fun dispose() {
        if (hasOpenResultSet()) {
            resultSet.close()
        }
        future = null
        executed = false
        afterQueryExec = null
        afterBatchExec = null
        afterUpdateExec = null

        for (p in parameters) {
            p.clear()
        }

        if (isBatched) {
            backingStatement.clearParameters()
            backingStatement.clearBatch()
        }
    }

    override fun addFutureBatch() {
        isBatched = true
        parameters.add(currentParameters)
        currentParameters = ParameterList()
    }

    override fun executeFutureQuery(): FutureResultSet {
        if (future != null) {
            return futureResultSet!!
        }

        future = CachedFuture { prepareExec { backingStatement.executeQuery() } }
        lsdConnection.addFutureStatement(this)
        futureResultSet = LSDResultSet(this)

        return futureResultSet!!
    }

    override fun executeFutureUpdate() {
        if (future != null) {
            return
        }

        future = CachedFuture { prepareExec { backingStatement.executeUpdate() } }
        lsdConnection.addFutureStatement(this)
    }

    override fun executeFutureBatch() {
        if (future != null) {
            return
        }
        future = CachedFuture { prepareExec { backingStatement.executeBatch() } }
        lsdConnection.addFutureStatement(this)
    }

    override fun addBatch() {
        backingStatement.addBatch()
    }

    override fun addBatch(sql: String?) {
        backingStatement.addBatch(sql)
    }

    override fun clearBatch() {
        backingStatement.clearBatch()
    }

    override fun executeBatch(): IntArray {
        return backingStatement.executeBatch()
    }

    private fun hasOpenResultSet(): Boolean {
        return backingStatement.resultSet != null && !backingStatement.resultSet?.isClosed!!
    }

    override fun executeQuery(): ResultSet {
        return backingStatement.executeQuery()
    }

    override fun afterQueryExecution(f: (OperationResultSet) -> Unit) {
        afterQueryExec = f
    }

    override fun afterUpdateExecution(f: (UpdateOperationResult) -> Unit) {
        afterUpdateExec = f
    }

    override fun afterBatchExecution(f: (BatchOperationResult) -> Unit) {
        afterBatchExec = f
    }

    private fun addParameter(f: () -> Unit) {
        addParameter(f, false)
    }

    private fun addParameter(f: () -> Unit, realFuture: Boolean) {
        if (isBatched || realFuture) {
            currentParameters.add(CachedFuture(f))
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

    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        TODO("Not yet implemented")
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        TODO("Not yet implemented")
    }

    override fun close() {
        backingStatement.close()
    }

    override fun executeQuery(sql: String?): ResultSet {
        return backingStatement.executeQuery(sql)
    }

    override fun executeUpdate(): Int {
        return backingStatement.executeUpdate()
    }

    override fun executeUpdate(sql: String?): Int {
        return backingStatement.executeUpdate(sql)
    }

    override fun executeUpdate(sql: String?, autoGeneratedKeys: Int): Int {
        return backingStatement.executeUpdate(sql, autoGeneratedKeys)
    }

    override fun executeUpdate(sql: String?, columnIndexes: IntArray?): Int {
        return backingStatement.executeUpdate(sql, columnIndexes)
    }

    override fun executeUpdate(sql: String?, columnNames: Array<out String>?): Int {
        return backingStatement.executeUpdate(sql, columnNames)
    }

    override fun getMaxFieldSize(): Int {
        return backingStatement.maxFieldSize
    }

    override fun setMaxFieldSize(max: Int) {
        backingStatement.maxFieldSize = max
    }

    override fun getMaxRows(): Int {
        return backingStatement.maxRows
    }

    override fun setMaxRows(max: Int) {
        backingStatement.maxRows = maxRows
    }

    override fun setEscapeProcessing(enable: Boolean) {
        backingStatement.setEscapeProcessing(enable)
    }

    override fun getQueryTimeout(): Int {
        return backingStatement.queryTimeout
    }

    override fun setQueryTimeout(seconds: Int) {
        backingStatement.queryTimeout = seconds
    }

    override fun cancel() {
        backingStatement.cancel()
    }

    override fun getWarnings(): SQLWarning {
        return backingStatement.warnings
    }

    override fun clearWarnings() {
        backingStatement.clearWarnings()
    }

    override fun setCursorName(name: String?) {
        backingStatement.setCursorName(name)
    }

    override fun execute(): Boolean {
        return backingStatement.execute()
    }

    override fun execute(sql: String?): Boolean {
        return backingStatement.execute(sql)
    }

    override fun execute(sql: String?, autoGeneratedKeys: Int): Boolean {
        return backingStatement.execute(sql, autoGeneratedKeys)
    }

    override fun execute(sql: String?, columnIndexes: IntArray?): Boolean {
        return backingStatement.execute(sql, columnIndexes)
    }

    override fun execute(sql: String?, columnNames: Array<out String>?): Boolean {
        return backingStatement.execute(sql, columnNames)
    }

    override fun getResultSet(): ResultSet {
        return backingStatement.resultSet
    }

    override fun getUpdateCount(): Int {
        return backingStatement.updateCount
    }

    override fun getMoreResults(): Boolean {
        return backingStatement.moreResults
    }

    override fun getMoreResults(current: Int): Boolean {
        return backingStatement.moreResults
    }

    override fun setFetchDirection(direction: Int) {
        backingStatement.fetchDirection = direction
    }

    override fun getFetchDirection(): Int {
        return backingStatement.fetchDirection
    }

    override fun setFetchSize(rows: Int) {
        backingStatement.fetchDirection = rows
    }

    override fun getFetchSize(): Int {
        return backingStatement.fetchSize
    }

    override fun getResultSetConcurrency(): Int {
        return backingStatement.resultSetConcurrency
    }

    override fun getResultSetType(): Int {
        return backingStatement.resultSetType
    }

    override fun getConnection(): Connection {
        return backingStatement.connection
    }

    override fun getGeneratedKeys(): ResultSet {
        return backingStatement.generatedKeys
    }

    override fun getResultSetHoldability(): Int {
        return backingStatement.resultSetHoldability
    }

    override fun isClosed(): Boolean {
        return backingStatement.isClosed
    }

    override fun setPoolable(poolable: Boolean) {
        backingStatement.isPoolable = poolable
    }

    override fun isPoolable(): Boolean {
        return backingStatement.isPoolable
    }

    override fun closeOnCompletion() {
        backingStatement.closeOnCompletion()
    }

    override fun isCloseOnCompletion(): Boolean {
        return backingStatement.isCloseOnCompletion
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