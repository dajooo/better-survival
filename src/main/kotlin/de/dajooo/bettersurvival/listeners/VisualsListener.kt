package de.dajooo.bettersurvival.listeners

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.kaper.event.MiniMessageBuildEvent
import de.dajooo.kaper.extensions.minimessage
import de.dajooo.kaper.extensions.to
import de.dajooo.kaper.extensions.toParsed
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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
}