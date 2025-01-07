package de.dajooo.bettersurvival.feature.features

import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.util.toByteArray
import de.dajooo.bettersurvival.util.uuidFromBytes
import de.dajooo.kaper.extensions.*
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.block.DoubleChest
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType
import java.util.UUID
import kotlin.uuid.toKotlinUuid

class GraveFeature: AbstractFeature<GraveFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var allowedCropSeeds: List<Material> = listOf(
            Material.BEETROOT_SEEDS,
            Material.WHEAT_SEEDS,
            Material.POTATO,
            Material.CARROT,
            Material.NETHER_WART
        ),
    ) : FeatureConfig

    override val name = "grave"
    override val displayName = !"<gold>Graves</gold>"
    override val description = !"<gray>Spawn a grave where you die.</gray>"
    override val typedConfig = config(Config())

    @EventHandler
    fun handlePlayerDeath(event: PlayerDeathEvent) {
        val deathLoc = event.entity.location

        deathLoc.block.type = Material.CHEST

        val items = event.drops
        val chest = deathLoc.block.state as Chest
        chest.persistentDataContainer.set(keyFor("death_chest"), event.player.uniqueId.toByteArray())
        chest.update()

        if (items.size <= 27) {
            items.forEachIndexed { index, item ->
                chest.inventory.setItem(index, item)
            }
        } else {
            deathLoc.add(1.0, 0.0, 0.0).block.type = Material.CHEST

            val doubleChest = chest.inventory.holder as DoubleChest
            items.forEachIndexed { index, item ->
                doubleChest.inventory.setItem(index, item)
            }
        }

        event.drops.clear()

        val holoLoc = deathLoc.toBlockLocation().clone().add(0.5, 1.25, 0.5)
        val display = deathLoc.world.spawn(holoLoc, TextDisplay::class.java)

        display.text(!"<gold>${event.entity.name}'s Death Chest")
        display.billboard = Display.Billboard.CENTER
    }

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {
        if (event.block.type !== Material.CHEST) {
            return
        }
        val chest = event.block.state as Chest
        if (chest.persistentDataContainer.has(keyFor("death_chest"))) {
            val chestOwner = uuidFromBytes(chest.persistentDataContainer.get<ByteArray>(keyFor("death_chest"))!!)
            if (chestOwner != event.player.uniqueId || event.player.hasPermission("bettersurvival.graves.break")) {
                event.isCancelled = true
                event.player.sendMessage(!"<red>You can't break this chest!")
                return
            }
            val holoLoc = event.block.location.clone().add(0.5, 1.25, 0.5)
            holoLoc.getNearbyEntitiesByType(TextDisplay::class.java, 0.1).forEach { it.remove() }
        }
    }

    @EventHandler
    fun handleExplosion(event: BlockExplodeEvent) {
        event.blockList().removeIf { block ->
            if (block.type == Material.CHEST) {
                val chest = block.state as Chest
                chest.persistentDataContainer.has(keyFor("death_chest"))
            } else false
        }
    }

    @EventHandler
    fun handleEntityExplosion(event: EntityExplodeEvent) {
        event.blockList().removeIf { block ->
            if (block.type == Material.CHEST) {
                val chest = block.state as Chest
                chest.persistentDataContainer.has(keyFor("death_chest"))
            } else false
        }
    }

    @EventHandler
    fun handleMobGrief(event: EntityChangeBlockEvent) {
        val block = event.block
        if (block.type == Material.CHEST) {
            val chest = block.state as Chest
            if (chest.persistentDataContainer.has(keyFor("death_chest"))) {
                event.isCancelled = true
            }
        }
    }
}