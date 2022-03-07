package map.together.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_maps_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.api.Api
import map.together.db.entity.MapEntity
import map.together.fragments.BaseFragment
import map.together.items.ItemsList
import map.together.repository.CurrentUserRepository
import map.together.utils.ResponseActions
import map.together.utils.recycler.adapters.MapsAdapter
import map.together.viewholders.MapViewHolder
import javax.net.ssl.HttpsURLConnection


class MapsListFragment : BaseFragment() {

    private val mapsInfo = ItemsList<MapEntity>(mutableListOf())

    override fun getFragmentLayoutId(): Int = R.layout.fragment_maps_list

    override fun getAppbarTitle(): Int = R.string.maps_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_bar.visibility = View.VISIBLE
        loadMapsAsync { mapsList ->
            mapsInfo.setData(mapsList)
            progress_bar.visibility = View.INVISIBLE
            maps_list.layoutManager = LinearLayoutManager(context)
            maps_list.adapter = MapsAdapter(
                    MapViewHolder::class,
                    R.layout.map_list_item,
                    mapsInfo,
                    { mapEntity ->
                        (activity as BaseFragmentActivity).router?.showMainPage(mapEntity.id)
                    },
                    { mapEntity ->
                        showDeletaAlertDialog(mapEntity)
                    }
            )
        }

        imageView.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            addMapAsync { newMap: MapEntity ->
                mapsInfo.addLast(newMap)
                maps_list.smoothScrollToPosition(mapsInfo.size() - 1)
                progress_bar.visibility = View.INVISIBLE
            }
        }

    }

    private fun addMapAsync(callback: (MapEntity) -> Unit) {
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.createMap(getToken(), "Новая карта " + (mapsInfo.size() + 1)).subscribe(
                {ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_BAD_REQUEST) { createdMap ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = (activity as BaseFragmentActivity).database!!
                        val mapEntity = createdMap?.toMapEntity()
                        mapEntity?.let {
                            mapEntity.id = database.mapDao().insert(mapEntity)
                        }
                        withContext(Dispatchers.Main) {
                            callback.invoke(mapEntity!!)
                        }
                    }
                } },
                {ResponseActions.onFail(it, requireContext())}
            )
        )
    }

    private fun removeMapAsync(mapId: Long, callback: () -> Unit) {
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.removeMap(getToken(), mapId).subscribe(
                {ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_UNAUTHORIZED) { removedMap ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = (activity as BaseFragmentActivity).database!!
                        database.mapDao().getById(removedMap!!.id)
                        withContext(Dispatchers.Main) {
                            callback.invoke()
                        }
                    }
                } },
                {ResponseActions.onFail(it, requireContext())}
            )
        )
    }

    private fun leaveMapAsync(mapId: Long, callback: () -> Unit) {
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.leaveMap(getToken(), mapId).subscribe(
                {ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_FORBIDDEN) { removedMap ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = (activity as BaseFragmentActivity).database!!
                        database.mapDao().getById(removedMap!!.id)
                        withContext(Dispatchers.Main) {
                            callback.invoke()
                        }
                    }
                } },
                {ResponseActions.onFail(it, requireContext())}
            )
        )
    }

    private fun loadMapsAsync(onLoaded: (MutableList<MapEntity>) -> Unit) {
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.getMyMaps(getToken(), "").subscribe(
                {ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_FORBIDDEN) { actualMaps ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val database = (activity as BaseFragmentActivity).database!!
                        var currentMaps = database.mapDao().getAll().toMutableList()
                        actualMaps?.let {
                            currentMaps.forEach { currentMap ->
                                database.mapDao().delete(currentMap)
                            }
                            currentMaps = actualMaps.map { map -> map.toMapEntity() }.toMutableList()
                            val ids = database.mapDao().insert(currentMaps)
                            currentMaps.forEachIndexed { index, mapEntity ->
                                mapEntity.id = ids[index]
                            }
                        }
                        withContext(Dispatchers.Main) { onLoaded.invoke(currentMaps) }
                    }
                } },
                {ResponseActions.onFail(it, requireContext())}
            )
        )
    }

    fun showDeletaAlertDialog(map: MapEntity) {
        if (map.canDelete) {
            AlertDialog.Builder(requireContext()).setMessage(getText(R.string.map_remove_alert).toString()
                    .replace("%s", map.name))
                .setPositiveButton(
                    R.string.remove,
                    { dialogInterface: DialogInterface, i: Int -> deleteMap(map) })
                .setNegativeButton(
                    R.string.cancel,
                    { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }).create().show()
        }
        else
        {
            AlertDialog.Builder(requireContext()).setMessage(getText(R.string.map_leave_alert).toString()
                    .replace("%s", map.name))
                .setPositiveButton(
                    R.string.remove,
                    { dialogInterface: DialogInterface, i: Int -> leaveMap(map) })
                .setNegativeButton(
                    R.string.cancel,
                    { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() }).create().show()
        }
    }

    private fun deleteMap(map: MapEntity) {
        removeMapAsync(map.serverId) {
            mapsInfo.remove(map)
        }
    }

    private fun leaveMap(map: MapEntity) {
        leaveMapAsync(map.serverId) {
            mapsInfo.remove(map)
        }
    }

    private fun getToken(): String = CurrentUserRepository.getCurrentUserToken(requireContext())!!
}