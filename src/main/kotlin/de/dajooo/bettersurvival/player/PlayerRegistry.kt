package de.dajooo.bettersurvival.player

import de.dajooo.bettersurvival.feature.Feature
import org.bukkit.entity.Player
import java.util.UUID

class PlayerRegistry : MutableIterable<BetterSurvivalPlayer> {
    private val players: MutableList<BetterSurvivalPlayer> = mutableListOf()

    fun init(): PlayerRegistry {
        return this
    }

    fun findPlayer(uuid: UUID) = players.find { it.uuid == uuid }

    fun registerPlayer(player: BetterSurvivalPlayer) {
        players.add(player)
    }

    fun registerPlayer(player: Player) = BetterSurvivalPlayer(player).apply { players.add(this) }

    fun unregisterPlayer(player: BetterSurvivalPlayer) {
        players.remove(player)
    }

    override fun iterator() = players.iterator()
}