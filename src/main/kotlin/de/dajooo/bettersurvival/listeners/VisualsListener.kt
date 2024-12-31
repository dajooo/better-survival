package de.dajooo.bettersurvival.listeners

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.kaper.event.MiniMessageBuildEvent
import de.dajooo.kaper.extensions.minimessage
import de.dajooo.kaper.extensions.to
import de.dajooo.kaper.extensions.toParsed
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object VisualsListener: Listener, KoinComponent {
    private val messages by inject<MessageConfig>()

    @EventHandler
    fun miniMessageBuilderHook(event: MiniMessageBuildEvent) {
        event.builder.editTags {
            it.resolver("prefix" toParsed messages.prefix)
        }
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        val displayName = minimessage("<gray><name>", "name" to event.player.name)
        event.player.displayName(displayName)
        event.player.playerListName(displayName)
        event.joinMessage(minimessage(messages.joinMessage, "name" to event.player.displayName()))
    }

    @EventHandler
    fun handlePlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage(minimessage(messages.leaveMessage, "name" to event.player.displayName()))
    }

    @EventHandler
    fun handlePlayerChat(event: AsyncChatEvent) {
        event.renderer { _, sourceDisplayName, message, _ ->
            minimessage(messages.chatFormat, "display_name" to sourceDisplayName, "message" to message, "name" to event.player.name())
        }
    }

    @EventHandler
    fun handleServerListPing(event: ServerListPingEvent) {
        event.motd(minimessage(messages.motd))
    }
}