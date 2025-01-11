package de.dajooo.bettersurvival.feature.features

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.util.TeleportConfigurable
import de.dajooo.kaper.extensions.not
import kotlinx.serialization.Serializable
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.actor.BukkitCommandActor

class QuickAccessCommandsFeature : AbstractFeature<QuickAccessCommandsFeature.Config>() {
    private val messages by inject<MessageConfig>()

    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var enableCraftCommand: Boolean = true,
        var enableEnderChestCommand: Boolean = true,
    ) : FeatureConfig

    override val name = "quick-access-commands"
    override val displayName = !"<gold>Quick Access Commands</gold>"
    override val description = !"<gray>Adds commands to quickly access various guis.</gray>"
    override val typedConfig = config(Config())

    override val commands = arrayOf<Any>(this)

    @Command("craft", "c")
    fun craft(actor: BukkitCommandActor) {
        val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
        if (!player.hasPermission("bettersurvival.craft")) return actor.sender().sendMessage(!messages.noPermission)
        if (!config.enableCraftCommand) return actor.sender().sendMessage(!messages.commandDisabled)
        player.openWorkbench(player.location, true)
    }

    @Command("enderchest", "ec")
    fun enderchest(actor: BukkitCommandActor) {
        val player = actor.asPlayer() ?: return actor.sender().sendMessage(!messages.playersOnlyCommand)
        if (!player.hasPermission("bettersurvival.enderchest")) return actor.sender().sendMessage(!messages.noPermission)
        if (!config.enableCraftCommand) return actor.sender().sendMessage(!messages.commandDisabled)
        player.openInventory(player.enderChest)
    }
}
