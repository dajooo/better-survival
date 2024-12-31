package de.dajooo.bettersurvival

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import de.dajooo.bettersurvival.commands.registerCommands
import de.dajooo.bettersurvival.config.Config
import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.config.loadConfig
import de.dajooo.bettersurvival.config.loadMessageConfig
import de.dajooo.bettersurvival.database.connectDatabase
import de.dajooo.bettersurvival.feature.FeatureRegistry
import de.dajooo.bettersurvival.gui.feature.FeatureOverview
import de.dajooo.bettersurvival.listeners.DatabaseListener
import de.dajooo.bettersurvival.listeners.VisualsListener
import de.dajooo.kaper.KotlinPlugin
import de.dajooo.kaper.extensions.pluginManager
import de.dajooo.kommons.TypedYamlConfiguration
import de.dajooo.kommons.koin.get
import de.dajooo.kommons.koin.loadModule
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.slf4j.logger
import me.devnatan.inventoryframework.ViewFrame
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module


class BetterSurvivalPlugin : KotlinPlugin() {

    override fun enable() {
        pluginManager.registerSuspendingEvents(VisualsListener, this)
        pluginManager.registerSuspendingEvents(DatabaseListener, this)
        startKoin {
            modules(module(createdAtStart = true) {
                single { this@BetterSurvivalPlugin } bind JavaPlugin::class
                single { loadConfig(dataPath) }
                single { get<TypedYamlConfiguration<Config>>().get() }
                single(named("messages")) { loadMessageConfig(dataPath) }
                single { get<TypedYamlConfiguration<MessageConfig>>(named("messages")).get() }
                single { KotlinLogging.logger(slF4JLogger) }
                single { FeatureRegistry().init() }
                single { ViewFrame.create(this@BetterSurvivalPlugin).with(FeatureOverview()).register() }
                single { registerCommands() }
                single {
                    val provider = Bukkit.getServicesManager().getRegistration(
                        LuckPerms::class.java
                    )
                    provider?.provider
                }
            })
        }
    }

    override suspend fun enableSuspending() {
        val db = connectDatabase()
        loadModule {
            single { db }
        }
    }

    override fun onDisable() {
        TransactionManager.closeAndUnregister(get())
    }
}
