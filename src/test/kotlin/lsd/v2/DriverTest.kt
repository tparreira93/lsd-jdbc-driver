package lsd.v2

import Helper
import lsd.v2.jdbc.LSDConnection
import org.junit.jupiter.api.Test

class DriverTest {
    private val urlLSD = "jdbc:lsd.v2:postgresql://localhost/benchmarksql_test"
    private val helper = Helper()

    @Test
    fun `URL Acceptance`() {
        assert(Driver().acceptsURL(urlLSD))
    }

    @Test
    fun `Establish Connection`() {
        val connection = helper.createConnection()
        assert(connection.javaClass == LSDConnection::class.java)
        connection.close()
    }
}