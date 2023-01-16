package trxsys.lsd.jdbc

import trxsys.lsd.RollbackException
import trxsys.lsd.StopExecution
import trxsys.lsd.api.*
import trxsys.lsd.future.Future
import java.sql.*
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class LSDConnection(private val connection: Connection) : FutureConnection {
    private var connectionRollback: Boolean = false
    private val futures = ArrayList<Future<*>>()
    private var stop = false
    private var rollback = false

    private fun cleanUp() {
        for (statements in futures) {
            statements.dispose()
        }
        futures.clear()
        connectionRollback = false
    }

    fun addFuture(future: Future<*>) {
        futures.add(future)
    }

    private fun futurePreparedStatement(statement: PreparedStatement): PreparedFutureStatement {
        return LSDPreparedStatement(this, statement)
    }

    override fun prepareFutureStatement(sql: String?): PreparedFutureStatement {
        return futurePreparedStatement(prepareStatement(sql))
    }

    override fun prepareFutureStatement(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int
    ): PreparedFutureStatement {
        return futurePreparedStatement(connection.prepareStatement(sql, resultSetType, resultSetConcurrency))
    }

    override fun prepareFutureStatement(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): PreparedFutureStatement {
        return futurePreparedStatement(
            connection.prepareStatement(
                sql,
                resultSetType,
                resultSetConcurrency,
                resultSetHoldability
            )
        )
    }

    override fun isTrue(condition: Invariant): FutureCondition {
        val future = LSDInvariant(condition)
        addFuture(future)
        return future
    }

    override fun isTrue(condition: () -> Boolean): FutureCondition {
        val future = LSDIsTrue(condition)
        addFuture(future)
        return future
    }

    override fun isTrue(condition: String): FutureStatementCondition {
        val future = LSDIsTrueStatement(this, "SELECT $condition;")
        addFuture(future)
        return future
    }

    override fun prepareStatement(sql: String?): PreparedStatement {
        return connection.prepareStatement(sql)
    }

    override fun prepareStatement(sql: String?, resultSetType: Int, resultSetConcurrency: Int): PreparedStatement {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency)
    }

    override fun prepareStatement(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): PreparedStatement {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability)
    }

    override fun close() {
        if (!connection.isClosed) {
            futures.clear()
            connection.close()
        }
    }

    override fun abort(executor: Executor?) {
        connection.abort(executor)
    }

    override fun stop() {
        stop = true;
    }

    override fun stopAndRollback() {
        stop = true
        rollback = true
    }

    override fun commit() {
        try {
            var i = 0
            var size = futures.size

            while (i < size) {
                if (stop) break

                futures[i++].resolve()
                size = futures.size

                if (hasRolledBack()) {
                    throw RollbackException()
                }
            }
            if (!rollback)
                connection.commit()
        } catch (e: StopExecution){
            // ignore
        } catch (e: RollbackException){
            // ignore
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
            cleanUp()
        }
    }

    override fun setAutoCommit(autoCommit: Boolean) {
        connection.autoCommit = autoCommit
    }

    override fun getAutoCommit(): Boolean {
        return connection.autoCommit
    }

    override fun rollback() {
        connectionRollback = true
        connection.rollback()
    }

    override fun rollback(savepoint: Savepoint?) {
        connectionRollback = true
        connection.rollback(savepoint)
    }

    override fun <T : Any?> unwrap(iface: Class<T>?): T {
        if (iface!!.isAssignableFrom(javaClass)) {
            return iface.cast(this)
        }
        throw SQLException("Cannot unwrap to " + iface.name)
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean {
        return iface!!.isAssignableFrom(javaClass)
    }

    override fun createStatement(): Statement {
        return connection.createStatement()
    }

    override fun createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement {
        return connection.createStatement(resultSetType, resultSetConcurrency)
    }

    override fun createStatement(resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): Statement {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability)
    }

    override fun prepareStatement(sql: String?, autoGeneratedKeys: Int): PreparedStatement {
        return connection.prepareStatement(sql, autoGeneratedKeys)
    }

    override fun prepareStatement(sql: String?, columnIndexes: IntArray?): PreparedStatement {
        return connection.prepareStatement(sql, columnIndexes)
    }

    override fun prepareStatement(sql: String?, columnNames: Array<out String>?): PreparedStatement {
        return connection.prepareStatement(sql, columnNames)
    }

    override fun prepareCall(sql: String?): CallableStatement {
        return connection.prepareCall(sql)
    }

    override fun prepareCall(sql: String?, resultSetType: Int, resultSetConcurrency: Int): CallableStatement {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency)
    }

    override fun prepareCall(
        sql: String?,
        resultSetType: Int,
        resultSetConcurrency: Int,
        resultSetHoldability: Int
    ): CallableStatement {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability)
    }

    override fun nativeSQL(sql: String?): String {
        return connection.nativeSQL(sql)
    }

    override fun isClosed(): Boolean {
        return connection.isClosed
    }

    override fun getMetaData(): DatabaseMetaData {
        return connection.metaData
    }

    override fun setReadOnly(readOnly: Boolean) {
        connection.isReadOnly = readOnly
    }

    override fun isReadOnly(): Boolean {
        return connection.isReadOnly
    }

    override fun setCatalog(catalog: String?) {
        connection.catalog = catalog
    }

    override fun getCatalog(): String {
        return connection.catalog
    }

    override fun setTransactionIsolation(level: Int) {
        connection.transactionIsolation = level
    }

    override fun getTransactionIsolation(): Int {
        return connection.transactionIsolation
    }

    override fun getWarnings(): SQLWarning {
        return connection.warnings
    }

    override fun clearWarnings() {
        connection.clearWarnings()
    }

    override fun getTypeMap(): MutableMap<String, Class<*>> {
        return connection.typeMap
    }

    override fun setTypeMap(map: MutableMap<String, Class<*>>?) {
        connection.typeMap = typeMap
    }

    override fun setHoldability(holdability: Int) {
        connection.holdability = holdability
    }

    override fun getHoldability(): Int {
        return connection.holdability
    }

    override fun setSavepoint(): Savepoint {
        return connection.setSavepoint()
    }

    override fun setSavepoint(name: String?): Savepoint {
        return connection.setSavepoint(name)
    }

    override fun releaseSavepoint(savepoint: Savepoint?) {
        connection.releaseSavepoint(savepoint)
    }

    override fun createClob(): Clob {
        return connection.createClob()
    }

    override fun createBlob(): Blob {
        return connection.createBlob()
    }

    override fun createNClob(): NClob {
        return connection.createNClob()
    }

    override fun createSQLXML(): SQLXML {
        return connection.createSQLXML()
    }

    override fun isValid(timeout: Int): Boolean {
        return connection.isValid(timeout)
    }

    override fun setClientInfo(name: String?, value: String?) {
        connection.setClientInfo(name, value)
    }

    override fun setClientInfo(properties: Properties?) {
        connection.clientInfo = properties
    }

    override fun getClientInfo(name: String?): String {
        return connection.getClientInfo(name)
    }

    override fun getClientInfo(): Properties {
        return connection.clientInfo
    }

    override fun createArrayOf(typeName: String?, elements: Array<out Any>?): java.sql.Array {
        return connection.createArrayOf(typeName, elements)
    }

    override fun createStruct(typeName: String?, attributes: Array<out Any>?): Struct {
        return connection.createStruct(typeName, attributes)
    }

    override fun setSchema(schema: String?) {
        connection.schema = schema
    }

    override fun getSchema(): String {
        return connection.schema
    }

    override fun setNetworkTimeout(executor: Executor?, milliseconds: Int) {
        connection.setNetworkTimeout(executor, milliseconds)
    }

    override fun getNetworkTimeout(): Int {
        return connection.networkTimeout
    }

    fun hasRolledBack(): Boolean {
        return connectionRollback
    }
}