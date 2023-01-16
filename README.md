# lsd-jdbc-driver

This is a JDBC implementation of the concepts presented in [Lazy State Determination: More concurrency for contending linearizable transactions](https://arxiv.org/abs/2007.09733) by Tiago M. Vale, João Leitão, Nuno Preguiça, Rodrigo Rodrigues, Ricardo J. Dias, João M. Lourenço.

## Considerations

The driver is implemented in Kotlin and the idea was to have minimal changes to formal JDBC API.
For this, new methods were introduced that enable using futures while the original API for non-futures remains untouched.

The driver was tested with PostgreSQL, but the intention is for the driver to be vendor-agnostic.
As an example, a connection URI for PostgreSQL would be defined as follows `jdbc:lsd:postgresql://some_host:some_port/some_database`.
What happens in the background is that the LSD driver will create a backing connection with the inteded database, in this case PostgresSQL. 

The connection URI is structured in a way that it should start with `jdbc:lsd:` and the remainder should be related with the database vendor.
This is because the `lsd:` will be removed from the connection URI and LSD driver will then search for a driver that will use to create the backing connection.
Going back to the PostgreSQL example, the backing connection used by this driver would be `jdbc:postgresql://some_host:some_port/some_database`.

## Installing

The library is not currently published to any maven repository, so you need to either reference the output jar or publish to your maven local by doing 
```bash
./gradlew publishToMavenLocal
```

Then you should be able to reference it either as a maven dependency
```xml
    <dependency>
        <groupId>trxsys</groupId>
        <artifactId>lsd.v2</artifactId>
        <version>0.11.0</version>
    </dependency>
```
or as a gradle dependency

```groovy
    implementation "trxsys:lsd.v2:0.12.0"
```

## Usage

In order to use this library you must first register it as an avaliable driver
```kotlin
import trxsys.lsd.Driver;
...
...
    Driver driver = new Driver();
    DriverManager.registerDriver(driver);
```

In order to create the connection, you must then request it via the wrapping connection URI, in the form of `jdbc:lsd:postgresql://some_host:some_port/some_database`.

```kotlin
    val connection = DriverManager.getConnection("jdbc:lsd:postgresql://some_host:some_port/some_database", properties)
```

Using the connection is then very similar to what you would do with a JDBC driver

```kotlin
    val connection = DriverManager.getConnection("jdbc:lsd:postgresql://some_host:some_port/some_database", properties)

    stockSQL = connection.prepareFutureStatement("SELECT stock FROM items WHERE id = 1")

    val result = stockSQL.executeFutureQuery()
    val stock = result.getFutureInt(1)

    val updateSQL = connection.prepareFutureStatement("UPDATE items SET stock = ? + 50 WHERE id = 1")
    updateSQL.setFutureInt(1, stock)
    updateSQL.executeFutureUpdate()

    connection.commit()
```

## Examples

Usage examples are present in the [LSDConnectionTest.kt](src/test/kotlin/trxsys/lsd/jdbc/LSDConnectionTest.kt) and [LSDPreparedStatementTest.kt](src/test/kotlin/trxsys/lsd/jdbc/LSDPreparedStatementTest.kt).

## Acknowledgments

This work was partially funded by the Portuguese FCT-MEC project HiPSTr (High-performance Software Transactions — PTDC/CCI-COM/32456/2017)&LISBOA-01-0145-FEDER-032456).
