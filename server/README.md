# Notable - Server

The server written using Kotlin.

# Stack

- [Ktor](https://ktor.io/) for the REST server
- [Exposed](https://github.com/JetBrains/Exposed) as the database ORM
- PostgreSQL
- [JWT](https://jwt.io/) Token for authentication
- [Detekt](https://detekt.dev/) for linting

# Installation

1. Install Docker and get the Postgresql container
2. Start the Postgresql container and create a DB with the name `notable`
   ```bash
    docker exec -it postgres createdb --username=<owner> --owner=<owner> notable
   ```
3. Install [IntelliJ](https://www.jetbrains.com/idea/)
4. Sync the dependencies using `Gradle`
5. Run the server from the `Application.kt` file.

# Testing

1. Create a test DB
   ```bash
    docker exec -it postgres createdb --username=<owner> --owner=<owner> notable_test
   ```
2. Change the `db.dbUrl` value in the `application-test.conf` to your values.
3. Run the tests in the `test` directory.
