package de.dajooo.bettersurvival.commands

import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.updater.Updater
import de.dajooo.kaper.extensions.not
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission

@Command("bettersurvival", "survival", "bs")
@CommandPermission("bettersurvival.command")
object BetterSurvivalCommand: KoinComponent {
    private val plugin: BetterSurvivalPlugin by inject()

    @Subcommand("version", "v")
    @CommandPermission("bettersurvival.command.version")
    fun version(actor: BukkitCommandActor) {
        actor.sender().sendMessage(!"<prefix>You are running BetterSurvival <yellow>${plugin.pluginMeta.version}</yellow>!")
    }

    @Subcommand("reload", "rl")
    @CommandPermission("bettersurvival.command.reload")
    fun reload(actor: BukkitCommandActor) {
        plugin.reloadConfig()
        actor.sender().sendMessage(!"<prefix><green>Config has been reloaded!")
    }

    @Subcommand("update")
    @CommandPermission("bettersurvival.command.update")
    suspend fun update(actor: BukkitCommandActor) {
        if (!Updater.updateAvailable()) {
            actor.sender().sendMessage(!"<prefix><green>You are already on the latest version!")
            return
        }
        actor.sender().sendMessage(!"<prefix>Starting update...")
        Updater.update()
        actor.sender().sendMessage(!"<prefix><green>Update was completed. Restart your server now for the changes to apply!")
    }
}