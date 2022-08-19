package io.notable.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.HoconApplicationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private const val MAX_POOL_SIZE = 3

    fun init(config: HoconApplicationConfig) {
        Database.connect(hikari(config))
        transaction {
            create(UserTable, NoteTable)
        }
    }

    private fun hikari(appConfig: HoconApplicationConfig): HikariDataSource {
        val url = appConfig.property("db.dbUrl").getString()
        val credentialsAndConnectionString = url.split("@")
        val credentials = credentialsAndConnectionString[0].split("postgresql://")[1].split(":")


        val config = HikariConfig().apply {
            driverClassName = appConfig.property("db.dbDriver").getString()
            jdbcUrl = "jdbc:postgresql://${credentialsAndConnectionString[1]}"
            username = credentials[0]
            password = credentials[1]
            maximumPoolSize = MAX_POOL_SIZE
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

    suspend fun dropDB() {
        dbQuery {
            drop(UserTable, NoteTable)
        }
    }
}
