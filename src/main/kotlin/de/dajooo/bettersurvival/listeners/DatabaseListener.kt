package de.dajooo.bettersurvival.listeners

import de.dajooo.bettersurvival.database.model.PlayerEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseListener: Listener {

    @EventHandler
    suspend fun handlePlayerJoin(event: PlayerJoinEvent) {
        newSuspendedTransaction {
            PlayerEntity.findByIdAndUpdate(event.player.uniqueId) {
                it.name = event.player.name
            } ?: PlayerEntity.new(event.player.uniqueId) {
                this.name = event.player.name
                this.lastPosition = event.player.location
            }
        }
    }

    @EventHandler
    suspend fun handlePlayerQuit(event: PlayerQuitEvent) {
        /*newSuspendedTransaction {
            PlayerEntity.findByIdAndUpdate(event.player.uniqueId) {
                it.lastPosition = event.player.location
            }
        }*/
    }
    @EventHandler
    suspend fun handlePlayerDeath(event: PlayerDeathEvent) {
        newSuspendedTransaction {
            PlayerEntity.findByIdAndUpdate(event.player.uniqueId) {
                it.deathPosition = event.player.location
            }
        }
    }


}
