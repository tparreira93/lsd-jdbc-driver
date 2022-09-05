import trxsys.lsd.Driver
import trxsys.lsd.api.FutureConnection
import trxsys.lsd.jdbc.LSDConnection
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class Helper {
    val properties = loadProps()

    fun getUrl(): String {
        return properties.getProperty("connectionLSD")
    }

    fun getConvertedUrl(): String {
        return Driver.converUrl(getUrl())
    }

    fun createConnection(): Connection {
        DriverManager.registerDriver(Driver())
        return DriverManager.getConnection(properties.getProperty("connectionLSD"), properties)
    }

    fun createLSDConnection(): FutureConnection {
        DriverManager.registerDriver(Driver())
        return DriverManager.getConnection(properties.getProperty("connectionLSD"), properties) as LSDConnection
    }

    companion object {
        fun loadProps(): Properties {
            val props = Properties()
            val resource = this.javaClass.classLoader.getResourceAsStream("lsd.properties")
            props.load(resource)

            return props
        }
    }
}