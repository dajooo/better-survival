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

    @Subcommand("reload", "rl")
    @CommandPermission("bettersurvival.command.reload")
    fun reload(actor: BukkitCommandActor) {
        plugin.reloadConfig()
        actor.sender().sendMessage("<red>Config has been reloaded!")
    }

    @Subcommand("update")
    @CommandPermission("bettersurvival.command.update")
    suspend fun update(actor: BukkitCommandActor) {
        if (!Updater.updateAvailable()) {
            actor.sender().sendMessage(!"<green>You are already on the latest version!")
        }
        actor.sender().sendMessage(!"<red>Starting update...")
        Updater.update()
        actor.sender().sendMessage(!"<red>Update was completed. Restart your server now for the changes to apply!")
    }
}