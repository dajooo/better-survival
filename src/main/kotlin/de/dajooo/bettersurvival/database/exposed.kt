package de.dajooo.bettersurvival.database

import de.dajooo.bettersurvival.config.Config
import de.dajooo.bettersurvival.database.model.Homes
import de.dajooo.bettersurvival.database.model.Players
import de.dajooo.bettersurvival.database.model.Warps
import de.dajooo.kommons.exposed.datasource.createDataSourceFromConfigurable
import de.dajooo.kommons.koin.getKoin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun connectDatabase(): Database {
    val db = Database.connect(createDataSourceFromConfigurable(getKoin().get<Config>().database))
    newSuspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(Players, Homes, Warps)
    }
    return db
}