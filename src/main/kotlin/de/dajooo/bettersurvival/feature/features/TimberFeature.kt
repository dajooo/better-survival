package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.MaterialTags
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.util.expiringBuffer
import de.dajooo.kaper.extensions.not
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Leaves
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.koin.core.component.inject

class TimberFeature : AbstractFeature<TimberFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var limit: Int = 512,
        var maxLeaves: Int = 512,
    ) : FeatureConfig

    private val plugin by inject<BetterSurvivalPlugin>()

    override val name = "timber"
    override val displayName = !"<gold>Timber</gold>"
    override val description = !"<gray>Breaks connected logs and leaves.</gray>"
    override val typedConfig = config(Config())

    private val logToLeaveMap = mapOf(
        Material.SPRUCE_LOG to Material.SPRUCE_LEAVES,
        Material.ACACIA_LOG to Material.ACACIA_LEAVES,
        Material.BIRCH_LOG to Material.BIRCH_LEAVES,
        Material.DARK_OAK_LOG to Material.DARK_OAK_LEAVES,
        Material.JUNGLE_LOG to Material.JUNGLE_LEAVES,
        Material.OAK_LOG to Material.OAK_LEAVES,
        Material.MANGROVE_LOG to Material.MANGROVE_LEAVES,
    )

    private val playerActionbarBuffer = expiringBuffer<Player>()

    @EventHandler
    fun handleMove(event: PlayerMoveEvent) {
        val targetBlock = event.player.getTargetBlock(setOf(Material.AIR), 5)
        if (MaterialSetTag.LOGS.isTagged(targetBlock.type) && event.player.isSneaking) {
            event.player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} logs</red>")
            playerActionbarBuffer.add(event.player)
            return
        }
        if (playerActionbarBuffer.contains(event.player) && (!MaterialSetTag.LOGS.isTagged(targetBlock.type) || !event.player.isSneaking)) {
            event.player.sendActionBar(Component.empty())
            playerActionbarBuffer.remove(event.player)
        }
    }

    @EventHandler
    fun handleToggleSneak(event: PlayerToggleSneakEvent) {
        val player = event.player
        val targetBlock = player.getTargetBlock(setOf(Material.AIR), 5)

        if (event.isSneaking && MaterialSetTag.LOGS.isTagged(targetBlock.type)) {
            player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} logs</red>")
            playerActionbarBuffer.add(player)
        }
        if (!event.isSneaking && playerActionbarBuffer.contains(player)) {
            player.sendActionBar(Component.empty())
            playerActionbarBuffer.remove(player)
        }
    }

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
                if (leave.distance < leave.maximumDistance) return@forEach
                if (leave.isPersistent) return@forEach
                if (brokenBlocks % 10 == 0) delay(1.ticks)
                block.breakNaturallyWithToolBreaking(event.player, item, false)
                brokenBlocks++
            }
        }
    }

    private fun Block.connectedBlocks(): List<Block> {
        val blocks = HashSet<Block>().apply { add(this@connectedBlocks) }
        val queue = ArrayDeque<Block>().apply { add(this@connectedBlocks) }

        while (queue.isNotEmpty()) {
            queue.removeFirst().neighbors.forEach { neighborBlock ->
                if (blocks.size >= config.limit) return blocks.toList()
                if (neighborBlock.type == type && blocks.add(neighborBlock)) {
                    queue.add(neighborBlock)
                }
            }
        }
        return blocks.toList()
    }
    private fun List<Block>.connectedLeaves(type: Material): List<Block> {
        val blocks = HashSet<Block>()
        val queue = ArrayDeque(this)
        val targetType = logToLeaveMap[type]

        while (queue.isNotEmpty()) {
            queue.removeFirst().neighbors.forEach { neighborBlock ->
                if (this.size + blocks.size >= config.maxLeaves) return blocks.toList()
                if (targetType == neighborBlock.type && blocks.add(neighborBlock)) {
                    queue.add(neighborBlock)
                }
            }
        }
        return blocks.toList()
    }

    private fun Block.breakNaturallyWithToolBreaking(player: Player, tool: ItemStack, triggerEffect: Boolean) {
        if (!MaterialSetTag.LOGS.isTagged(this.type) ||
            !MaterialTags.AXES.isTagged(tool.type) ||
            tool.itemMeta?.isUnbreakable == true ||
            tool.itemMeta !is Damageable
        ) {
            this.breakNaturally(tool, triggerEffect)
            return
        }
        player.damageItemStack(tool, 1)
        this.breakNaturally(tool, triggerEffect)
    }

    private val Block.neighbors: Set<Block>
        get() = buildSet(27) {
            for (x in -1..1)
                for (y in -1..1)
                    for (z in -1..1)
                        add(getRelative(x, y, z))
        }
}