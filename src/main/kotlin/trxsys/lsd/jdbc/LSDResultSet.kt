package trxsys.lsd.jdbc

import trxsys.lsd.api.*
import trxsys.lsd.future.*
import java.io.InputStream
import java.io.Reader
import java.math.BigDecimal
import java.net.URL
import java.sql.*
import java.sql.Array
import java.sql.Date
import java.util.*
import java.util.function.Consumer

class LSDResultSet(private val futureResultSet: Future<ResultSet>) : FutureResultSet {
    private var resultSet: ResultSet? = null
    private var first: Boolean = false
    private var hasResults: Boolean = false
    private var consumer: Consumer<ResultSet>? = null
    private var whenEmpty: Runnable? = null
    override fun resolve(): ResultSet {
        resultSet = futureResultSet.resolve()
        if (!first) {
            if (resultSet == null)
                throw NullPointerException("Result set has not been properly resolved!")
            hasResults = resultSet!!.next()
            first = true

            if (!hasResults) {
                whenEmpty?.run()
            }

            consumer?.accept(resultSet!!)
        }
        return resultSet!!
    }

    override fun then(function: Consumer<ResultSet>) {
        this.consumer = function
    }

    override fun ifEmpty(function: Runnable) {
        this.whenEmpty = function
    }

    override fun getFutureInt(columnIndex: Int): Future<Int> {
        return FutureGetIntIndex(this, columnIndex)
    }

    override fun getFutureInt(columnLabel: String?): Future<Int> {
        return FutureGetIntName(this, columnLabel!!)
    }

    override fun getFutureLong(columnIndex: Int): Future<Long> {
        return FutureGetLongIndex(this, columnIndex)
    }

    override fun getFutureLong(columnLabel: String?): Future<Long> {
        return FutureGetLongName(this, columnLabel!!)
    }

    override fun getFutureFloat(columnIndex: Int): Future<Float> {
        return FutureGetFloatIndex(this, columnIndex)
    }

    override fun getFutureFloat(columnLabel: String?): Future<Float> {
        return FutureGetFloatName(this, columnLabel!!)
    }

    override fun getFutureDouble(columnIndex: Int): Future<Double> {
        return FutureGetDoubleIndex(this, columnIndex)
    }

    override fun getFutureDouble(columnLabel: String?): Future<Double> {
        return FutureGetDoubleName(this, columnLabel!!)
    }

    override fun getFutureTimestamp(columnIndex: Int): Future<Timestamp> {
        return FutureGetTimestampIndex(this, columnIndex)
    }

    override fun getFutureTimestamp(columnLabel: String?): Future<Timestamp> {
        return FutureGetTimestampName(this, columnLabel!!)
    }

    override fun getFutureString(columnIndex: Int): Future<String> {
        return FutureGetStringIndex(this, columnIndex)
    }

    override fun getFutureString(columnLabel: String?): Future<String> {
        return FutureGetStringName(this, columnLabel!!)
    }

    override fun getFutureObject(columnIndex: Int): Future<Any> {
        return FutureGetObjectIndex(this, columnIndex)
    }

    override fun getFutureObject(columnLabel: String?): Future<Any> {
        return FutureGetObjectName(this, columnLabel!!)
    }

    override fun getObject(columnIndex: Int): Any {
        return resultSet?.getObject(columnIndex)!!
    }

    override fun getObject(columnLabel: String?): Any {
        return resultSet?.getObject(columnLabel)!!
    }

    override fun getString(columnIndex: Int): String {
        return resultSet?.getString(columnIndex)!!
    }

    override fun getString(columnLabel: String?): String {
        return resultSet?.getString(columnLabel)!!
    }

    override fun getInt(columnIndex: Int): Int {
        return resultSet?.getInt(columnIndex)!!
    }

    override fun getInt(columnLabel: String?): Int {
        return resultSet?.getInt(columnLabel)!!
    }

    override fun getLong(columnIndex: Int): Long {
        return resultSet?.getLong(columnIndex)!!
    }

    override fun getLong(columnLabel: String?): Long {
        return resultSet?.getLong(columnLabel)!!
    }

    override fun getFloat(columnIndex: Int): Float {
        return resultSet?.getFloat(columnIndex)!!
    }

    override fun getFloat(columnLabel: String?): Float {
        return resultSet?.getFloat(columnLabel)!!
    }

