package de.dajooo.bettersurvival.feature.features

import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.util.toByteArray
import de.dajooo.bettersurvival.util.uuidFromBytes
import de.dajooo.kaper.extensions.*
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
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
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.UUID
import kotlin.uuid.toKotlinUuid

class GraveFeature : AbstractFeature<GraveFeature.Config>() {
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
        val items = event.drops

        deathLoc.block.type = Material.CHEST
        val chest = deathLoc.block.state as Chest

        val serializedItems = items.map { it.serializeAsBytes() }.toTypedArray()

        chest.persistentDataContainer.apply {
            set(keyFor("death_chest_items"), PersistentDataType.BYTE_ARRAY, serializeItemArray(serializedItems))
            set(keyFor("death_chest_owner"), PersistentDataType.BYTE_ARRAY, event.player.uniqueId.toByteArray())
        }
        chest.update()

        event.drops.clear()

        val holoLoc = deathLoc.toBlockLocation().clone().add(0.5, 1.25, 0.5)
        val display = deathLoc.world.spawn(holoLoc, TextDisplay::class.java)

        display.text(!"<gold>${event.entity.name}'s Death Chest")
        display.billboard = Display.Billboard.CENTER
    }

    @EventHandler
    fun handleChestOpen(event: InventoryOpenEvent) {
        if (event.inventory.holder is Chest) {
            val chest = (event.inventory.holder as Chest)
            if (chest.persistentDataContainer.has(keyFor("death_chest_items"))) {
                event.isCancelled = true

                val itemsData = chest.persistentDataContainer.get(keyFor("death_chest_items"), PersistentDataType.BYTE_ARRAY)!!
                val items = deserializeItemArray(itemsData)

                val inv = DeathChestHolder(chest).inventory
                items.forEachIndexed { index, item -> inv.setItem(index, item) }
                event.player.openInventory(inv)
            }
        }
    }

    @EventHandler
    fun handleChestClose(event: InventoryCloseEvent) {
        if (event.inventory.holder is DeathChestHolder) {
            val chest = (event.inventory.holder as DeathChestHolder).chest
            val serializedItems = event.inventory.contents.filterNotNull().map { it.serializeAsBytes() }.toTypedArray()
            chest.persistentDataContainer.set(keyFor("death_chest_items"), PersistentDataType.BYTE_ARRAY, serializeItemArray(serializedItems))
            chest.update()
        }
    }

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {
        if (event.block.type !== Material.CHEST) {
            return
        }

        val chest = event.block.state as Chest
        if (!chest.persistentDataContainer.has(keyFor("death_chest_items"))) {
            return
        }

        val chestOwner = uuidFromBytes(chest.persistentDataContainer.get(keyFor("death_chest_owner"), PersistentDataType.BYTE_ARRAY)!!)
        if (chestOwner != event.player.uniqueId && !event.player.hasPermission("bettersurvival.graves.break")) {
            event.isCancelled = true
            event.player.sendMessage(!"<red>You can't break this chest!")
            return
        }

        val itemsData = chest.persistentDataContainer.get(keyFor("death_chest_items"), PersistentDataType.BYTE_ARRAY)!!
        val items = deserializeItemArray(itemsData)
        items.forEach { item ->
            event.block.world.dropItemNaturally(event.block.location, item)
        }

        val holoLoc = event.block.location.clone().add(0.5, 1.25, 0.5)
        holoLoc.getNearbyEntitiesByType(TextDisplay::class.java, 0.1).forEach { it.remove() }
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

    private fun serializeItemArray(items: Array<ByteArray>): ByteArray {
        return ByteArrayOutputStream().use { byteStream ->
            DataOutputStream(byteStream).use { dataStream ->
                dataStream.writeInt(items.size)
                items.forEach { itemData ->
                    dataStream.writeInt(itemData.size)
                    dataStream.write(itemData)
                }
            }
            byteStream.toByteArray()
        }
    }

    private fun deserializeItemArray(data: ByteArray): Array<ItemStack> {
        return ByteArrayInputStream(data).use { byteStream ->
            DataInputStream(byteStream).use { dataStream ->
                val size = dataStream.readInt()
                Array(size) {
                    val itemSize = dataStream.readInt()
                    val itemData = ByteArray(itemSize)
                    dataStream.readFully(itemData)
                    ItemStack.deserializeBytes(itemData)
                }
            }
        }
    }

    class DeathChestHolder(val chest: Chest) : InventoryHolder {
        override fun getInventory(): Inventory {
            return Bukkit.createInventory(this, 45, Component.text("Death Chest"))
        }
    }
}