package map.together.mockActivities.auth

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_maps_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.activities.BaseFragmentActivity
import map.together.api.Api

import map.together.db.entity.MapEntity
import map.together.fragments.BaseFragment
import map.together.items.ItemsList
import map.together.repository.CurrentUserRepository
import map.together.utils.ResponseActions
import map.together.utils.recycler.adapters.MapsAdapter
import map.together.viewholders.MapViewHolder
import javax.net.ssl.HttpsURLConnection

class FakeMapsList : BaseFragment() {

    private val mapsInfo = ItemsList<MapEntity>(mutableListOf())

    override fun getFragmentLayoutId(): Int = R.layout.fragment_maps_list

    override fun getAppbarTitle(): Int = R.string.maps_list

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_bar.visibility = View.VISIBLE
        loadMapsAsync { mapsList ->
            mapsInfo.setData(mapsList)
            maps_list.layoutManager = LinearLayoutManager(context)
            maps_list.adapter = MapsAdapter(
                MapViewHolder::class,
                R.layout.map_list_item,
                mapsInfo,
                { mapEntity ->
                    (activity as BaseFragmentActivity).router?.showFakeMainPage(mapEntity.id)
                },
                { mapEntity ->
                    showDeletaAlertDialog(mapEntity)
                }
            )
        }

        imageView.setOnClickListener {
            addMapAsync { newMap: MapEntity ->
                mapsInfo.addLast(newMap)
                maps_list.smoothScrollToPosition(mapsInfo.size() - 1)
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        println("On create options menu")
        inflater.inflate(R.menu.map_menu, menu)

        val settingsBtn: MenuItem? = menu.findItem(R.id.settings_btn)
        settingsBtn?.setOnMenuItemClickListener {
            println("Open settings fragment!")
            router?.showSettingsPage()
            true
        }
    }

    private fun addMapAsync(callback: (MapEntity) -> Unit) {
        setLoading(true)
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.fakeCreateMap(getToken(), "Новая карта " + (mapsInfo.size() + 1)).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        requireContext(),
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_BAD_REQUEST
                    ) { createdMap ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val database = (activity as BaseFragmentActivity).database!!
                            val mapEntity = createdMap?.toMapEntity()
                            mapEntity?.let {
                                mapEntity.id = database.mapDao().insert(mapEntity)
                            }
                            withContext(Dispatchers.Main) {
                                callback.invoke(mapEntity!!)
                                setLoading(false)
                            }
                        }
                    }
                },
                {
                    ResponseActions.onFail(it, requireContext())
                    setLoading(false)
                }
            )
        )
    }

    private fun removeMapAsync(mapId: Long, callback: () -> Unit) {
        setLoading(true)
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.fakeremoveMap(getToken(), mapId).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        requireContext(),
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_UNAUTHORIZED
                    ) { removedMap ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val database = (activity as BaseFragmentActivity).database!!
                            database.mapDao().getById(removedMap!!.id)
                            withContext(Dispatchers.Main) {
                                callback.invoke()
                                setLoading(false)
                            }
                        }
                    }
                },
                {
                    ResponseActions.onFail(it, requireContext())
                    setLoading(false)
                }
            )
        )
    }

    private fun leaveMapAsync(mapId: Long, callback: () -> Unit) {
        setLoading(true)
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.fakeleaveMap(getToken(), mapId).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        requireContext(),
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_FORBIDDEN
                    ) { removedMap ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val database = (activity as BaseFragmentActivity).database!!
                            database.mapDao().getById(removedMap!!.id)
                            withContext(Dispatchers.Main) {
                                callback.invoke()
                                setLoading(false)
                            }
                        }
                    }
                },
                {
                    ResponseActions.onFail(it, requireContext())
                    setLoading(false)
                }
            )
        )
    }

    private fun loadMapsAsync(onLoaded: (MutableList<MapEntity>) -> Unit) {
        setLoading(true)
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.fakegetMyMaps(getToken(), "").subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        requireContext(),
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_FORBIDDEN
                    ) { actualMaps ->
                        GlobalScope.launch(Dispatchers.IO) {
                            val database = (activity as BaseFragmentActivity).database!!
                            var currentMaps = database.mapDao().getAll().toMutableList()
                            actualMaps?.let {
                                currentMaps.forEach { currentMap ->
                                    database.mapDao().delete(currentMap)
                                }
                                currentMaps =
                                    actualMaps.map { map -> map.toMapEntity() }.toMutableList()
                                val ids = database.mapDao().insert(currentMaps)
                                currentMaps.forEachIndexed { index, mapEntity ->
                                    mapEntity.id = ids[index]
                                }
                            }
                            withContext(Dispatchers.Main) {
                                onLoaded.invoke(currentMaps)
                                setLoading(false)
                            }
                        }
                    }
                },
                {
                    ResponseActions.onFail(it, requireContext())
                    setLoading(false)
                }
            )
        )
    }

    fun showDeletaAlertDialog(map: MapEntity) {
        if (map.canDelete) {
            AlertDialog.Builder(requireContext()).setMessage(
                getText(R.string.map_remove_alert).toString()
                    .replace("%s", map.name)
            )
                .setPositiveButton(
                    R.string.remove,
                    { dialogInterface: DialogInterface, i: Int -> deleteMap(map) })
                .setNegativeButton(
                    R.string.cancel,
                    { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() })
                .create().show()
        } else {
            AlertDialog.Builder(requireContext()).setMessage(
                getText(R.string.map_leave_alert).toString()
                    .replace("%s", map.name)
            )
                .setPositiveButton(
                    R.string.remove,
                    { dialogInterface: DialogInterface, i: Int -> leaveMap(map) })
                .setNegativeButton(
                    R.string.cancel,
                    { dialogInterface: DialogInterface, i: Int -> dialogInterface.cancel() })
                .create().show()
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