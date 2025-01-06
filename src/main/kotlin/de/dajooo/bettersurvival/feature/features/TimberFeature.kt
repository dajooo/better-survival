package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.MaterialTags
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.kaper.extensions.not
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Leaves
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.koin.core.component.inject

class TimberFeature : AbstractFeature<TimberFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var limit: Int = 512,
    ): FeatureConfig

    private val plugin by inject<BetterSurvivalPlugin>()

    override val typedConfig = config(Config())
    override val name = "timber"
    override val displayName = !"<gold>Timber</gold>"
    override val description = !"<gray>Breaks connected logs and leaves.</gray>"

    private val logToLeaveMap = mapOf(
        Material.SPRUCE_LOG to Material.SPRUCE_LEAVES,
        Material.ACACIA_LOG to Material.ACACIA_LEAVES,
        Material.BIRCH_LOG to Material.BIRCH_LEAVES,
        Material.DARK_OAK_LOG to Material.DARK_OAK_LEAVES,
        Material.JUNGLE_LOG to Material.JUNGLE_LEAVES,
        Material.OAK_LOG to Material.OAK_LEAVES,
        Material.MANGROVE_LOG to Material.MANGROVE_LEAVES,
    )

   @EventHandler
   fun handleBockBreak(event: BlockBreakEvent) {
        if (!MaterialSetTag.LOGS.isTagged(event.block.type)) return
        if (!event.player.isSneaking) return
        val logs = event.block.connectedBlocks()
        val leaves = logs.connectedLeaves(event.block.type)
        if (logs.size <= 1) return
        val item = event.player.equipment.itemInMainHand
        plugin.launch {
            var lastY = 0
            logs.sortedBy { it.location.blockY }.forEach { block ->
                if (!MaterialSetTag.LOGS.isTagged(block.type)) return@forEach
                if (lastY != block.y) delay(2.ticks)
                block.breakNaturallyWithToolBreaking(event.player, item, true)
                lastY = block.y
            }
            delay(5.ticks)
            var brokenBlocks = 0
            leaves.shuffled().forEach { block ->
                if (!MaterialSetTag.LEAVES.isTagged(block.type)) return@forEach
                val leave = block.blockData as? Leaves ?: return@forEach
                if(leave.distance < leave.maximumDistance) return@forEach
                if(leave.isPersistent) return@forEach
                if (brokenBlocks % 10 == 0) delay(1.ticks)
                block.breakNaturallyWithToolBreaking(event.player, item, false)
                brokenBlocks++
            }
        }
    }

    private fun Block.connectedBlocks(): List<Block> {
        val blocks = mutableListOf<Block>()
        blocks.add(this)
        val queue = mutableListOf(this)
        while (queue.isNotEmpty()) {
            if (blocks.size > config.limit) return blocks
            val block = queue.removeAt(0)
            block.neighbors.forEach { neighborBlock ->
                if (neighborBlock.type == type && !blocks.contains(neighborBlock)) {
                    blocks.add(neighborBlock)
                    queue.add(neighborBlock)
                }
            }
        }
        return blocks
    }

    private fun List<Block>.connectedLeaves(type: Material): List<Block> {
        val blocks = mutableListOf<Block>()
        val queue = mutableListOf<Block>()
        queue.addAll(this)
        while (queue.isNotEmpty()) {
            if (this.size + blocks.size > config.limit) return blocks
            val block = queue.removeAt(0)
            block.neighbors.forEach { neighborBlock ->
                if (logToLeaveMap[type] == neighborBlock.type && !blocks.contains(neighborBlock)) {
                    blocks.add(neighborBlock)
                    queue.add(neighborBlock)
                }
            }
        }
        return blocks
    }

    private fun Block.breakNaturallyWithToolBreaking(player: Player, tool: ItemStack, triggerEffect: Boolean) {
        val canToolBeDamaged = MaterialSetTag.LOGS.isTagged(this.type) &&
                MaterialTags.AXES.isTagged(tool.type) &&
                !tool.itemMeta.isUnbreakable &&
                tool.itemMeta is Damageable
        if (!canToolBeDamaged) {
            this.breakNaturally(tool, triggerEffect)
            return
        }
        player.damageItemStack(tool, 1)
        this.breakNaturally(tool, triggerEffect)
    }

    private val Block.neighbors: Set<Block>
        get() = LinkedHashSet<Block>().apply {
            (- 1 until 2).forEach { x ->
                (- 1 until 2).forEach { y ->
                    (- 1 until 2).forEach { z ->
                        this += getRelative(x, y, z)
                    }
                }
            }
        }
}