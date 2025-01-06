package de.dajooo.bettersurvival.feature.features

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.feature.features.CustomRecipesFeature.Config
import de.dajooo.kaper.extensions.mini
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.onlinePlayers
import de.dajooo.kaper.extensions.to
import io.papermc.paper.event.player.PlayerDeepSleepEvent
import kotlinx.serialization.Serializable
import org.bukkit.event.EventHandler
import org.koin.core.component.inject

class BetterBedsFeature: AbstractFeature<BetterBedsFeature.Config>() {
    private val messages by inject<MessageConfig>()

    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var minPercentage: Int = 50,
        var morningTime: Long = 1000L,
    ) : FeatureConfig

    override val name = "better-beds"
    override val displayName = !"<gold>Better Beds</gold>"
    override val description = !"<gray>Skip the night when a percentage of players are sleeping, no need for everyone to sleep!</gray>"
    override val typedConfig = config(Config())

    @EventHandler
    fun handleDeepSleep(event: PlayerDeepSleepEvent) {
        val fullySleepingPlayers = onlinePlayers.filter { it.isDeeplySleeping }
        val onlinePlayersInWorld = onlinePlayers.filter { it.world == event.player.world }
        val sleepingPercentage = fullySleepingPlayers.size.toDouble() / onlinePlayersInWorld.size * 100
        onlinePlayers.forEach {
            it.sendMessage(
                messages.playersSleeping.mini(
                    "sleeping" to fullySleepingPlayers.size,
                    "max_sleeping" to onlinePlayersInWorld.size,
                    "percentage" to sleepingPercentage.toInt(),
                    "world" to event.player.world.name
                )
            )
        }
        if (sleepingPercentage >= (config.minPercentage)) {
            event.player.world.time = config.morningTime
            onlinePlayers.forEach {
                it.sendMessage(messages.morning.mini("world" to event.player.world.name))
            }
        }
    }
}