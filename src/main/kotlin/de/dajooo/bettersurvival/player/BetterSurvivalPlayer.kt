package de.dajooo.bettersurvival.player

import de.dajooo.bettersurvival.database.model.Home
import de.dajooo.bettersurvival.database.model.Homes
import de.dajooo.bettersurvival.database.model.PlayerEntity
import de.dajooo.kaper.extensions.mini
import de.dajooo.kaper.extensions.scoreboardManager
import de.dajooo.kaper.scoreboard.nametag.nameTag
import de.dajooo.kommons.koin.withKoin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class BetterSurvivalPlayer(
    val uuid: UUID,
    val player: Player = Bukkit.getPlayer(uuid) ?: error("Player nout found")
) : KoinComponent {
    constructor(player: Player) : this(player.uniqueId, player)

    private val luckPerms by inject<LuckPerms>()

    val name get() = player.name()
    val databasePlayer get() = PlayerEntity.findById(uuid) ?: error("Player is not registered in database")
    val luckPermsUser get() = luckPerms.userManager.getUser(uuid)
    val prefix = luckPermsUser?.cachedData?.metaData?.prefix?.mini() ?: Component.empty()
    val suffix = luckPermsUser?.cachedData?.metaData?.suffix?.mini() ?: Component.empty()
    val color = luckPermsUser?.cachedData?.metaData?.getMetaValue("color")?.let { NamedTextColor.NAMES.value(it) } ?: NamedTextColor.GRAY

    fun applyNametag() {
        player.displayName(prefix.append(name).append(suffix))
        player.playerListName(prefix.append(name).append(suffix))
        player.nameTag {
            prefix = this@BetterSurvivalPlayer.prefix
            suffix = this@BetterSurvivalPlayer.suffix
            color = this@BetterSurvivalPlayer.color
        }
    }

    suspend fun homes() = newSuspendedTransaction { Home.find(Homes.player eq uuid).toList() }
    suspend fun home(name: String) = newSuspendedTransaction { Home.find(Homes.player.eq(uuid) and Homes.name.eq(name)).firstOrNull() }
    suspend fun upsertHomeLocation(name: String) = newSuspendedTransaction {
        Home.findSingleByAndUpdate(Homes.player.eq(player.uniqueId) and Homes.name.eq(name)) {
            it.location = player.location
        } ?: Home.new {
            this.name = name
            this.player = databasePlayer
            this.location = this@BetterSurvivalPlayer.player.location
        }
    }
}

val Player.survivalPlayer get() = withKoin { get<PlayerRegistry>().findPlayer(uniqueId) } ?: error("Player not registered in registry")