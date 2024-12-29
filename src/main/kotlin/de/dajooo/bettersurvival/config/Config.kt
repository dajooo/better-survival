package de.dajooo.bettersurvival.config

import de.dajooo.kommons.exposed.datasource.DatabaseConfigurable
import de.dajooo.kommons.exposed.datasource.DatabaseType
import de.dajooo.kommons.loadYamlConfig
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class DatabaseConfig(
    override val type: DatabaseType = DatabaseType.POSTGRESQL,
    override val host: String = "127.0.0.1",
    override val port: Int = 5432,
    override val database: String = "bettersurvival",
    override val username: String = "bettersurvival",
    override val password: String = "bettersurvival",
    override val filePath: String? = null
): DatabaseConfigurable

@Serializable
data class Config(
    val database: DatabaseConfig = DatabaseConfig(),
)

fun loadConfig(basePath: Path) = loadYamlConfig(basePath.resolve("config.yaml"), Config())
