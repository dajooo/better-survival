package de.dajooo.bettersurvival.listeners

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.kaper.event.MiniMessageBuildEvent
import de.dajooo.kaper.extensions.mini
import de.dajooo.kaper.extensions.minimessage
import de.dajooo.kaper.extensions.to
import de.dajooo.kaper.extensions.toParsed
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.future.await
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture


object VisualsListener: Listener, KoinComponent {
    private val messages by inject<MessageConfig>()
    private val luckPerms by inject<LuckPerms>()

    @EventHandler
    fun miniMessageBuilderHook(event: MiniMessageBuildEvent) {
        event.builder.editTags {
            it.resolver("prefix" toParsed messages.prefix)
        }
    }

    @EventHandler
    fun handlePlayerJoin(event: PlayerJoinEvent) {
        val userManager = luckPerms.userManager
        val user = userManager.getUser(event.player.uniqueId)
        event.player.displayName(
            user?.cachedData?.metaData?.prefix?.mini()?.append(event.player.name())?.append {
                if (user.cachedData.metaData.suffix != null)
                       user.cachedData.metaData.suffix!!.mini()
                else
                       Component.empty()
            })
        event.player.playerListName(
            user?.cachedData?.metaData?.prefix?.mini()?.append(event.player.name())?.append {
                if (user.cachedData.metaData.suffix != null)
                    user.cachedData.metaData.suffix!!.mini()
                else
                    Component.empty()
            })
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