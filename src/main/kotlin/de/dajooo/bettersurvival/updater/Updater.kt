package de.dajooo.bettersurvival.updater

import de.dajooo.bettersurvival.BetterSurvivalPlugin
import de.dajooo.bettersurvival.config.Config
import de.dajooo.bettersurvival.config.UpdateChannel
import io.github.oshai.kotlinlogging.KLogger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.semver4j.Semver
import kotlin.io.path.*

object Updater : KoinComponent {
    private val plugin by inject<BetterSurvivalPlugin>()
    private val config by inject<Config>()
    private val logger by inject<KLogger>()
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private suspend fun fetchLatestRelease(): GithubRelease? {
        return runCatching {
            httpClient.get("https://api.github.com/repos/dajooo/better-survival/releases/latest") {
                accept(ContentType("application", "vnd.github.v3+json"))
                bearerAuth("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")
                header("X-GitHub-Api-Version", "2022-11-28")
            }.body<GithubRelease>()
        }.getOrNull()
    }

    private suspend fun fetchLatestPrerelease(channel: UpdateChannel): GithubRelease? {
        return httpClient.get("https://api.github.com/repos/dajooo/better-survival/releases") {
            accept(ContentType("application", "vnd.github.v3+json"))
            header("X-GitHub-Api-Version", "2022-11-28")
        }.body<List<GithubRelease>>().filter {
            if (channel == UpdateChannel.BETA) Semver.parse(it.tagName)?.preRelease?.first() == "beta"
            else Semver.parse(it.tagName)?.preRelease?.first() == "alpha"
        }.firstOrNull { it.prerelease }
    }

    suspend fun fetchReleaseByChannel(channel: UpdateChannel): GithubRelease? {
        return if (config.updateChannel == UpdateChannel.RELEASE) {
            fetchLatestRelease()
        } else fetchLatestPrerelease(channel)
    }

    suspend fun updateAvailable(): Boolean {
        val release = fetchReleaseByChannel(config.updateChannel)
        if (release == null) {
            logger.debug { "No release found" }
            return false
        }
        return Semver.parse(release.tagName)?.isGreaterThan(plugin.pluginMeta.version) ?: false
    }

    suspend fun update() {
        val release = fetchReleaseByChannel(config.updateChannel)
        if (release == null) {
            logger.debug { "No release found" }
            return
        }
        val asset = release.assets.first { it.name.endsWith(".jar") && it.name.endsWith("-all.jar") }
        val downloadUrl = asset.browserDownloadUrl
        val oldPlugin = plugin.javaClass.protectionDomain.codeSource.location.toURI().toPath()
        val file = plugin.dataPath.resolve(asset.name)
        val rootDir = Path(".")
        val httpResponse = httpClient.get(Url(downloadUrl)) {
            onDownload { bytesSentTotal, contentLength ->
                logger.debug { "Received $bytesSentTotal bytes from $contentLength" }
            }
        }
        val responseBody: ByteArray = httpResponse.body()
        file.writeBytes(responseBody)
        logger.debug { "A file saved to ${file.relativeTo(rootDir)}" }
        oldPlugin.moveTo(oldPlugin.parent.resolve("${oldPlugin.fileName}.old"))
        logger.debug { "Moved old plugin to ${oldPlugin.relativeTo(rootDir)}.old" }
    }
}