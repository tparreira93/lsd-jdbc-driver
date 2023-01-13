# lsd-jdbc-driver

This is a JDBC implementation of the concepts presented in [Lazy State Determination: More concurrency for contending linearizable transactions](https://arxiv.org/abs/2007.09733) by Tiago M. Vale, João Leitão, Nuno Preguiça, Rodrigo Rodrigues, Ricardo J. Dias, João M. Lourenço.

## Considerations

The driver is implemented in Kotlin and the idea was to have minimal changes to formal JDBC API.
For this, new methods were introduced that enable using futures while the original API for non-futures remains untouched.

The driver was tested with PostgreSQL but the intention is for the driver to be database vendor agnostic.
As an example, a connection URI for PostgreSQL would be defined as follows `jdbc:lsd:postgresql://some_host:some_port/some_database`.
What happens in the background is that the LSD driver will create a backing connection with the inteded database, in this case PostgresSQL. 

The connection URI is structured in a way that it should start with `jdbc:lsd:` and the remainder should be related with the database vendor.
This is because the `lsd:` will be removed from the from the connection URI and LSD driver will then search for a driver that will use to create the backing connection.
Going back to the PostgreSQL example, the backing connection used by this driver would be `jdbc:postgresql://some_host:some_port/some_database`.

## Acknowledgments

This work was partially funded by the Portuguese FCT-MEC project HiPSTr (High-performance Software Transactions — PTDC/CCI-COM/32456/2017)&LISBOA-01-0145-FEDER-032456).
