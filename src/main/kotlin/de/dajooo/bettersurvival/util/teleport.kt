package de.dajooo.bettersurvival.util

import io.papermc.paper.entity.TeleportFlag
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause

interface TeleportConfigurable {
    var keepPassengers: Boolean
    var keepVehicle: Boolean
}

fun flagsFromConfig(player: Player, location: Location, config: TeleportConfigurable) = if(config.keepPassengers || config.keepVehicle && player.world.name == location.world.name) {
    val flags = mutableSetOf<TeleportFlag>()
    if(config.keepPassengers) {
        flags.add(TeleportFlag.EntityState.RETAIN_PASSENGERS)
    }
    if(config.keepVehicle) {
        flags.add(TeleportFlag.EntityState.RETAIN_VEHICLE)
    }
    flags.toTypedArray()
} else emptyArray<TeleportFlag>()

fun Player.teleportAsync(location: Location, cause: TeleportCause, config: TeleportConfigurable) {
    teleportAsync(location, cause, *flagsFromConfig(this, location, config))
}

fun Player.teleport(location: Location, cause: TeleportCause, config: TeleportConfigurable) {
    teleport(location, cause, *flagsFromConfig(this, location, config))
}