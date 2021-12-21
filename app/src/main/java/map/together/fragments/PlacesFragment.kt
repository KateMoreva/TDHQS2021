package map.together.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
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
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader


class PlacesFragment : BaseFragment() {
    private val placesInfo: MutableList<PlaceItem> = mutableListOf()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_tags_list

    override fun getAppbarTitle(): Int = R.string.places_list

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
        search_text_clear.setOnClickListener {
            search_text_field.setText("")
        }
        search_button_id.setOnClickListener {
            if (search_text_field.text.toString().isNotEmpty()) {
                placesInfo.filter { placeItem ->
                    placeItem.name == search_text_field.text.toString() ||
                            placeItem.layer_name == search_text_field.text.toString() ||
                            placeItem.address == search_text_field.text.toString()
                }
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