package de.dajooo.bettersurvival.player

import de.dajooo.bettersurvival.config.MessageConfig
import de.dajooo.bettersurvival.database.model.Home
import de.dajooo.bettersurvival.database.model.Homes
import de.dajooo.bettersurvival.database.model.PlayerEntity
import de.dajooo.kaper.extensions.mini
import de.dajooo.kaper.extensions.to
import de.dajooo.kaper.scoreboard.nametag.nameTag
import de.dajooo.kommons.koin.withKoin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.util.*
import kotlin.jvm.optionals.getOrNull

class BetterSurvivalPlayer(
    val uuid: UUID,
    val player: Player = Bukkit.getPlayer(uuid) ?: error("Player nout found")
) : KoinComponent {
    constructor(player: Player) : this(player.uniqueId, player)

    private val luckPerms by inject<Optional<LuckPerms>>(named("LuckPerms"))
    private val messages by inject<MessageConfig>()

    val name get() = player.name()
    val databasePlayer get() = PlayerEntity.findById(uuid) ?: error("Player is not registered in database")
    val luckPermsUser get() = luckPerms.getOrNull()?.userManager?.getUser(uuid)
    val prefix = luckPermsUser?.cachedData?.metaData?.prefix?.mini() ?: Component.empty()
    val suffix = luckPermsUser?.cachedData?.metaData?.suffix?.mini() ?: Component.empty()
    val color = luckPermsUser?.cachedData?.metaData?.getMetaValue("color")?.let { NamedTextColor.NAMES.value(it) } ?: NamedTextColor.GRAY

    fun applyNametag() {
        player.displayName(messages.displayNameFormat.mini("player_prefix" to prefix, "player_suffix" to suffix, "player_name" to name, "player_color" to color))
        player.playerListName(messages.playerListNameFormat.mini("player_prefix" to prefix, "player_suffix" to suffix, "player_name" to name, "player_color" to color))
        player.nameTag {
            prefix = this@BetterSurvivalPlayer.prefix
            suffix = this@BetterSurvivalPlayer.suffix
            color = this@BetterSurvivalPlayer.color
        }
    }

    fun homes() = Home.find(Homes.player eq uuid).toList()
    fun home(name: String) = Home.find(Homes.player.eq(uuid) and Homes.name.eq(name)).firstOrNull()
    fun upsertHomeLocation(name: String, loc: Location) =
        Home.findSingleByAndUpdate(Homes.player.eq(player.uniqueId) and Homes.name.eq(name)) {
            it.location = loc
        } ?: Home.new {
            this.name = name
            this.player = databasePlayer
            this.location = loc
        }
}

val Player.survivalPlayer get() = withKoin { get<PlayerRegistry>().findPlayer(uniqueId) } ?: error("Player not registered in registry")