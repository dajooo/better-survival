package de.dajooo.bettersurvival.util

import io.papermc.paper.entity.TeleportFlag
import kotlinx.coroutines.future.await
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause

interface TeleportConfigurable {
    var keepPassengers: Boolean
    var keepVehicle: Boolean
}

fun flagsFromConfig(player: Player, location: Location, config: TeleportConfigurable) =
    if (player.world == location.world) {
        val flags = mutableSetOf<TeleportFlag>()
        if (config.keepPassengers) {
            flags.add(TeleportFlag.EntityState.RETAIN_PASSENGERS)
        }
        if (config.keepVehicle) {
            flags.add(TeleportFlag.EntityState.RETAIN_VEHICLE)
        }
        flags.toTypedArray()
    } else emptyArray<TeleportFlag>()

suspend fun Player.teleportSuspending(location: Location, cause: TeleportCause, config: TeleportConfigurable): Boolean {
    return if (isInsideVehicle && config.keepVehicle) {
        vehicle?.teleportAsync(location, cause, TeleportFlag.EntityState.RETAIN_PASSENGERS)?.await() ?: false
    } else {
        teleportAsync(location, cause, *flagsFromConfig(this, location, config)).await()
    }
}