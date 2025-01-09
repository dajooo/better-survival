package de.dajooo.bettersurvival.feature.features

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.commands.suggestions.SuggestHomes
import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.player.survivalPlayer
import de.dajooo.bettersurvival.util.TeleportConfigurable
import de.dajooo.bettersurvival.util.teleportAsync
import de.dajooo.kaper.extensions.mini
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.to
import kotlinx.serialization.Serializable
import org.bukkit.event.player.PlayerTeleportEvent
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
        override var enabled: Boolean = true,
        var allowBack: Boolean = true,
        override var keepPassengers: Boolean = true,
        override var keepVehicle: Boolean = true,
    ) : FeatureConfig, TeleportConfigurable

    override val name = "homes"
    override val displayName = !"<gold>Homes</gold>"
    override val description = !"<gray>Adds a homes feature to the plugin.</gray>"
    override val typedConfig = config(Config())

    override val commands =
        arrayOf<Any>(HomesCommand, HomeCommand(config), SetHomeCommand, DeleteHomeCommand, BackCommand(config))

    class BackCommand(private val config: Config) : KoinComponent {
        private val messages by inject<MessageConfig>()

        @Command("back", "b")
        suspend fun back(actor: BukkitCommandActor) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            if (!config.allowBack) return player.sendMessage(!messages.backDisabled)
            val databasePlayer = newSuspendedTransaction { player.survivalPlayer.databasePlayer }
            val lastLocation = databasePlayer.deathPosition ?: databasePlayer.lastPosition
            player.sendMessage(!messages.backTeleport)
            newSuspendedTransaction {
                databasePlayer.deathPosition = null
                databasePlayer.lastPosition = player.location
            }
            player.teleportAsync(lastLocation, PlayerTeleportEvent.TeleportCause.COMMAND, config)
        }
    }

    @Command("homes", "home list", "home ls")
    object HomesCommand : KoinComponent {
        private val messages by inject<MessageConfig>()
        private val plugin by inject<BetterSurvivalPlugin>()

        @CommandPlaceholder
        suspend fun homes(actor: BukkitCommandActor) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            player.sendMessage(!messages.homeListHeader)
            newSuspendedTransaction { player.survivalPlayer.homes() }.forEach {
                player.sendMessage(
                    messages.homeListEntry.mini(
                        "name" to it.name,
                        "x" to it.location.blockX,
                        "y" to it.location.blockY,
                        "z" to it.location.blockZ,
                        "world" to it.location.world.name
                    )
                )
            }
            player.sendMessage(!messages.homeListFooter)
        }
    }

    @Command("home", "h")
    class HomeCommand(private val config: Config) : KoinComponent {
        private val messages by inject<MessageConfig>()
        private val plugin by inject<BetterSurvivalPlugin>()

        @CommandPlaceholder
        suspend fun home(actor: BukkitCommandActor) {
            home(actor, "default")
        }

        @Subcommand("<name>")
        suspend fun home(actor: BukkitCommandActor, @SuggestHomes name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            val home = newSuspendedTransaction { player.survivalPlayer.home(name) }
                    ?: return player.sendMessage(messages.homeNotFound.mini("name" to name))
            newSuspendedTransaction {
                player.survivalPlayer.databasePlayer.lastPosition = player.location
            }
            plugin.launch(plugin.minecraftDispatcher) {
                player.sendMessage(messages.homeTeleport.mini("name" to home.name))
                player.teleportAsync(home.location, PlayerTeleportEvent.TeleportCause.COMMAND, config)
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
                player.survivalPlayer.upsertHomeLocation(name)
            }
            player.sendMessage(messages.homeSet.mini("name" to name))
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
                player.survivalPlayer.home(name)?.delete()
            }
            player.sendMessage(messages.homeRemoved.mini("name" to name))
        }
    }


}
