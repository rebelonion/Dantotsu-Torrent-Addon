package eu.kanade.tachiyomi.data.torrentServer

import android.util.Log
import eu.kanade.tachiyomi.data.torrentServer.model.Torrent
import eu.kanade.tachiyomi.data.torrentServer.model.TorrentRequest
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import uy.kohesive.injekt.injectLazy

object TorrentServerApi {
    private val client: OkHttpClient by injectLazy()
    private val hostUrl = TorrentServerUtils.hostUrl

    fun echo(): String {
        return try {
            client.newCall(GET("$hostUrl/echo")).execute().body.string()
        } catch (e: Exception) {
            ""
        }
    }

    fun shutdown(): String {
        return try {
            client.newCall(GET("$hostUrl/shutdown")).execute().body.string()
        } catch (e: Exception) {
            ""
        }
    }

    // / Torrents
    fun addTorrent(
        link: String,
        title: String,
        poster: String,
        data: String,
        save: Boolean,
    ): Torrent {
        val req =
            TorrentRequest(
                "add",
                link = link,
                title = title,
                poster = poster,
                data = data,
                save_to_db = save,
            ).toString()
        val resp =
            client.newCall(
                POST("$hostUrl/torrents", body = req.toRequestBody("application/json".toMediaTypeOrNull())),
            ).execute()
        val body = resp.body.string()
        Log.e("TorrentServerApi", "addTorrent: $body")
        Log.e("TorrentServerApi", "addTorrent: ${resp.code}")
        return Json.decodeFromString(Torrent.serializer(), body)
    }

    fun getTorrent(hash: String): Torrent {
        val req = TorrentRequest("get", hash).toString()
        val resp =
            client.newCall(
                POST("$hostUrl/torrents", body = req.toRequestBody("application/json".toMediaTypeOrNull())),
            ).execute()
        return Json.decodeFromString(Torrent.serializer(), resp.body.string())
    }

    fun remTorrent(hash: String) {
        val req = TorrentRequest("rem", hash).toString()
        client.newCall(
            POST("$hostUrl/torrents", body = req.toRequestBody("application/json".toMediaTypeOrNull())),
        ).execute()
    }

    fun listTorrent(): List<Torrent> {
        val req = TorrentRequest("list").toString()
        val resp =
            client.newCall(
                POST("$hostUrl/torrents", body = req.toRequestBody("application/json".toMediaTypeOrNull())),
            ).execute()
        return Json.decodeFromString<List<Torrent>>(resp.body.string())
    }

}