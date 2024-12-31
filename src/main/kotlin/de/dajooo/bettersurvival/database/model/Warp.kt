package de.dajooo.bettersurvival.database.model

import de.dajooo.bettersurvival.database.delegates.location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Warps : IntIdTable("warps", "warps") {
    val name = varchar("name", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val world = varchar("world", 255)
    val yaw = float("yaw")
    val pitch = float("pitch")
}

class Warp(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Warp>(Warps)

    var name by Warps.name

    var location by location(Warps.world, Warps.x, Warps.y, Warps.z, Warps.yaw, Warps.pitch)
}