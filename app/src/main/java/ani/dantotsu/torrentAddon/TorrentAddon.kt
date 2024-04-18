package ani.dantotsu.torrentAddon

import ani.dantotsu.addons.torrent.TorrentAddonApi
import eu.kanade.tachiyomi.data.torrentServer.TorrentServerApi
import eu.kanade.tachiyomi.data.torrentServer.TorrentServerUtils
import eu.kanade.tachiyomi.data.torrentServer.model.Torrent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TorrentAddon : TorrentAddonApi {

    /*override fun startServer(path: String) {
        if (!ServerService.isRunning(app)) {
            ServerService.start(app)
            ServerService.wait(10)
            TorrentServerUtils.setTrackersList()
        }
    }*/
    override fun startServer(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            server.Server.start(path, "", "", "", "", false, false, false)
            delay(10000L)
            TorrentServerUtils.setTrackersList()
        }
    }


    /*override suspend fun stopServer(app: Application) {
        if (ServerService.isRunning(app)) {
            try {
                ServerService.stop(app)
            } catch (ignored: Exception) {
            }
        }
    }*/

    override fun stopServer() {
        server.Server.stop()
        TorrentServerApi.shutdown()
    }

    override fun echo(): String {
        return TorrentServerApi.echo()
    }

    override fun removeTorrent(torrent: String) {
        TorrentServerApi.remTorrent(torrent)
    }

    override fun addTorrent(
        link: String,
        title: String,
        poster: String,
        data: String,
        save: Boolean,
    ): Torrent {
        return TorrentServerApi.addTorrent(link, title, poster, data, save)
    }

    override fun getLink(torrent: Torrent, index: Int): String {
        return TorrentServerUtils.getTorrentPlayLink(torrent, index)
    }
}