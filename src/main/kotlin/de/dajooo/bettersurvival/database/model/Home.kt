package de.dajooo.bettersurvival.database.model

import de.dajooo.bettersurvival.database.delegates.location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Homes : IntIdTable("homes", "homes") {
    val name = varchar("name", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val world = varchar("world", 255)
    val yaw = float("yaw")
    val pitch = float("pitch")
    val player = reference("player", Players)
}

class Home(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Home>(Homes)

    var name by Homes.name
    var player by PlayerEntity referencedOn Homes.player

    var location by location(Homes.world, Homes.x, Homes.y, Homes.z, Homes.yaw, Homes.pitch)
}