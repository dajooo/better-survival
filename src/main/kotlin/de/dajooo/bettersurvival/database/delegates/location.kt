package de.dajooo.bettersurvival.database.delegates

import io.papermc.paper.math.BlockPosition
import io.papermc.paper.math.FinePosition
import io.papermc.paper.math.Position
import org.bukkit.Bukkit
import org.bukkit.Location
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.Column
import kotlin.reflect.KProperty

class LocationDelegate(
    private val world: Column<String>,
    private val x: Column<Double>,
    private val y: Column<Double>,
    private val z: Column<Double>,
    private val yaw: Column<Float>,
    private val pitch: Column<Float>
) {
    operator fun <ID : Comparable<ID>> getValue(entity: Entity<ID>, property: KProperty<*>): Location {
        return Location(
            entity.run { Bukkit.getWorld(world.getValue(this, property)) },
            entity.run { x.getValue(this, property) },
            entity.run { y.getValue(this, property) },
            entity.run { z.getValue(this, property) },
            entity.run { yaw.getValue(this, property) },
            entity.run { pitch.getValue(this, property) },
        )
    }

    operator fun <ID : Comparable<ID>> setValue(entity: Entity<ID>, property: KProperty<*>, value: Location) =
        entity.apply {
            world.setValue(entity, property, value.world.name)
            x.setValue(entity, property, value.x)
            y.setValue(entity, property, value.y)
            z.setValue(entity, property, value.z)
            yaw.setValue(entity, property, value.yaw)
            pitch.setValue(entity, property, value.pitch)
        }
}

class NullableLocationDelegate(
    private val world: Column<String?>,
    private val x: Column<Double?>,
    private val y: Column<Double?>,
    private val z: Column<Double?>,
    private val yaw: Column<Float?>,
    private val pitch: Column<Float?>
) {
    operator fun <ID : Comparable<ID>> getValue(entity: Entity<ID>, property: KProperty<*>): Location? {
        return entity.run { world.getValue(this, property) }?.let { world ->
            Location(
                Bukkit.getWorld(world),
                entity.run { x.getValue(this, property) }!!,
                entity.run { y.getValue(this, property) }!!,
                entity.run { z.getValue(this, property) }!!,
                entity.run { yaw.getValue(this, property) }!!,
                entity.run { pitch.getValue(this, property) }!!,
            )
        }
    }

    operator fun <ID : Comparable<ID>> setValue(entity: Entity<ID>, property: KProperty<*>, value: Location?) =
        entity.apply {
            world.setValue(entity, property, value?.world?.name)
            x.setValue(entity, property, value?.x)
            y.setValue(entity, property, value?.y)
            z.setValue(entity, property, value?.z)
            yaw.setValue(entity, property, value?.yaw)
            pitch.setValue(entity, property, value?.pitch)
        }
}

class FinePositionDelegate(
    private val x: Column<Double>,
    private val y: Column<Double>,
    private val z: Column<Double>,
) {
    operator fun <ID : Comparable<ID>> getValue(entity: Entity<ID>, property: KProperty<*>): FinePosition {
        return Position.fine(
            entity.run { x.getValue(this, property) },
            entity.run { y.getValue(this, property) },
            entity.run { z.getValue(this, property) },
        )
    }

    operator fun <ID : Comparable<ID>> setValue(entity: Entity<ID>, property: KProperty<*>, value: FinePosition) =
        entity.apply {
            x.setValue(entity, property, value.x())
            y.setValue(entity, property, value.y())
            z.setValue(entity, property, value.z())
        }
}

class BlockPositionDelegate(
    private val x: Column<Int>,
    private val y: Column<Int>,
    private val z: Column<Int>,
) {
    operator fun <ID : Comparable<ID>> getValue(entity: Entity<ID>, property: KProperty<*>): BlockPosition {
        return Position.block(
            entity.run { x.getValue(this, property) },
            entity.run { y.getValue(this, property) },
            entity.run { z.getValue(this, property) },
        )
    }

    operator fun <ID : Comparable<ID>> setValue(entity: Entity<ID>, property: KProperty<*>, value: BlockPosition) =
        entity.apply {
            x.setValue(entity, property, value.blockX())
            y.setValue(entity, property, value.blockY())
            z.setValue(entity, property, value.blockZ())
        }
}

fun location(
    world: Column<String>,
    x: Column<Double>,
    y: Column<Double>,
    z: Column<Double>,
    yaw: Column<Float>,
    pitch: Column<Float>
) = LocationDelegate(world, x, y, z, yaw, pitch)

fun nullableLocation(
    world: Column<String?>,
    x: Column<Double?>,
    y: Column<Double?>,
    z: Column<Double?>,
    yaw: Column<Float?>,
    pitch: Column<Float?>
) = NullableLocationDelegate(world, x, y, z, yaw, pitch)

fun finePosition(x: Column<Double>, y: Column<Double>, z: Column<Double>) = FinePositionDelegate(x, y, z)
fun blockPosition(x: Column<Int>, y: Column<Int>, z: Column<Int>) = BlockPositionDelegate(x, y, z)