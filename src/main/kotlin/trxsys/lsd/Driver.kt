package trxsys.lsd

import trxsys.lsd.jdbc.LSDConnection
import trxsys.lsd.util.DriverInfo
import java.sql.Connection
import java.sql.DriverManager
import java.sql.DriverPropertyInfo
import java.sql.SQLException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


class Driver : java.sql.Driver {

    private val logger = Logger.getLogger(this.javaClass.name)

    init {
        logger.log(Level.FINE, DriverInfo.DRIVER_FULL_NAME)
    }

    override fun connect(url: String?, properties: Properties): Connection? {
        // As per the documentation, connect must return null if the driver realises its url is invalid
        if (!acceptsURL(url!!)) return null

        val connection = createBackingConnection(converUrl(url), properties)
        return LSDConnection(connection)
    }

    override fun acceptsURL(url: String?): Boolean {
        if (url!!.startsWith(DriverInfo.LSD_URL)) {
            return true
        }

        return false
    }

    private fun createBackingConnection(url: String, props: Properties): Connection {
        val conn = DriverManager.getConnection(url, props)
        conn.autoCommit = false

        return conn
    }

    override fun getPropertyInfo(url: String?, info: Properties?): Array<DriverPropertyInfo> {
        TODO("Not yet implemented")
    }

    override fun getMajorVersion(): Int {
        return DriverInfo.MAJOR_VERSION
    }

    override fun getMinorVersion(): Int {
        return DriverInfo.MINOR_VERSION
    }

    // To return true, this driver must pass a specific test battery.
    // It is irrelevant
    override fun jdbcCompliant(): Boolean {
        return false
    }

    override fun getParentLogger(): Logger {
        return logger.parent
    }

    companion object {
        fun converUrl(url: String): String {
            if (url.startsWith(DriverInfo.LSD_URL)) {
                return url.replace(DriverInfo.LSD_URL, DriverInfo.JDBC_URL)
            }

            throw SQLException("Inconvertible URL: $url")
        }
    }
}