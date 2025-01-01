package de.dajooo.bettersurvival.listeners

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.player.PlayerRegistry
import de.dajooo.kaper.event.MiniMessageBuildEvent
import de.dajooo.kaper.extensions.*
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object VisualsListener: Listener, KoinComponent {
    private val messages by inject<MessageConfig>()
    private val playerRegistry by inject<PlayerRegistry>()

    @EventHandler
    fun miniMessageBuilderHook(event: MiniMessageBuildEvent) {
        event.builder.editTags {
            it.resolver("prefix" toParsed messages.prefix)
        }
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        val survivalPlayer = playerRegistry.registerPlayer(event.player)
        survivalPlayer.applyNametag()
        event.joinMessage(messages.joinMessage, "name" to event.player.displayName())
    }

    @EventHandler
    fun handlePlayerQuit(event: PlayerQuitEvent) {
        event.quitMessage(messages.leaveMessage, "name" to event.player.displayName())
    }

    @EventHandler
    fun handlePlayerChat(event: AsyncChatEvent) {
        event.format(messages.chatFormat, "name" to event.player.name())
    }

    @EventHandler
    fun handleServerListPing(event: ServerListPingEvent) {
        event.motd(minimessage(messages.motd))
    }
}