    override fun getDouble(columnIndex: Int): Double {
        return resultSet?.getDouble(columnIndex)!!
    }

    override fun getDouble(columnLabel: String?): Double {
        return resultSet?.getDouble(columnLabel)!!
    }

    override fun getObject(columnIndex: Int, map: MutableMap<String, Class<*>>?): Any {
        return resultSet?.getObject(columnIndex, map)!!
    }

    override fun getObject(columnLabel: String?, map: MutableMap<String, Class<*>>?): Any {
        return resultSet?.getObject(columnLabel, map)!!
    }

    override fun <T : Any?> getObject(columnIndex: Int, type: Class<T>?): T {
        return resultSet?.getObject(columnIndex, type)!!
    }

    override fun <T : Any?> getObject(columnLabel: String?, type: Class<T>?): T {
        return resultSet?.getObject(columnLabel, type)!!
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

    override fun close() {
        resultSet?.close()
    }

    override fun next(): Boolean {
        return resultSet?.next()!!
    }

    override fun wasNull(): Boolean {
        return resultSet?.wasNull()!!
    }

    override fun getBoolean(columnIndex: Int): Boolean {
        return resultSet?.getBoolean(columnIndex)!!
    }

    override fun getBoolean(columnLabel: String?): Boolean {
        return resultSet?.getBoolean(columnLabel)!!
    }

    override fun getByte(columnIndex: Int): Byte {
        return resultSet?.getByte(columnIndex)!!
    }

    override fun getByte(columnLabel: String?): Byte {
        return resultSet?.getByte(columnLabel)!!
    }

    override fun getShort(columnIndex: Int): Short {
        return resultSet?.getShort(columnIndex)!!
    }

    override fun getShort(columnLabel: String?): Short {
        return resultSet?.getShort(columnLabel)!!
    }

    override fun getBigDecimal(columnIndex: Int, scale: Int): BigDecimal {
        return resultSet?.getBigDecimal(columnIndex, scale)!!
    }

    override fun getBigDecimal(columnLabel: String?, scale: Int): BigDecimal {
        return resultSet?.getBigDecimal(columnLabel, scale)!!
    }

    override fun getBigDecimal(columnIndex: Int): BigDecimal {
        return resultSet?.getBigDecimal(columnIndex)!!
    }

    override fun getBigDecimal(columnLabel: String?): BigDecimal {
        return resultSet?.getBigDecimal(columnLabel)!!
    }

    override fun getBytes(columnIndex: Int): ByteArray {
        return resultSet?.getBytes(columnIndex)!!
    }

    override fun getBytes(columnLabel: String?): ByteArray {
        return resultSet?.getBytes(columnLabel)!!
    }

    override fun getDate(columnIndex: Int): Date {
        return resultSet?.getDate(columnIndex)!!
    }

    override fun getDate(columnLabel: String?): Date {
        return resultSet?.getDate(columnLabel)!!
    }

    override fun getDate(columnIndex: Int, cal: Calendar?): Date {
        return resultSet?.getDate(columnIndex, cal)!!
    }

    override fun getDate(columnLabel: String?, cal: Calendar?): Date {
        return resultSet?.getDate(columnLabel, cal)!!
    }

    override fun getTime(columnIndex: Int): Time {
        return resultSet?.getTime(columnIndex)!!
    }

    override fun getTime(columnLabel: String?): Time {
        return resultSet?.getTime(columnLabel)!!
    }

    override fun getTime(columnIndex: Int, cal: Calendar?): Time {
        return resultSet?.getTime(columnIndex, cal)!!
    }

    override fun getTime(columnLabel: String?, cal: Calendar?): Time {
        return resultSet?.getTime(columnLabel, cal)!!
    }

    override fun getTimestamp(columnIndex: Int): Timestamp {
        return resultSet?.getTimestamp(columnIndex)!!
    }

    override fun getTimestamp(columnLabel: String?): Timestamp {
        return resultSet?.getTimestamp(columnLabel)!!
    }

    override fun getTimestamp(columnIndex: Int, cal: Calendar?): Timestamp {
        return resultSet?.getTimestamp(columnIndex, cal)!!
    }

    override fun getTimestamp(columnLabel: String?, cal: Calendar?): Timestamp {
        return resultSet?.getTimestamp(columnLabel, cal)!!
    }

    override fun getAsciiStream(columnIndex: Int): InputStream {
        return resultSet?.getAsciiStream(columnIndex)!!
    }

    override fun getAsciiStream(columnLabel: String?): InputStream {
        return resultSet?.getAsciiStream(columnLabel)!!
    }

    override fun getUnicodeStream(columnIndex: Int): InputStream {
        return resultSet?.getUnicodeStream(columnIndex)!!
    }

    override fun getUnicodeStream(columnLabel: String?): InputStream {
        return resultSet?.getUnicodeStream(columnLabel)!!
    }

    override fun getBinaryStream(columnIndex: Int): InputStream {
        return resultSet?.getBinaryStream(columnIndex)!!
    }

    override fun getBinaryStream(columnLabel: String?): InputStream {
        return resultSet?.getBinaryStream(columnLabel)!!
    }

    override fun getWarnings(): SQLWarning {
        return resultSet?.warnings!!
    }

    override fun clearWarnings() {
        resultSet?.clearWarnings()
    }

    override fun getCursorName(): String {
        return resultSet?.cursorName!!
    }

    override fun getMetaData(): ResultSetMetaData {
        return resultSet?.metaData!!
    }

    override fun findColumn(columnLabel: String?): Int {
        return resultSet?.findColumn(columnLabel)!!
    }

    override fun getCharacterStream(columnIndex: Int): Reader {
        return resultSet?.getCharacterStream(columnIndex)!!
    }

    override fun getCharacterStream(columnLabel: String?): Reader {
        return resultSet?.getCharacterStream(columnLabel)!!
    }

    override fun isBeforeFirst(): Boolean {
        return resultSet?.isBeforeFirst!!
    }

    override fun isAfterLast(): Boolean {
        return resultSet?.isAfterLast!!
    }

    override fun isFirst(): Boolean {
        return resultSet?.isFirst!!
    }

    override fun isLast(): Boolean {
        return resultSet?.isLast!!
    }

    override fun beforeFirst() {
        resultSet?.beforeFirst()
    }

    override fun afterLast() {
        resultSet?.afterLast()
    }

    override fun first(): Boolean {
        return resultSet?.first()!!
    }

    override fun last(): Boolean {
        return resultSet?.last()!!
    }

    override fun getRow(): Int {
        return resultSet?.row!!
    }

    override fun absolute(row: Int): Boolean {
        return resultSet?.absolute(row)!!
    }

    override fun relative(rows: Int): Boolean {
        return resultSet?.relative(rows)!!
    }

    override fun previous(): Boolean {
        return resultSet?.previous()!!
    }

    override fun setFetchDirection(direction: Int) {
        resultSet?.fetchDirection = direction
    }

    override fun getFetchDirection(): Int {
        return resultSet?.fetchDirection!!
    }

    override fun setFetchSize(rows: Int) {
        resultSet?.fetchSize = rows
    }

    override fun getFetchSize(): Int {
        return resultSet?.fetchSize!!
    }

    override fun getType(): Int {
        return resultSet?.type!!
    }

    override fun getConcurrency(): Int {
        return resultSet?.concurrency!!
    }

    override fun rowUpdated(): Boolean {
        return resultSet?.rowUpdated()!!
    }

    override fun rowInserted(): Boolean {
        return resultSet?.rowInserted()!!
    }

    override fun rowDeleted(): Boolean {
        return resultSet?.rowDeleted()!!
    }

    override fun updateNull(columnIndex: Int) {
        resultSet?.updateNull(columnIndex)
    }

    override fun updateNull(columnLabel: String?) {
        return resultSet?.updateNull(columnLabel)!!
    }

    override fun updateBoolean(columnIndex: Int, x: Boolean) {
        resultSet?.updateBoolean(columnIndex, x)
    }

    override fun updateBoolean(columnLabel: String?, x: Boolean) {
        resultSet?.updateBoolean(columnLabel, x)
    }

    override fun updateByte(columnIndex: Int, x: Byte) {
        resultSet?.updateByte(columnIndex, x)
    }

    override fun updateByte(columnLabel: String?, x: Byte) {
        resultSet?.updateByte(columnLabel, x)
    }

    override fun updateShort(columnIndex: Int, x: Short) {
        resultSet?.updateShort(columnIndex, x)
    }

    override fun updateShort(columnLabel: String?, x: Short) {
        resultSet?.updateShort(columnLabel, x)
    }

    override fun updateInt(columnIndex: Int, x: Int) {
        resultSet?.updateInt(columnIndex, x)
    }

    override fun updateInt(columnLabel: String?, x: Int) {
        resultSet?.updateInt(columnLabel, x)
    }

    override fun updateLong(columnIndex: Int, x: Long) {
        resultSet?.updateLong(columnIndex, x)
    }

    override fun updateLong(columnLabel: String?, x: Long) {
        resultSet?.updateLong(columnLabel, x)
    }

    override fun updateFloat(columnIndex: Int, x: Float) {
        resultSet?.updateFloat(columnIndex, x)
    }

    override fun updateFloat(columnLabel: String?, x: Float) {
        resultSet?.updateFloat(columnLabel, x)
    }

    override fun updateDouble(columnIndex: Int, x: Double) {
        resultSet?.updateDouble(columnIndex, x)
    }

    override fun updateDouble(columnLabel: String?, x: Double) {
        resultSet?.updateDouble(columnLabel, x)
    }

    override fun updateBigDecimal(columnIndex: Int, x: BigDecimal?) {
        resultSet?.updateBigDecimal(columnIndex, x)!!
    }

    override fun updateBigDecimal(columnLabel: String?, x: BigDecimal?) {
        resultSet?.updateBigDecimal(columnLabel, x)
    }

    override fun updateString(columnIndex: Int, x: String?) {
        resultSet?.updateString(columnIndex, x)!!
    }

    override fun updateString(columnLabel: String?, x: String?) {
        resultSet?.updateString(columnLabel, x)
    }

    override fun updateBytes(columnIndex: Int, x: ByteArray?) {
        resultSet?.updateBytes(columnIndex, x)!!
    }

    override fun updateBytes(columnLabel: String?, x: ByteArray?) {
        resultSet?.updateBytes(columnLabel, x)
    }

    override fun updateDate(columnIndex: Int, x: Date?) {
        resultSet?.updateDate(columnIndex, x)!!
    }

    override fun updateDate(columnLabel: String?, x: Date?) {
        resultSet?.updateDate(columnLabel, x)
    }

    override fun updateTime(columnIndex: Int, x: Time?) {
        resultSet?.updateTime(columnIndex, x)!!
    }

    override fun updateTime(columnLabel: String?, x: Time?) {
        resultSet?.updateTime(columnLabel, x)
    }

    override fun updateTimestamp(columnIndex: Int, x: Timestamp?) {
        resultSet?.updateTimestamp(columnIndex, x)!!
    }

    override fun updateTimestamp(columnLabel: String?, x: Timestamp?) {
        resultSet?.updateTimestamp(columnLabel, x)
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Int) {
        resultSet?.updateAsciiStream(columnIndex, x, length)!!
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Int) {
        resultSet?.updateAsciiStream(columnLabel, x, length)
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?, length: Long) {
        resultSet?.updateAsciiStream(columnIndex, x, length)!!
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?, length: Long) {
        resultSet?.updateAsciiStream(columnLabel, x, length)!!
    }

    override fun updateAsciiStream(columnIndex: Int, x: InputStream?) {
        resultSet?.updateAsciiStream(columnIndex, x)
    }

    override fun updateAsciiStream(columnLabel: String?, x: InputStream?) {
        resultSet?.updateAsciiStream(columnLabel, x)
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Int) {
        resultSet?.updateBinaryStream(columnIndex, x, length)!!
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Int) {
        resultSet?.updateBinaryStream(columnLabel, x, length)
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?, length: Long) {
        resultSet?.updateBinaryStream(columnIndex, x, length)
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?, length: Long) {
        resultSet?.updateBinaryStream(columnLabel, x, length)
    }

    override fun updateBinaryStream(columnIndex: Int, x: InputStream?) {
        resultSet?.updateBinaryStream(columnIndex, x)
    }

    override fun updateBinaryStream(columnLabel: String?, x: InputStream?) {
        resultSet?.updateBinaryStream(columnLabel, x)
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Int) {
        resultSet?.updateCharacterStream(columnIndex, x, length)
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Int) {
        resultSet?.updateCharacterStream(columnLabel, reader, length)
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        resultSet?.updateCharacterStream(columnIndex, x, length)
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {
        resultSet?.updateCharacterStream(columnLabel, reader, length)
    }

    override fun updateCharacterStream(columnIndex: Int, x: Reader?) {
        resultSet?.updateCharacterStream(columnIndex, x)
    }

    override fun updateCharacterStream(columnLabel: String?, reader: Reader?) {
        resultSet?.updateCharacterStream(columnLabel, reader)
    }

    override fun updateObject(columnIndex: Int, x: Any?, scaleOrLength: Int) {
        resultSet?.updateObject(columnIndex, x, scaleOrLength)
    }

    override fun updateObject(columnIndex: Int, x: Any?) {
        resultSet?.updateObject(columnIndex, x)
    }

    override fun updateObject(columnLabel: String?, x: Any?, scaleOrLength: Int) {
        resultSet?.updateObject(columnLabel, x, scaleOrLength)
    }

    override fun updateObject(columnLabel: String?, x: Any?) {
        resultSet?.updateObject(columnLabel, x)
    }

    override fun insertRow() {
        resultSet?.insertRow()
    }

    override fun updateRow() {
        resultSet?.updateRow()
    }

    override fun deleteRow() {
        resultSet?.deleteRow()
    }

    override fun refreshRow() {
        resultSet?.refreshRow()
    }

    override fun cancelRowUpdates() {
        resultSet?.cancelRowUpdates()
    }

    override fun moveToInsertRow() {
        resultSet?.moveToInsertRow()
    }

    override fun moveToCurrentRow() {
        resultSet?.moveToCurrentRow()
    }

    override fun getStatement(): Statement {
        return resultSet?.statement!!
    }

    override fun getRef(columnIndex: Int): Ref {
        return resultSet?.getRef(columnIndex)!!
    }

    override fun getRef(columnLabel: String?): Ref {
        return resultSet?.getRef(columnLabel)!!
    }

    override fun getBlob(columnIndex: Int): Blob {
        return resultSet?.getBlob(columnIndex)!!
    }

    override fun getBlob(columnLabel: String?): Blob {
        return resultSet?.getBlob(columnLabel)!!
    }

    override fun getClob(columnIndex: Int): Clob {
        return resultSet?.getClob(columnIndex)!!
    }

    override fun getClob(columnLabel: String?): Clob {
        return resultSet?.getClob(columnLabel)!!
    }

    override fun getArray(columnIndex: Int): Array {
        return resultSet?.getArray(columnIndex)!!
    }

    override fun getArray(columnLabel: String?): Array {
        return resultSet?.getArray(columnLabel)!!
    }

    override fun getURL(columnIndex: Int): URL {
        return resultSet?.getURL(columnIndex)!!
    }

    override fun getURL(columnLabel: String?): URL {
        return resultSet?.getURL(columnLabel)!!
    }

    override fun updateRef(columnIndex: Int, x: Ref?) {
        resultSet?.updateRef(columnIndex, x)
    }

    override fun updateRef(columnLabel: String?, x: Ref?) {
        resultSet?.updateRef(columnLabel, x)
    }

    override fun updateBlob(columnIndex: Int, x: Blob?) {
        resultSet?.updateBlob(columnIndex, x)
    }

    override fun updateBlob(columnLabel: String?, x: Blob?) {
        resultSet?.updateBlob(columnLabel, x)
    }

    override fun updateBlob(columnIndex: Int, inputStream: InputStream?, length: Long) {
        resultSet?.updateBlob(columnIndex, inputStream, length)
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?, length: Long) {
        resultSet?.updateBlob(columnLabel, inputStream, length)
    }

    override fun updateBlob(columnIndex: Int, x: InputStream?) {
        resultSet?.updateBlob(columnIndex, x)
    }

    override fun updateBlob(columnLabel: String?, inputStream: InputStream?) {
        resultSet?.updateBlob(columnLabel, inputStream)
    }

    override fun updateClob(columnIndex: Int, x: Clob?) {
        resultSet?.updateClob(columnIndex, x)
    }

    override fun updateClob(columnLabel: String?, x: Clob?) {
        resultSet?.updateClob(columnLabel, x)
    }

    override fun updateClob(columnIndex: Int, reader: Reader?, length: Long) {
        resultSet?.updateClob(columnIndex, reader, length)
    }

    override fun updateClob(columnLabel: String?, reader: Reader?, length: Long) {
        resultSet?.updateClob(columnLabel, reader, length)
    }

    override fun updateClob(columnIndex: Int, reader: Reader?) {
        resultSet?.updateClob(columnIndex, reader)
    }

    override fun updateClob(columnLabel: String?, reader: Reader?) {
        resultSet?.updateClob(columnLabel, reader)
    }

    override fun updateArray(columnIndex: Int, x: Array?) {
        resultSet?.updateArray(columnIndex, x)
    }

    override fun updateArray(columnLabel: String?, x: Array?) {
        resultSet?.updateArray(columnLabel, x)
    }

    override fun getRowId(columnIndex: Int): RowId {
        return resultSet?.getRowId(columnIndex)!!
    }

    override fun getRowId(columnLabel: String?): RowId {
        return resultSet?.getRowId(columnLabel)!!
    }

    override fun updateRowId(columnIndex: Int, x: RowId?) {
        resultSet?.updateRowId(columnIndex, x)
    }

    override fun updateRowId(columnLabel: String?, x: RowId?) {
        resultSet?.updateRowId(columnLabel, x)
    }

    override fun getHoldability(): Int {
        return resultSet?.holdability!!
    }

    override fun isClosed(): Boolean {
        return resultSet?.isClosed!!
    }

    override fun updateNString(columnIndex: Int, nString: String?) {
        resultSet?.updateNString(columnIndex, nString)
    }

    override fun updateNString(columnLabel: String?, nString: String?) {
        resultSet?.updateNString(columnLabel, nString)
    }

    override fun updateNClob(columnIndex: Int, nClob: NClob?) {
        resultSet?.updateNClob(columnIndex, nClob)
    }

    override fun updateNClob(columnLabel: String?, nClob: NClob?) {
        resultSet?.updateNClob(columnLabel, nClob)
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?, length: Long) {
        resultSet?.updateNClob(columnIndex, reader, length)
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?, length: Long) {
        resultSet?.updateNClob(columnLabel, reader, length)
    }

    override fun updateNClob(columnIndex: Int, reader: Reader?) {
        resultSet?.updateNClob(columnIndex, reader)
    }

    override fun updateNClob(columnLabel: String?, reader: Reader?) {
        resultSet?.updateNClob(columnLabel, reader)
    }

    override fun getNClob(columnIndex: Int): NClob {
        return resultSet?.getNClob(columnIndex)!!
    }

    override fun getNClob(columnLabel: String?): NClob {
        return resultSet?.getNClob(columnLabel)!!
    }

    override fun getSQLXML(columnIndex: Int): SQLXML {
        return resultSet?.getSQLXML(columnIndex)!!
    }

    override fun getSQLXML(columnLabel: String?): SQLXML {
        return resultSet?.getSQLXML(columnLabel)!!
    }

    override fun updateSQLXML(columnIndex: Int, xmlObject: SQLXML?) {
        return resultSet?.updateSQLXML(columnIndex, xmlObject)!!
    }

    override fun updateSQLXML(columnLabel: String?, xmlObject: SQLXML?) {
        return resultSet?.updateSQLXML(columnLabel, xmlObject)!!
    }

    override fun getNString(columnIndex: Int): String {
        return resultSet?.getNString(columnIndex)!!
    }

    override fun getNString(columnLabel: String?): String {
        return resultSet?.getNString(columnLabel)!!
    }

    override fun getNCharacterStream(columnIndex: Int): Reader {
        return resultSet?.getNCharacterStream(columnIndex)!!
    }

    override fun getNCharacterStream(columnLabel: String?): Reader {
        return resultSet?.getNCharacterStream(columnLabel)!!
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?, length: Long) {
        resultSet?.updateNCharacterStream(columnIndex, x, length)
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?, length: Long) {

        resultSet?.updateNCharacterStream(columnLabel, reader, length)
    }

    override fun updateNCharacterStream(columnIndex: Int, x: Reader?) {

        resultSet?.updateNCharacterStream(columnIndex, x)
    }

    override fun updateNCharacterStream(columnLabel: String?, reader: Reader?) {

        resultSet?.updateNCharacterStream(columnLabel, reader)
    }
}