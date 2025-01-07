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
import de.dajooo.kaper.extensions.tagKeyFor
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.koin.core.component.inject

class VeinMinerFeature : AbstractFeature<VeinMinerFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var limit: Int = 32,
    ) : FeatureConfig

    private val plugin by inject<BetterSurvivalPlugin>()

    override val name = "vein-miner"
    override val displayName = !"<gold>Vein Minder</gold>"
    override val description = !"<gray>Break whole ore veins</gray>"
    override val typedConfig = config(Config())

    private val playerActionbarBuffer = expiringBuffer<Player>()

    @EventHandler
    fun handleMove(event: PlayerMoveEvent) {
        val targetBlock = event.player.getTargetBlock(setOf(Material.AIR), 5)
        if (MaterialTags.ORES.isTagged(targetBlock.type) && event.player.isSneaking && targetBlock.isPreferredTool(event.player.inventory.itemInMainHand)) {
            event.player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} blocks</red>")
            playerActionbarBuffer.add(event.player)
            return
        }
        if (playerActionbarBuffer.contains(event.player) && (!MaterialTags.ORES.isTagged(targetBlock.type) || !event.player.isSneaking)) {
            event.player.sendActionBar(Component.empty())
            playerActionbarBuffer.remove(event.player)
        }
    }

    @EventHandler
    fun handleToggleSneak(event: PlayerToggleSneakEvent) {
        val player = event.player
        val targetBlock = player.getTargetBlock(setOf(Material.AIR), 5)

        if (event.isSneaking && MaterialTags.ORES.isTagged(targetBlock.type) && targetBlock.isPreferredTool(event.player.inventory.itemInMainHand)) {
            player.sendActionBar(!"<red>Mining ${targetBlock.connectedBlocks().count()} blocks</red>")
            playerActionbarBuffer.add(player)
        }
        if (!event.isSneaking && playerActionbarBuffer.contains(player)) {
            player.sendActionBar(Component.empty())
            playerActionbarBuffer.remove(player)
        }
    }

    @EventHandler
    fun handleBockBreak(event: BlockBreakEvent) {
        if (!MaterialTags.ORES.isTagged(event.block.type)) return
        if (!event.player.isSneaking) return
        if (!event.block.isPreferredTool(event.player.inventory.itemInMainHand)) return
        val blocks = event.block.connectedBlocks()
        if (blocks.size <= 1) return
        val item = event.player.equipment.itemInMainHand
        plugin.launch {
            blocks.forEach { block ->
                if (!MaterialTags.ORES.isTagged(block.type)) return@forEach
                block.breakNaturallyWithToolBreaking(event.player, item, true)
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

    private fun Block.breakNaturallyWithToolBreaking(player: Player, tool: ItemStack, triggerEffect: Boolean) {
        if (!MaterialSetTag.LOGS.isTagged(this.type) ||
            !MaterialTags.PICKAXES.isTagged(tool.type) ||
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