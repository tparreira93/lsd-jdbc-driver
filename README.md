# lsd-jdbc-driver

This is a JDBC implementation of the concepts presented in [Lazy State Determination: More concurrency for contending linearizable transactions](https://arxiv.org/abs/2007.09733) by Tiago M. Vale, João Leitão, Nuno Preguiça, Rodrigo Rodrigues, Ricardo J. Dias, João M. Lourenço.

## Considerations

The driver is implemented in kotlin and the idea was to have minimal changes to formal JDBC API.
For this new methods were introduced that enable using futures and the original API for non-futures remains untouched.

The driver was tested with PostgreSQL but the intention is for the driver to be database vendor agnostic.
The connection for PostgreSQL is defined as follows `jdbc:lsd.v2:postgresql://some_host:some_port/some_database`.
The only requirement in terms of connection URI is to have the database URI start with `jdbc:lsd.v2`, the remainder should be related with the database vendor.
