package de.dajooo.bettersurvival.database.model

import de.dajooo.bettersurvival.database.delegates.location
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Players: UUIDTable("players", "players") {
    val name = varchar("name", 16)
    val lastPositionX = double("lastPositionX")
    val lastPositionY = double("lastPositionY")
    val lastPositionZ = double("lastPositionZ")
    val lastPositionWorld = varchar("lastPositionWorld", 16)
    val lastPositionYaw = float("lastPositionYaw")
    val lastPositionPitch = float("lastPositionPitch")
}

class PlayerEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerEntity>(Players)

    var name by Players.name
    var lastPosition by location(
        Players.lastPositionWorld,
        Players.lastPositionX,
        Players.lastPositionY,
        Players.lastPositionZ,
        Players.lastPositionYaw,
        Players.lastPositionPitch
    )
}
