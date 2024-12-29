package de.dajooo.bettersurvival.commands

import de.dajooo.bettersurvival.commands.suggestions.SuggestFeatures
import de.dajooo.bettersurvival.config.Config
import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.FeatureRegistry
import de.dajooo.bettersurvival.gui.feature.FeatureOverview
import de.dajooo.kaper.extensions.minimessage
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.to
import me.devnatan.inventoryframework.ViewFrame
import net.kyori.adventure.text.format.NamedTextColor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.CommandPlaceholder
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("features", "feature", "feat", "f")
object FeaturesCommand : KoinComponent {
    private val config by inject<Config>()
    private val messages by inject<MessageConfig>()
    private val featureRegistry by inject<FeatureRegistry>()
    private val viewFrame by inject<ViewFrame>()

    @Subcommand("list", "ls")
    @CommandPermission("bettersurvival.feature.list")
    fun list(actor: BukkitCommandActor) {
        actor.sender().sendMessage(!"<gray>--- <white>Features</white> <gray>---")
        featureRegistry.forEach {
            val statusName = if (it.enabled) it.displayName.color(NamedTextColor.GREEN) else it.displayName.color(NamedTextColor.RED)
            actor.sender().sendMessage(
                minimessage(
                    "<gray>-</gray> <status_name> <gray>-</gray> <white><description></white>",
                    "status_name" to statusName,
                    "description" to it.description
                )
            )
        }
    }

    @Subcommand("enable <feature>")
    @CommandPermission("bettersurvival.feature.enable")
    fun enable(actor: BukkitCommandActor, @SuggestFeatures feature: String) {
        if (!actor.sender().hasPermission("bettersurvival.feature.enable.$feature")) return actor.sender().sendMessage(messages.noPermission)
        val feat = featureRegistry.enable(feature) ?: return actor.sender().sendMessage(minimessage(messages.featureNotFound, "feature" to feature))
        actor.sender().sendMessage(minimessage(messages.featureEnabled, "feature" to feat.displayName))
    }

    @Subcommand("disable <feature>")
    @CommandPermission("bettersurvival.feature.disable")
    fun disable(actor: BukkitCommandActor, @SuggestFeatures feature: String) {
        if (!actor.sender().hasPermission("bettersurvival.feature.disable.$feature")) return actor.sender().sendMessage(messages.noPermission)
        val feat = featureRegistry.enable(feature) ?: return actor.sender().sendMessage(minimessage(messages.featureNotFound, "feature" to feature))
        actor.sender().sendMessage(minimessage(messages.featureDisabled, "feature" to feat.displayName))
    }

    @Subcommand("toggle <feature>")
    @CommandPermission("bettersurvival.feature.toggle")
    fun toggle(actor: BukkitCommandActor, @SuggestFeatures feature: String) {
        if (!actor.sender().hasPermission("bettersurvival.feature.toggle.$feature")) return actor.sender().sendMessage(messages.noPermission)
        val feat = featureRegistry.toggle(feature) ?: return actor.sender().sendMessage(minimessage(messages.featureNotFound, "feature" to feature))
        if(feat.enabled) {
            actor.sender().sendMessage(minimessage(messages.featureEnabled, "feature" to feat.displayName))
        } else {
            actor.sender().sendMessage(minimessage(messages.featureDisabled, "feature" to feat.displayName))
        }
    }

    @CommandPlaceholder
    fun defaultCommand(actor: BukkitCommandActor) {
        openGui(actor)
    }

    @Subcommand("gui")
    @CommandPermission("bettersurvival.feature.gui")
    fun openGui(actor: BukkitCommandActor) {
        val player = actor.asPlayer() ?: return actor.sender().sendMessage("Only players can execute this command.")
        viewFrame.open(FeatureOverview::class.java, player)
    }
}