package de.dajooo.bettersurvival.config

import de.dajooo.kommons.loadYamlConfig
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class MessageConfig(
    val prefix: String = "<gradient:dark_green:green><bold>BetterSurvival</bold></gradient> <gray>",
    val noPermission: String = "<prefix><red>You don't have permission to do that!",
    val featureNotFound: String = "<red><yellow><feature></yellow> has not been found!</red>",
    val featureEnabled: String = "<feature> <gray>has been <green>enabled</green>!</gray>",
    val featureDisabled: String = "<feature> <gray>has been <red>disabled</red>!</gray>",
)

fun loadMessageConfig(basePath: Path) = loadYamlConfig(basePath.resolve("messages.yaml"), MessageConfig())