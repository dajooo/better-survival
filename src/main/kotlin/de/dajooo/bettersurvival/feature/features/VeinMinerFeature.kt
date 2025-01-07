package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.MaterialTags
import com.github.shynixn.mccoroutine.bukkit.launch
import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.bettersurvival.util.expiringBuffer
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.onlinePlayers
import de.dajooo.kaper.extensions.tagKeyFor
import de.dajooo.kaper.extensions.timerTask
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.scheduler.BukkitTask
import org.koin.core.component.inject


class VeinMinerFeature : AbstractFeature<VeinMinerFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var limit: Int = 32,
        var minableBlocks: List<Material> = MaterialTags.ORES.values.toList(),
    ) : FeatureConfig

    private val plugin by inject<BetterSurvivalPlugin>()

    override val name = "vein-miner"
    override val displayName = !"<gold>Vein Minder</gold>"
    override val description = !"<gray>Break whole ore veins</gray>"
    override val typedConfig = config(Config())

    private val playerActionbarBuffer = expiringBuffer<Player>()

    private val minableBlocksSetTag = MaterialSetTag(tagKeyFor("vein_miner_blocks"), config.minableBlocks)

    override fun onTickAsync(tick: Int) {
        onlinePlayers.forEach { player ->
            val targetBlock = player.getTargetBlock(setOf(Material.AIR), 5)
            if (minableBlocksSetTag.isTagged(targetBlock.type) && player.isSneaking) {
                player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} blocks</red>")
                playerActionbarBuffer.add(player)
                return@forEach
            }
            if (playerActionbarBuffer.contains(player) && (!MaterialSetTag.LOGS.isTagged(targetBlock.type) || !player.isSneaking)) {
                player.sendActionBar(Component.empty())
                playerActionbarBuffer.remove(player)
            }
        }
    }

    @EventHandler
    fun handleMove(event: PlayerMoveEvent) {
        val targetBlock = event.player.getTargetBlockExact(5) ?: return
        if (minableBlocksSetTag.isTagged(targetBlock.type) && event.player.isSneaking && targetBlock.isPreferredTool(event.player.inventory.itemInMainHand)) {
            event.player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} blocks</red>")
            playerActionbarBuffer.add(event.player)
            return
        }
        if (playerActionbarBuffer.contains(event.player) && (!minableBlocksSetTag.isTagged(targetBlock.type) || !event.player.isSneaking)) {
            event.player.sendActionBar(Component.empty())
            playerActionbarBuffer.remove(event.player)
        }
    }

    @EventHandler
    fun handleToggleSneak(event: PlayerToggleSneakEvent) {
        val targetBlock = event.player.getTargetBlockExact(5) ?: return

        if (event.isSneaking && minableBlocksSetTag.isTagged(targetBlock.type) && targetBlock.isPreferredTool(event.player.inventory.itemInMainHand)) {
            event.player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} blocks</red>")
            playerActionbarBuffer.add(event.player)
        }
        if (!event.isSneaking && playerActionbarBuffer.contains(event.player)) {
            event.player.sendActionBar(Component.empty())
            playerActionbarBuffer.remove(event.player)
        }
    }
    private val processingBlocks = HashSet<Block>()

    @EventHandler
    fun handleBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        if (processingBlocks.contains(block)) return
        if (!minableBlocksSetTag.isTagged(block.type)) return
        if (!event.player.isSneaking) return
        if (!block.isPreferredTool(event.player.inventory.itemInMainHand)) return

        val blocks = block.connectedBlocks().take(config.limit)
        if (blocks.size <= 1) return

        event.isCancelled = true

        plugin.launch {
            blocks.forEach { connectedBlock ->
                if (!minableBlocksSetTag.isTagged(connectedBlock.type)) return@forEach
                processingBlocks.add(connectedBlock)
                event.player.breakBlock(connectedBlock)
                processingBlocks.remove(connectedBlock)
            }
        }
    }

    private fun Block.connectedBlocks(): List<Block> {
        val blocks = HashSet<Block>().apply { add(this@connectedBlocks) }
        val queue = ArrayDeque<Block>().apply { add(this@connectedBlocks) }

        while (queue.isNotEmpty() && blocks.size < config.limit) {
            queue.removeFirst().neighbors.forEach { neighborBlock ->
                if (blocks.size >= config.limit) return blocks.toList()
                if (neighborBlock.type == type && blocks.add(neighborBlock)) {
                    queue.add(neighborBlock)
                }
            }
        }
        return blocks.toList()
    }

    private val Block.neighbors: Set<Block>
        get() = buildSet(27) {
            for (x in -1..1)
                for (y in -1..1)
                    for (z in -1..1)
                        add(getRelative(x, y, z))
        }
}