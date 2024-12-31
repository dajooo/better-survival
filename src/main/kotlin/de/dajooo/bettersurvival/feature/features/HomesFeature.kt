package de.dajooo.bettersurvival.feature.features

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.commands.suggestions.SuggestHomes
import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.database.model.Home
import de.dajooo.bettersurvival.database.model.Homes
import de.dajooo.bettersurvival.database.model.PlayerEntity
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.kaper.extensions.minimessage
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.to
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.CommandPlaceholder
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor

class HomesFeature : AbstractFeature<HomesFeature.Config>() {

    @Serializable
    data class Config(
        override var enabled: Boolean = true
    ) : FeatureConfig

    override val name = "homes"
    override val displayName = !"<gold>Homes</gold>"
    override val description = !"<gray>Adds a homes feature to the plugin.</gray>"
    override val typedConfig = config(Config())

    override val commands = arrayOf<Any>(HomesCommand, SetHomeCommand, DeleteHomeCommand)

    @Command("home", "h")
    object HomesCommand : KoinComponent {
        private val messages by inject<MessageConfig>()
        private val plugin by inject<BetterSurvivalPlugin>()

        @CommandPlaceholder
        suspend fun home(actor: BukkitCommandActor) {
            home(actor, "default")
        }

        @Subcommand("<name>")
        suspend fun home(actor: BukkitCommandActor, @SuggestHomes name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            val home = newSuspendedTransaction {
                Home.find { Homes.player.eq(player.uniqueId) and Homes.name.eq(name) }.firstOrNull()
            } ?: return player.sendMessage(!messages.homeNotFound)
            plugin.launch(plugin.minecraftDispatcher) {
                player.sendMessage(minimessage(messages.homeTeleport, "home" to home.name))
                player.teleportAsync(home.location)
            }
        }
    }

    @Command("sethome", "sh")
    object SetHomeCommand : KoinComponent {
        private val messages by inject<MessageConfig>()

        @CommandPlaceholder
        suspend fun setHome(actor: BukkitCommandActor) {
            setHome(actor, "default")
        }

        @Subcommand("<name>")
        suspend fun setHome(actor: BukkitCommandActor, @SuggestHomes name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            newSuspendedTransaction {
                val databasePlayer = PlayerEntity.findById(player.uniqueId) ?: return@newSuspendedTransaction actor.sender().sendMessage("Error retrieving user from database.")
                Home.findSingleByAndUpdate(Homes.player.eq(player.uniqueId) and Homes.name.eq(name)) {
                    it.location = player.location
                } ?: Home.new {
                    this.name = name
                    this.player = databasePlayer
                    this.location = player.location
                }
            }
            player.sendMessage(minimessage(messages.homeSet, "home" to name))
        }
    }

    @Command("deletehome", "delhome", "rmhome", "dh", "rmh")
    object DeleteHomeCommand : KoinComponent {
        private val messages by inject<MessageConfig>()

        @CommandPlaceholder
        suspend fun deleteHome(actor: BukkitCommandActor) {
            deleteHome(actor, "default")
        }

        @Subcommand("<name>")
        suspend fun deleteHome(actor: BukkitCommandActor, @SuggestHomes name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            newSuspendedTransaction {
                Home.find(Homes.player.eq(player.uniqueId) and Homes.name.eq(name)).first().delete()
            }
            player.sendMessage(minimessage(messages.homeRemoved, "home" to name))
        }
    }


}