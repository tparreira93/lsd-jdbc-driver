package lsd.v2.util

import lsd.v2.Driver
import kotlin.system.exitProcess

object LSDJDBCMain {
    @JvmStatic
    fun main(args: Array<String>) {
        val url = Driver::class.java.getResource("/lsd.driver.v2/LSDDriver.class")
        System.out.printf("%n%s%n", DriverInfo.DRIVER_FULL_NAME)
        System.out.printf("Found in: %s%n%n", url)
        System.out.printf(
            "The LSD JDBC driver is not an executable Java program.%n%n"
                    + "You must install it according to the JDBC driver installation "
                    + "instructions for your application / container / appserver, "
                    + "then use it by specifying a JDBC URL of the form %n    jdbc:lsd.v2:<database>//%n"
                    + "or using an application specific method.%n%n"
                    + "This command has had no effect.%n"
        )
        exitProcess(1)
    }
}