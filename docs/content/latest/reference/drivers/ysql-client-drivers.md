---
title: Client drivers for YSQL
headerTitle: Client drivers for YSQL
linkTitle: Client drivers for YSQL
description: Lists the client drivers that you can use to connect to and interact with the YSQL API.
menu:
  latest:
    identifier: ysql-client-libraries
    parent: drivers
    weight: 2940
isTocNested: true
showAsideToc: true
---

The [Yugabyte Structured Query Language (YSQL) API](../../../api/ysql) builds upon and extends a fork of the query layer from PostgreSQL 11.2, with the intent of supporting most PostgreSQL functionality and adding new functionality to supported distributed SQL databases.

For details on PostgreSQL feature support in YSQL, see [What Features Does YSQL Support?](https://github.com/yugabyte/yugabyte-db/blob/master/architecture/YSQL-Features-Supported.md)

Yugabyte and third party client drivers listed below are supported for developing applications that connect to and interact with the YSQL API. Most of the drivers use [libpq](#libpq) and support the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For help using these drivers with YSQL, ask your questions in the [Slack community](https://www.yugabyte.com/slack).

If you encounter an issue or have an enhancement request, [file a GitHub issue](https://github.com/yugabyte/yugabyte-db/issues/new/choose).

## C

### libpq

[`libpq`](https://www.postgresql.org/docs/11/libpq.html) is the C client library for connecting to and interacting with PostgreSQL databases. `libpq` is also the underlying engine used in other PostgreSQL application interfaces. The `libpq` client library also supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For details and documentation, see [`libpq - C Library`](https://www.postgresql.org/docs/11/libpq.html) for PostgreSQL 11, which YugabyteDB is based on.

For a tutorial on building a sample C application with `libpq`, see [Build a C application](../../../quick-start/build-apps/c/ysql/).

#### To install the libpq client library

The `libpq` C driver is included in the YugabyteDB installation. You can use it by setting the `LD_LIBRARY_PATH` as follows:

```sh
$ export LD_LIBRARY_PATH=<yugabyte-install-dir>/postgres/lib
```

## C++

### libpqxx

The [libpqxx](http://pqxx.org/development/libpqxx/) driver is the official C++ client API for PostgreSQL. `libpqxx` is based on [`libpq`](#libpq) and supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For details and documentation, see [`jvt/libpqxx` README](https://github.com/jtv/libpqxx#readme) and [`libpqxx` ReadTheDocs](https://libpqxx.readthedocs.io/en/latest/).

For a tutorial on building a sample C++ application with `libpqxx`, see [Build a C++ application](../../../quick-start/build-apps/cpp/ysql/).

#### Install the libpqxx driver

To build and install the libpqxx driver for use with YugabyteDB, follow these steps:

- Clone the libpqxx repository.

```sh
$ git clone https://github.com/jtv/libpqxx.git
```

- For dependencies on the PostgreSQL binaries, add the PostgreSQL `bin` directory to the command path by running the following command.

```sh
$ export PATH=$PATH:<yugabyte-install-dir>/postgres/bin
```

- Build and install the driver.

```sh
$ cd libpqxx
$ ./configure
$ make
$ make install
```

## C\#

### Npgsql

[Npgsql](https://www.npgsql.org/) is an open source ADO.NET Data Provider for PostgreSQL that enables C# applications to connect and interact with PostgreSQL databases. Npgsql is based on [`libpq`](#libpq) and supports the [[SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For details on installing and using Npgsql, see [Npgsql documentation](https://www.npgsql.org/doc/).

To follow a tutorial on building a sample C# application with Npgsql, see [Build a C# application](../../../quick-start/build-apps/csharp/ysql/).

#### Install the driver

To install Npgsql in your Visual Studio project, follow the steps below.

1. Open the **Project Solution View**.

2. Right-click on **Packages** and click **Add Packages**.

3. Search for `Npgsql` and click **Add Package**.

{{< warning title="Warning" >}}

On every new connection the NpgSQL driver also makes [extra system table queries to map types](https://github.com/npgsql/npgsql/issues/1486), which adds significant overhead. To turn off this behavior, set the following option in your connection string builder:

```csharp
connStringBuilder.ServerCompatibilityMode = ServerCompatibilityMode.NoTypeLoading;
```

{{< /warning >}}

## Go

### Go PostgreSQL driver (pq)

The [Go PostgreSQL driver package (`pq`)](https://pkg.go.dev/github.com/lib/pq?tab=doc) is a Go PostgreSQL driver for the `database/sql` package.  `pq` is not based on the [`libpq`](#libpq) client library, but supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For a tutorial on building a sample Go application with `pq`, see [Build a Go application](../../../quick-start/build-apps/go/ysql-pq).

#### Install the pq driver

To install the package locally, run the following [`go get`](https://golang.org/cmd/go/#hdr-Add_dependencies_to_current_module_and_install_them) command:

```sh
$ go get github.com/lib/pq
```

The `pq` driver is ready for building Go applications that connect to and interact with YugabyteDB.

## Java

### Yugabyte JDBC driver

The Yugabyte JDBC driver is a distributed JDBC driver for YSQL built on the PostgreSQL JDBC driver, with features that eliminate the need for external load balancers.

For information on the Yugabyte JDBC driver and its load balancing features, see [Yugabyte JDBC driver](../../../integrations/jdbc-driver).

For a tutorial on building a sample Java application with the Yugabyte JDBC driver, see [Build a Java application](../../../quick-start/build-apps/java/ysql-yb-jdbc/).

### PostgreSQL JDBC driver (PgJDBC)

The [PostgreSQL JDBC driver](https://jdbc.postgresql.org/) is the official JDBC driver for PostgreSQL. PgJDBC is not based on [`libpq`](#libpq), but supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For a tutorial on building a sample Java application with the PostgreSQL JDBC Driver, see [Build a Java application](../../../quick-start/build-apps/java/ysql-jdbc/).

#### Install the PostgreSQL JDBC driver

To download binary JAR files, go to [PostgreSQL JDBC driver – Downloads](https://jdbc.postgresql.org/download.html). Because Java is platform neutral, download the appropriate JAR file and drop it into the classpath.

To get the latest versions for projects using [Apache Maven](https://maven.apache.org), see [Maven Central Repository Search](https://search.maven.org/artifact/org.postgresql/postgresql/42.2.14.jre7/jar).

## Node.JS

### node-postgres

[`node-postgres`](https://node-postgres.com/) is a collection of Node.js modules for interacting with PostgreSQL databases. `node-postgres` optionally uses [`libpq`](#libpq) and supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For details on installing and using node-postgres, see the [node-postgres documentation](https://node-postgres.com/).

For a tutorial on building a sample Node.js application with `node-postgres`, see [Build a Node.js application](../../../quick-start/build-apps/nodejs/ysql-pg/).

#### Install the node-postgres (pg) driver

To install `node-postgres` and any packages it depends on, run the following [`npm install`](https://docs.npmjs.com/cli/install.html) command:

```sh
$ npm install pg
```

## PHP

### php-pgsql

The [`php-pgsql`](https://www.php.net/manual/en/book.pgsql.php) driver is a collection of the official PostgreSQL module for PHP. `php-pgsql` is based on [`libpq`](#libpq) and supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For a tutorial on building a sample Node.js application with `php-pgsql`, see [Build a Node.js application](../../../quick-start/build-apps/php/ysql/).

#### Install the php-pgsql driver

To enable PostgreSQL support using `php-pgsql`, see [Installing/Configuring](https://www.php.net/manual/en/pgsql.setup.php) in the PHP documentation.

## Python

### psycopg2

[Psycopg](https://www.psycopg.org/) is the popular PostgreSQL database adapter for the Python programming language. `psycopg2` is based on [`libpq`](#libpq) and supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For details on using `psycopg2`, see [Psycopg documentation](https://www.psycopg.org/docs/).

For a tutorial on building a sample Python application that uses `psycopg2`, see [Build a Python application](../../../quick-start/build-apps/python/ysql-psycopg2).

#### Install the psycopg2 binary

To install the `psycopg2` binary package, run the following `pip3 install` command:

```sh
$ pip3 install psycopg2-binary
```

### aiopg

[aiopg](https://aiopg.readthedocs.io/en/stable/) is a library for accessing a PostgreSQL database using the asyncio (PEP-3156/tulip) framework. It wraps asynchronous features of the [Psycopg](https://www.psycopg.org/) database driver. For details on using `aiopg`, see [aiopg documentation](https://aiopg.readthedocs.io/en/stable/).

For a tutorial on building a sample Python application that uses `aiopg`, see [Build a Python application](../../../quick-start/build-apps/python/ysql-aiopg).

#### Install

To install the `aiopg` package, run the following `pip3 install` command:

```sh
pip3 install aiopg
```

## Ruby

### pg

[`pg`](https://github.com/ged/ruby-pg) is the Ruby interface for PostgreSQL databases. `pg` is based on [`libpq`](#libpq) and supports the [SCRAM-SHA-256 authentication method](../../../secure/authentication/password-authentication/#scram-sha-256).

For a tutorial on building a sample Ruby application with `pg`, see [Build a Ruby application](../../../quick-start/build-apps/ruby/ysql-pg).

#### Install the pg driver

To install the `pg` driver, run the following [`gem install`](https://guides.rubygems.org/command-reference/#gem-install) command:

```sh
$ gem install pg -- --with-pg-config=<yugabyte-install-dir>/postgres/bin/pg_config
```
