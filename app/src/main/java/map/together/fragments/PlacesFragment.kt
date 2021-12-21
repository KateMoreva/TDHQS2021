package map.together.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_tags_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.items.ItemsList
import map.together.items.PlaceItem
import map.together.utils.recycler.adapters.PlaceAdapter
import map.together.viewholders.PlaceViewHolder


class PlacesFragment : BaseFragment() {
    private val placesInfo: MutableList<PlaceItem> = mutableListOf()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_tags_list

    override fun getAppbarTitle(): Int = R.string.places_list

    data class TagInfo(
        val id: Long, val name: String, val address: String, val layer_name: String,
        val ownerId: Long
    )

    var nameTextView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layesIndexes = listOf<Long>(1)
        getPlaces(layesIndexes) { placesInfoRes ->
            val adapter = PlaceAdapter(
                holderType = PlaceViewHolder::class,
                layoutId = R.layout.tag_list_item,
                dataSource = ItemsList(placesInfo),
                onClick = {
                },
            )
            tags_list.adapter = adapter
            val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            tags_list.layoutManager = layoutManager

        }
    }

    fun getPlaces(
        layersId: List<Long>, actionsAfter: (
            List<PlaceItem>
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.let {
                for (layerId in layersId) {
                    val placesDao = it.placeDao().getByLayerId(layerId)
                    for (place in placesDao) {
                        val placeCategory = it.categoryDao().getById(place.categoryId)
                        val layer = it.layerDao().getById(layerId)
                        placesInfo.add(
                            PlaceItem(
                                place.id.toString(),
                                place.name,
                                place.name,
                                layer.name,
                                placeCategory.colorRecourse
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        actionsAfter.invoke(placesInfo)
                    }
                }
            }
        }
    }

//
//    private fun loadPlacesAsync(onLoaded: (MutableList<MapEntity>) -> Unit) {
//        (activity as BaseFragmentActivity).taskContainer.add(
//            Api.getPlaces(CurrentUserRepository.getCurrentUserToken(requireContext())!!, "").subscribe(
//                {
//                    ResponseActions.onResponse(it, requireContext(), HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_FORBIDDEN) { actualMaps ->
//                    GlobalScope.launch(Dispatchers.IO) {
//                        val database = (activity as BaseFragmentActivity).database!!
//                        var currentMaps = database.mapDao().getAll().toMutableList()
//                        actualMaps?.let {
//                            currentMaps.forEach { currentMap ->
//                                database.mapDao().delete(currentMap)
//                            }
//                            currentMaps = actualMaps.map { map -> map.toMapEntity() }.toMutableList()
//                            val ids = database.mapDao().insert(currentMaps)
//                            currentMaps.forEachIndexed { index, mapEntity ->
//                                mapEntity.id = ids[index]
//                            }
//                        }
//                        withContext(Dispatchers.Main) {
//                            onLoaded.invoke(currentMaps)
//                        }
//                    }
//                } },
//                { ResponseActions.onFail(it, requireContext())}
//            )
//        )
//    }

}