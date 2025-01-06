package de.dajooo.bettersurvival.feature.features

import com.destroystokyo.paper.MaterialSetTag
import de.dajooo.bettersurvival.feature.AbstractFeature
import de.dajooo.bettersurvival.feature.FeatureConfig
import de.dajooo.kaper.area.area
import de.dajooo.kaper.extensions.addXZ
import de.dajooo.kaper.extensions.isTagged
import de.dajooo.kaper.extensions.not
import de.dajooo.kaper.extensions.remXZ
import kotlinx.serialization.Serializable
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerToggleSneakEvent
import kotlin.random.Random

class SaplingTwerkFeature : AbstractFeature<SaplingTwerkFeature.Config>() {
    @Serializable
    data class Config(
        override var enabled: Boolean = true,
        var chance: Int = 10,
    ) : FeatureConfig

    override val name = "sapling-twerk"
    override val displayName = !"<gold>Sapling Twerk</gold>"
    override val description = !"<gray>Make saplings grow faster by twerking at them</gray>"
    override val typedConfig = config(Config())

    @EventHandler
    fun handleSneak(event: PlayerToggleSneakEvent) {
        if (event.isSneaking) {
            val area = area(event.player.location.clone() addXZ 2, event.player.location.clone() remXZ 2)
            area.blocks(event.player.world)
                .filter { MaterialSetTag.SAPLINGS.isTagged(it) }
                .forEach {
                    if (config.chance < Random.nextInt(0, 100)) return@forEach
                    it.applyBoneMeal(BlockFace.UP)
                }
        }
    }
}