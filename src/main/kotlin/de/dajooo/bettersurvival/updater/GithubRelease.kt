package de.dajooo.bettersurvival.updater

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRelease(
    val url: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("assets_url") val assetsUrl: String,
    @SerialName("upload_url") val uploadUrl: String,
    @SerialName("tarball_url") val tarballUrl: String? = null,
    @SerialName("zipball_url") val zipballUrl: String? = null,
    val id: Int,
    @SerialName("node_id") val nodeId: String,
    @SerialName("tag_name") val tagName: String,
    @SerialName("target_commitish") val targetCommitish: String,
    val name: String? = null,
    val body: String? = null,
    val draft: Boolean,
    val prerelease: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("published_at") val publishedAt: String? = null,
    val author: SimpleUser,
    val assets: List<ReleaseAsset>,
    @SerialName("body_html") val bodyHtml: String? = null,
    @SerialName("body_text") val bodyText: String? = null,
    @SerialName("mentions_count") val mentionsCount: Int? = null,
    @SerialName("discussion_url") val discussionUrl: String? = null,
    val reactions: ReactionRollup? = null
)

@Serializable
data class SimpleUser(
    val name: String? = null,
    val email: String? = null,
    val login: String,
    val id: Long,
    @SerialName("node_id") val nodeId: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("gravatar_id") val gravatarId: String? = null,
    val url: String,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("followers_url") val followersUrl: String,
    @SerialName("following_url") val followingUrl: String,
    @SerialName("gists_url") val gistsUrl: String,
    @SerialName("starred_url") val starredUrl: String,
    @SerialName("subscriptions_url") val subscriptionsUrl: String,
    @SerialName("organizations_url") val organizationsUrl: String,
    @SerialName("repos_url") val reposUrl: String,
    @SerialName("events_url") val eventsUrl: String,
    @SerialName("received_events_url") val receivedEventsUrl: String,
    val type: String,
    @SerialName("site_admin") val siteAdmin: Boolean,
    @SerialName("starred_at") val starredAt: String? = null,
    @SerialName("user_view_type") val userViewType: String? = null
)

@Serializable
data class ReleaseAsset(
    val url: String,
    @SerialName("browser_download_url") val browserDownloadUrl: String,
    val id: Int,
    @SerialName("node_id") val nodeId: String,
    val name: String,
    val label: String? = null,
    val state: String,
    @SerialName("content_type") val contentType: String,
    val size: Int,
    @SerialName("download_count") val downloadCount: Int,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val uploader: SimpleUser? = null
)

@Serializable
data class ReactionRollup(
    val url: String,
    @SerialName("total_count") val totalCount: Int,
    @SerialName("+1") val plus1: Int,
    @SerialName("-1") val minus1: Int,
    val laugh: Int,
    val confused: Int,
    val heart: Int,
    val hooray: Int,
    val eyes: Int,
    val rocket: Int
)