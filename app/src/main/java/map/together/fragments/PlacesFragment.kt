package map.together.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_maps_list.*
import kotlinx.android.synthetic.main.fragment_tags_list.*
import kotlinx.android.synthetic.main.tag_list_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.activities.BaseFragmentActivity
import map.together.api.Api
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceEntity
import map.together.dto.db.PlaceDto
import map.together.items.ItemsList
import map.together.items.PlaceItem
import map.together.lifecycle.Page
import map.together.repository.CurrentUserRepository
import map.together.utils.ResponseActions
import map.together.utils.recycler.adapters.MapsAdapter
import map.together.utils.recycler.adapters.PlaceAdapter
import map.together.viewholders.MapViewHolder
import map.together.viewholders.PlaceViewHolder
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.net.ssl.HttpsURLConnection


class PlacesFragment : BaseFragment() {

    private var places: MutableList<PlaceDto> = mutableListOf()
    private var placesInfo: MutableList<PlaceItem> = mutableListOf()
    private var placesInfoOld: MutableList<PlaceItem> = mutableListOf()
    private var items = ItemsList<PlaceItem>(mutableListOf())

    override fun getFragmentLayoutId(): Int = R.layout.fragment_tags_list

    override fun getAppbarTitle(): Int = R.string.places_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val layesIndexes = (activity?.intent?.extras?.get(Page.LAYERS_IDS) as? LongArray)!!.toList()
        val mapId = (activity?.intent?.extras?.get(Page.MAP_ID_KEY) as? Long)!!

        loadPlacesAsync(mapId, layesIndexes) { placesList ->
            items.setData(placesInfo)
            val adapter = PlaceAdapter(
                holderType = PlaceViewHolder::class,
                layoutId = R.layout.tag_list_item,
                dataSource = items,
                onClick = {
                },
                onRemove = { item ->
                    val place = places.find { placeItem -> placeItem.id == item.id.toLong() }
                    if (place != null && place.canDelete) {
                        items.remove(item)
                    }
                }
            )
            tags_list.adapter = adapter
            val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            tags_list.layoutManager = layoutManager
        }

        search_text_clear.setOnClickListener {
            search_text_field.setText("")
        }
        search_button_id.setOnClickListener {
            if (search_text_field.text.toString().isNotEmpty()) {
                placesInfo = placesInfo.filter { placeItem ->
                    placeItem.name == search_text_field.text.toString() ||
                            placeItem.layer_name == search_text_field.text.toString() ||
                            placeItem.address == search_text_field.text.toString()
                }.toMutableList()
            } else {
                placesInfo = placesInfoOld
            }
        }

        load_button_id.setOnClickListener {
            if (placesInfo.size > 0) {
                val file = "places$layesIndexes"
                val data: String = placesInfo.toString()
                val fileOutputStream: FileOutputStream
                try {
                    fileOutputStream = requireActivity().openFileOutput(file, Context.MODE_PRIVATE)
                    fileOutputStream.write(data.toByteArray())
                    fileOutputStream.close()
                    var fileInputStream = requireActivity().openFileInput(file)
                    var inputStreamReader = InputStreamReader(fileInputStream)
                    val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                    val stringBuilder: StringBuilder = StringBuilder()
                    var text: String? = null
                    while ({ text = bufferedReader.readLine(); text }() != null) {
                        stringBuilder.append(text)
                    }
                    print(text)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

//    fun getPlaces(
//        layersId: List<Long>, actionsAfter: (
//            List<PlaceItem>
//        ) -> Unit
//    ) {
//        GlobalScope.launch(Dispatchers.IO) {
//            database?.let {
//                for (layerId in layersId) {
//                    val placesDao = it.placeDao().getByLayerId(layerId)
//                    for (place in placesDao) {
//
//                        val placeCategory = it.categoryDao().getById(place.categoryId)
//                        val layer = it.layerDao().getById(layerId)
//                        placesInfo.add(
//                            PlaceItem(
//                                place.id.toString(),
//                                place.name,
//                                place.name,
//                                layer.name,
//                                placeCategory.colorRecourse
//                            )
//                        )
//                    }
//                    placesInfoOld = placesInfo
//                    withContext(Dispatchers.Main) {
//                        actionsAfter.invoke(placesInfo)
//                    }
//                }
//            }
//        }
//    }


    private fun loadPlacesAsync(
        mapID: Long,
        layersIds: List<Long>,
        onLoaded: (MutableList<PlaceEntity>) -> Unit
    ) {
        (activity as BaseFragmentActivity).taskContainer.add(
            Api.getMapInfo(CurrentUserRepository.getCurrentUserToken(requireContext())!!, mapID)
                .subscribe(
                    {
                        ResponseActions.onResponse(
                            it,
                            requireContext(),
                            HttpsURLConnection.HTTP_OK,
                            HttpsURLConnection.HTTP_FORBIDDEN
                        ) { actualMap ->
                            GlobalScope.launch(Dispatchers.IO) {
                                val database = (activity as BaseFragmentActivity).database!!
//                        var currentMap = database.mapDao().getAll().toMutableList()
                                actualMap?.let {
//                            currentMaps.forEach { currentMap ->
//                                database.mapDao().delete(currentMap)
//                            }
//                            currentMaps = actualMaps.map { map -> map.toMapEntity() }.toMutableList()
//                            val ids = database.mapDao().insert(currentMaps)
//                            currentMaps.forEachIndexed { index, mapEntity ->
//                                mapEntity.id = ids[index]
//                            }

                                    val layers = actualMap.layers.filter { layerDto ->
                                        layersIds.contains(layerDto.id)
                                    }
                                    for (layer in layers) {
                                        val lplaces = layer.places
                                        places.addAll(lplaces)
                                        for (pl in lplaces) {
                                            placesInfo.add(
                                                PlaceItem(
                                                    pl.id.toString(),
                                                    pl.name,
                                                    pl.name,
                                                    layer.name,
                                                    pl.categoryColor
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    { ResponseActions.onFail(it, requireContext()) }
                )
        )
    }

}