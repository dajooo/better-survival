package de.dajooo.bettersurvival.feature.features

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.minecraftDispatcher
import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.commands.suggestions.SuggestWarps
import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.database.model.Warp
import de.dajooo.bettersurvival.database.model.Warps
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.player.survivalPlayer
import de.dajooo.bettersurvival.util.TeleportConfigurable
import de.dajooo.bettersurvival.util.teleportSuspending
import de.dajooo.kaper.extensions.mini
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.to
import kotlinx.serialization.Serializable
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.CommandPlaceholder
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission

class WarpsFeature : AbstractFeature<WarpsFeature.Config>() {

    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        override var keepPassengers: Boolean = true,
        override var keepVehicle: Boolean = true,
    ) : FeatureConfig, TeleportConfigurable

    override val name = "warps"
    override val displayName = !"<gold>Warps</gold>"
    override val description = !"<gray>Adds a warps feature to the plugin.</gray>"
    override val typedConfig = config(Config())

    override val commands = arrayOf<Any>(WarpsCommand, WarpCommand(config), SetWarpCommand, DeleteWarpCommand)

    @Command("warps", "warp list", "warp ls")
    object WarpsCommand : KoinComponent {
        private val messages by inject<MessageConfig>()

        @CommandPlaceholder
        suspend fun warps(actor: BukkitCommandActor) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            player.sendMessage(!messages.warpListHeader)
            newSuspendedTransaction { Warp.all() }.toList().forEach {
                player.sendMessage(
                    messages.warpListEntry.mini(
                        "name" to it.name,
                        "x" to it.location.blockX,
                        "y" to it.location.blockY,
                        "z" to it.location.blockZ,
                        "world" to it.location.world.name
                    )
                )
            }
            player.sendMessage(!messages.warpListFooter)
        }
    }

    @Command("warp", "w")
    @CommandPermission("bettersurvival.warp.teleport")
    class WarpCommand(private val config: Config) : KoinComponent {
        private val messages by inject<MessageConfig>()
        private val plugin by inject<BetterSurvivalPlugin>()

        @Subcommand("<name>")
        suspend fun warp(actor: BukkitCommandActor, @SuggestWarps name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            val warp = newSuspendedTransaction { Warp.find(Warps.name eq name).firstOrNull() }
                ?: return player.sendMessage(messages.warpNotFound.mini("name" to name))
            newSuspendedTransaction {
                player.survivalPlayer.databasePlayer.lastPosition = player.location
            }
            plugin.launch(plugin.minecraftDispatcher) {
                player.sendMessage(messages.warpTeleport.mini("name" to warp.name))
                player.teleportSuspending(warp.location, PlayerTeleportEvent.TeleportCause.COMMAND, config)
            }
        }
    }

    @Command("setwarp", "sw")
    @CommandPermission("bettersurvival.warp.set")
    object SetWarpCommand : KoinComponent {
        private val messages by inject<MessageConfig>()

        @Subcommand("<name>")
        suspend fun setWarp(actor: BukkitCommandActor, @SuggestWarps name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            newSuspendedTransaction {
                Warp.findSingleByAndUpdate(Warps.name eq name) {
                    it.location = player.location
                } ?: Warp.new {
                    this.name = name
                    this.location = player.location
                }
            }
            player.sendMessage(messages.warpSet.mini("name" to name))
        }
    }

    @Command("deletewarp", "delwarp", "rmwarp", "dw", "rmw")
    @CommandPermission("bettersurvival.warp.delete")
    object DeleteWarpCommand : KoinComponent {
        private val messages by inject<MessageConfig>()

        @Subcommand("<name>")
        suspend fun deleteWarp(actor: BukkitCommandActor, @SuggestWarps name: String) {
            val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
            newSuspendedTransaction {
                Warp.find(Warps.name eq name).firstOrNull()?.delete()
            }
            player.sendMessage(messages.warpRemoved.mini("name" to name))
        }
    }

}
