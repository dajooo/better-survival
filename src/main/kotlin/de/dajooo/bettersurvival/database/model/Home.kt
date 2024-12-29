package de.dajooo.bettersurvival.database.model

import de.dajooo.bettersurvival.database.delegates.location
import de.dajooo.kommons.exposed.nanoid.NanoIdEntity
import de.dajooo.kommons.exposed.nanoid.NanoIdTable
import de.dajooo.kommons.nanoid.NanoId
import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.dao.id.EntityID

object Homes : NanoIdTable("homes") {
    val name = varchar("name", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val world = varchar("world", 255)
    val yaw = float("yaw")
    val pitch = float("pitch")
    val player = reference("player", Players)
}

class Home(id: EntityID<NanoId>) : NanoIdEntity(id) {
    var name by Homes.name
    var x by Homes.x
    var y by Homes.y
    var z by Homes.z
    var world by Homes.world
    var yaw by Homes.yaw
    var pitch by Homes.pitch
    var player by PlayerEntity referencedOn Homes.player

    var location by location(Homes.world, Homes.x, Homes.y, Homes.z, Homes.yaw, Homes.pitch)
}