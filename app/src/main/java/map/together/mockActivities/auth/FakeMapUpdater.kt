package map.together.mockActivities.auth

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import map.together.api.Api
import map.together.db.AppDatabase
import map.together.dto.db.MapInfoDto
import map.together.utils.ResponseActions
import java.net.HttpURLConnection
import java.util.*

class FakeMapUpdater(
    private val updateIntervalMs: Long,
    private val token: String,
    private val mapId: Long,
    private val context: Context,
    private val taskContainer: CompositeDisposable,
    private val database: AppDatabase,
    private val onMapChanged: (MapInfoDto) -> Unit = {},
) {

    private var mTimer: Timer? = null

    fun start() {
        if (mTimer == null) {
            mTimer = Timer()
            mTimer?.scheduleAtFixedRate(
                object : TimerTask() {
                    override fun run() = fetchUpdates()
                },
                100000, updateIntervalMs
            )
        }
    }

    fun stop() {
        mTimer?.let {
            it.cancel()
            mTimer = null
        }
    }

    private fun fetchUpdates() {
        val map = database.mapDao().getById(mapId)
        taskContainer.add(
            Api.getMapInfo(token, map.serverId).subscribe(
                {
                    ResponseActions.onResponse(
                        it, context,
                        HttpURLConnection.HTTP_OK,
                        HttpURLConnection.HTTP_FORBIDDEN
                    ) { mapInfo ->
                        println(mapInfo)
                        onMapChanged.invoke(mapInfo!!)
                    }
                },
                { ResponseActions.onFail(it, context) }
            )
        )
    }

}