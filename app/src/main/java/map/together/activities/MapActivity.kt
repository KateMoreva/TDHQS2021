package map.together.activities


import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.*
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.category_on_tap_fragment.*
import kotlinx.android.synthetic.main.item_layers_menu.*
import kotlinx.android.synthetic.main.item_layers_menu.layers_menu
import kotlinx.android.synthetic.main.item_menu.*
import kotlinx.android.synthetic.main.item_users.*
import kotlinx.coroutines.*
import map.together.R
import map.together.db.entity.CategoryEntity
import map.together.db.entity.LayerEntity
import map.together.db.entity.LayerMapEntity
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceCategoryEntity
import map.together.db.entity.PlaceEntity
import map.together.db.entity.UserEntity
import map.together.db.entity.UserMapEntity
import map.together.dto.db.LayerDto
import map.together.dto.db.MapDto
import map.together.dto.db.PlaceDto
import map.together.dto.db.UserMapDto
import map.together.fragments.dialogs.CategoryChoosingDialog
import map.together.fragments.dialogs.CategoryColorDialog
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.items.LayerItem
import map.together.items.SearchItem
import map.together.items.UserItem
import map.together.lifecycle.MapUpdater
import map.together.lifecycle.Page
import map.together.repository.CurrentUserRepository
import map.together.utils.RoleEnum
import map.together.utils.recycler.adapters.LayersAdapter
import map.together.utils.recycler.adapters.SearchResAdapter
import map.together.utils.recycler.adapters.UsersAdapter
import map.together.viewholders.LayerViewHolder
import map.together.viewholders.SearchViewHolder
import map.together.viewholders.UsersViewHolder
import kotlin.math.round
import kotlin.math.roundToInt


class MapActivity : AppbarActivity(), GeoObjectTapListener, InputListener, Session.SearchListener,
    CategoryColorDialog.CategoryDialogListener {
    val SPB = Point(59.9408455, 30.3131542)
    val layersList: ItemsList<LayerItem> = ItemsList(mutableListOf())
    var mapUpdater: MapUpdater? = null
    //TODO: loading from bundle
    var currentMapId = 1L
    val currentLayerId = 1L

    companion object {
        const val SHARED_PREFERENCE_LAST_MAP_ID = "LAST_MAP_ID"
        private var last_map_id: Long  = -1
        fun get_last_map(): Long
        {
            return last_map_id
        }
    }

    private val DRAGGABLE_PLACEMARK_CENTER = Point(59.948, 30.323)
    private val ANIMATED_PLACEMARK_CENTER = Point(59.948, 30.318)

    val currentPlaces: MutableList<PlaceEntity> = ArrayList()
    val currentAddress: MutableMap<Long, String> = HashMap()
    val currentGeoObjects: MutableMap<Long, GeoObject> = HashMap()
    val placeCategory: MutableMap<Long, CategoryItem> = HashMap()
    var polyline: Polyline = Polyline()
    var prevPolyline: Polyline = Polyline()
    var isLinePointClick = false
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    var geoSearch = true
    var preLoad = false
    var selectedLayerId: String = ""
    var selectedObjectId = ""
    var selectedObject: GeoObject? = null
    var loadingObjId = -1L
    val searchResults: MutableList<SearchItem> = ArrayList()
    val mapUsers: MutableList<UserItem> = ArrayList()
    val userLayer: MutableMap<Long, Long> = HashMap()
    var selectedPlaceCategory: CategoryItem? = null
    val layerPlaces = mutableListOf<PlaceEntity>()

    private val polylineListener = object : InputListener {
        override fun onMapLongTap(p0: Map, p1: Point) {}
        override fun onMapTap(p0: Map, p1: Point) {
            polyline.points.add(p1)
            p0.mapObjects.addPlacemark(p1)
            p0.mapObjects.addPolyline(Polyline(polyline.points))
        }
    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        if (!isLinePointClick) {
            val selectionMetadata = geoObjectTapEvent
                .geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            if (selectionMetadata != null) {
                mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
                val geo = geoObjectTapEvent.geoObject.geometry[0].point
                if (geo != null) {
                    val y = mapview.map.maxZoom.roundToInt()
                    geoSearch = true
                    searchSession = searchManager!!.submit(geo, y, SearchOptions(), this)
                    showTagMenu()
                }
            }
            return selectionMetadata != null
        }
        return false
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        //TODO: LOAD meta from sever
        currentMapId = (intent.extras?.get(Page.MAP_ID_KEY)) as Long
        val token = CurrentUserRepository.getCurrentUserToken(applicationContext)!!


        getUsers(currentMapId) { users ->
            mapUsers.addAll(userItemFromEntity(users))
            var usersList = ItemsList(mapUsers)
            val usersAdapter = UsersAdapter(
                holderType = UsersViewHolder::class,
                layoutId = R.layout.item_user,
                dataSource = usersList,
                onClick = { user ->
                    print("Layer $user clicked")
                },
                onRemove = { item ->
                    usersList.remove(item)
                },

                )

            users_list.adapter = usersAdapter
            val usersManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            users_list.layoutManager = usersManager
        }

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        search_text_field.visibility = View.INVISIBLE
        mapview.map.move(
            CameraPosition(SPB, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )

        getPlaces(currentLayerId) { places ->
            layerPlaces.addAll(places)
            drawPlaces(layerPlaces)
            category_on_tap_save_changes_id.setText(resources.getText(R.string.save))
//            for (place in places) {
//                val y = mapview.map.maxZoom.roundToInt()
//                geoSearch = true
//                loadingObjId = place.id
//                searchSession = searchManager!!.submit(
//                    Point(
//                        place.latitude.toDouble(),
//                        place.longitude.toDouble()
//                    ), y, SearchOptions(), this
//                )
//            }
            currentPlaces.addAll(layerPlaces)
        }

        last_map_id = getSharedPreferences(applicationContext.packageName, 0).getLong(
            SHARED_PREFERENCE_LAST_MAP_ID, 0)
        var map: MapEntity
        CoroutineScope(Dispatchers.IO).launch {
            try {
                map = database!!.mapDao().getById(last_map_id)
            } catch (ex: Exception) {
                    val main_malyer_id = database!!.layerDao().insert(LayerEntity("слой 1", userId))
                    val place_id = database!!.placeDao().getAll()[0].id
                    last_map_id = database!!.mapDao().insert(
                        MapEntity(
                            getString(R.string.new_map_name), place_id,
                            main_malyer_id, userId, true, true, "Админ", 1))
                    getSharedPreferences(applicationContext.packageName, 0).edit().putLong(
                        SHARED_PREFERENCE_LAST_MAP_ID, last_map_id)
                    map = database!!.mapDao().getById(last_map_id)
                    database!!.userMapDao().insert(UserMapEntity(userId!!, last_map_id, RoleEnum.ADMIN))
                database!!.layerMapDao().insert(LayerMapEntity(last_map_id, main_malyer_id))
            }
        }
        zoom_in_id.setOnClickListener(fun(_: View) {
            print("IN")
            mapview.map.move(
                CameraPosition(
                    mapview.map.cameraPosition.target,
                    mapview.map.cameraPosition.zoom.plus(1.0f), 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        })

        zoom_out_id.setOnClickListener(fun(_: View) {
            print("OUT")
            mapview.map.move(
                CameraPosition(
                    mapview.map.cameraPosition.target,
                    mapview.map.cameraPosition.zoom.minus(1.0f),
                    0.0f,
                    0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        })
        mapview.map.addTapListener(this)
        mapview.map.addInputListener(this)

        line_point.setOnClickListener(fun(_: View) {
            if (isLinePointClick) {
                prevPolyline = polyline
            }
            isLinePointClick = !isLinePointClick
            if (isLinePointClick) {
                polyline = Polyline(ArrayList<Point>())
                mapview.map.addInputListener(polylineListener)
            } else {
                mapview.map.removeInputListener(polylineListener)
                mapview.map.mapObjects.clear()
                for (obj in currentPlaces) {
                    val point = Point(obj.latitude.toDouble(), obj.longitude.toDouble())
                    val category = placeCategory.get(obj.id)
                    if (category != null) {
                        mapview.getMap().getMapObjects().addPlacemark(
                            Point(point.latitude, point.longitude),
                            ImageProvider.fromBitmap(drawSimpleBitmap(category.colorRecourse))
                        )
                    }
                }
            }
        })

        search_button_id.setOnClickListener(fun(view: View) {
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (search_text_field.visibility == View.INVISIBLE) {
                search_text_field.visibility = View.VISIBLE
                search_res_list.visibility = View.VISIBLE
                search_text_field.setSelection(0)
                imm.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED, 0)
            } else {
                search_text_field.visibility = View.INVISIBLE
                search_res_list.visibility = View.INVISIBLE
                search_text_clear.visibility = View.INVISIBLE
                search_text_field.setText("")
                searchResults.clear()
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                geoSearch = true
            }
        })
        search_text_clear.setOnClickListener {
            search_text_field.setText("")
            searchResults.clear()
        }

        search_text_field.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.length > 0) {
                    search_text_clear.visibility = View.VISIBLE
                }
                if (s.length > 2) {
                    geoSearch = false
                    val adapter = SearchResAdapter(
                        holderType = SearchViewHolder::class,
                        layoutId = R.layout.item_search_result,
                        dataSource = ItemsList(searchResults),
                        onClick = { item ->
                            if (item.coord.longitude != 0.0) {
                                val y = mapview.map.maxZoom.roundToInt()
                                geoSearch = true
                                searchSession = searchManager!!.submit(
                                    item.coord,
                                    y,
                                    SearchOptions(),
                                    this@MapActivity
                                )
                                search_text_field.visibility = View.INVISIBLE
                                search_res_list.visibility = View.GONE
                                searchResults.clear()
                                val imm =
                                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(
                                    this@MapActivity.currentFocus?.windowToken,
                                    0
                                )
                                showTagMenu()
                            }
                        },
                    )
                    search_res_list.adapter = adapter
                    val layoutManager =
                        LinearLayoutManager(this@MapActivity, RecyclerView.VERTICAL, false)
                    search_res_list.layoutManager = layoutManager
                    search_res_list.visibility = View.VISIBLE

                    dynamicSearch(s.toString())
                }
            }
        })

        val bottomSheetBehavior = from(layers_menu)
        bottomSheetBehavior.setState(STATE_HIDDEN);

        layers_btn.setOnClickListener {
            bottomSheetBehavior.isDraggable = true
            hide_menu_btn.visibility = View.GONE
            hide_menu_img.visibility = View.VISIBLE
            bottomSheetBehavior.setState(STATE_HALF_EXPANDED)
        }

        hide_menu_btn.setOnClickListener {
            bottomSheetBehavior.setState(STATE_HIDDEN)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = resizable_layers_menu.layoutParams
                val fullHeight = Resources.getSystem().getDisplayMetrics().heightPixels
                if (newState == STATE_EXPANDED) {
                    layoutParams.height = fullHeight - getNavigationBarHeight()
                    bottomSheetBehavior.isDraggable = false
                    hide_menu_btn.visibility = View.VISIBLE
                    hide_menu_img.visibility = View.GONE
                } else if (newState == STATE_HALF_EXPANDED) {
                    layoutParams.height = (fullHeight - getNavigationBarHeight()) / 2
                }
                resizable_layers_menu.layoutParams = layoutParams
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        open_button.setOnClickListener {
            router?.showMapsLibraryPage()
        }

        val tagBottomSheetBehavior = from(tag_edit_menu)
        tagBottomSheetBehavior.setState(STATE_HIDDEN);

        tagBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = tag_edit_menu.layoutParams
                val fullHeight = Resources.getSystem().getDisplayMetrics().heightPixels
                if (newState == STATE_HALF_EXPANDED) {
                    layoutParams.height = (fullHeight - getNavigationBarHeight()) / 2
                }
                tag_edit_menu.layoutParams = layoutParams
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


        val usersBottomSheetBehavior = from(users_edit_menu)
        usersBottomSheetBehavior.setState(STATE_HIDDEN);

        usersBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = users_edit_menu.layoutParams
                val fullHeight = Resources.getSystem().getDisplayMetrics().heightPixels
                if (newState == STATE_HALF_EXPANDED) {
                    layoutParams.height = (fullHeight - getNavigationBarHeight()) / 2
                }
                users_edit_menu.layoutParams = layoutParams
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        open_users.setOnClickListener {
            showUsersMenu()
        }

        val layers = mutableListOf(
            LayerItem("0", "Нажмите \"Показать всем\" для демонстрации", false, 0L, false, true),
            LayerItem("1", "Слой 1", true, 2, false),
            LayerItem("2", "Слой 2", false, 2, false)
        )
        layersList.setData(layers)
        val adapter = LayersAdapter(
            holderType = LayerViewHolder::class,
            layoutId = R.layout.item_layer,
                dataSource = layersList,
                onClick = { layer ->
                    print("Layer $layer clicked")
                    if (layer.isVisible && selectedLayerId != layer.id) {
                        layersList.items.forEach {
                            it.selected = false
                        }
                        layer.selected = true
                        selectedLayerId = layer.id
                    } else {
                        layer.isVisible = !layer.isVisible
                    }
                    layersList.rangeUpdate(0, layersList.size())
                },
                onRemove = {
                    // todo: check that user can delete this layer and delete it
                   layersList.remove(it)
                },
                onChangeCommonLayer = {
                    layersList.items.forEach {
                        if (it.ownerId != 0L)
                            it.disabled = !it.disabled
                    }
                    layersList.rangeUpdate(0, layersList.size())
                }
        )
        layers_list.adapter = adapter
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        layers_list.layoutManager = layoutManager

        hide_all_card.setOnClickListener {
            show_all_card.visibility = View.VISIBLE
            hide_all_card.visibility = View.INVISIBLE
            layersList.items.forEach {
                if (!it.disabled && it.ownerId != 0L)
                    it.isVisible = false
            }
            layersList.rangeUpdate(0, layersList.size())
        }

        go_to_places.setOnClickListener {
            //TODO: real layers
            val layersIds = listOf<Long>(1)
            router?.showPlacesPage(layersIds.toLongArray())
        }

        show_all_card.setOnClickListener {
            hide_all_card.visibility = View.VISIBLE
            show_all_card.visibility = View.INVISIBLE
            layersList.items.forEach {
                if (!it.disabled && it.ownerId != 0L)
                    it.isVisible = true
            }
            layersList.rangeUpdate(0, layersList.size())
        }

        add_layer_btn.setOnClickListener {
            val newLayer = LayerItem(layersList.size().toString(), "Новый слой " + layersList.size(), true, userId)
            layersList.addLast(newLayer)
            layers_list.smoothScrollToPosition(layersList.size() - 1)
        }

        demonstrate_card.setOnClickListener {
            stop_demonstrate_card.visibility = View.VISIBLE
            demonstrate_card.visibility = View.INVISIBLE
            // TODO: send these layers to server and wait for WS notification
            val itemsToDemonstrate = layersList.items.filter { it.isVisible }
            layersList.items.forEach {
                if (it.ownerId != 0L)
                    it.disabled = true
            }
            layersList.items[0].disabled = false
            layersList.items[0].isVisible = true
            layersList.items[0].title = "Демонстрационный слой"
            layersList.rangeUpdate(0, layersList.size())
        }

        stop_demonstrate_card.setOnClickListener {
            demonstrate_card.visibility = View.VISIBLE
            stop_demonstrate_card.visibility = View.INVISIBLE
            layersList.items.forEach {
                if (it.ownerId != 0L)
                    it.disabled = false
            }
            layersList.items[0].disabled = true
            layersList.items[0].isVisible = false
            layersList.items[0].title = "Нажмите \"Показать всем\" для демонстрации"
            layersList.rangeUpdate(0, layersList.size())
        }

        val bottomSheetBehavior2 = from(bottom_sheet)
        bottomSheetBehavior2.setState(STATE_HIDDEN);
        menu.setOnClickListener {
            bottomSheetBehavior2.setState(STATE_EXPANDED)
        }

        stop_demonstrate_card.visibility = View.INVISIBLE

        mapUpdater = MapUpdater(3000, token, currentMapId, applicationContext, taskContainer, database!!) { mapInfo ->
            // mapInfo.map -- done
            updateMap(mapInfo.map)

            // mapInfo.layers -- done
            updateLayers(mapInfo.layers)

            // mapInfo.users -- done
            updateUsers(mapInfo.users)

            // mapInfo.demonstrationLayers -- done
            updateDemonstration(mapInfo.demonstrationLayers, mapInfo.demonstrationTimestamp)
        }
        mapUpdater?.start()

    }

    private var mapDtoObject: MapDto? = null

    private fun updateMap(map: MapDto) {
        var updated = false
        if (mapDtoObject == null) {
            mapDtoObject = map
            updated = true
        }
        mapDtoObject?.let { currentMap ->
            if (currentMap.timestamp < map.timestamp) {
                mapDtoObject = map
                updated = true
            }
        }
        if (updated) {
            println("map dto object updated: $mapDtoObject")
        }
    }

    private var demonstrationLayersList: List<Long> = listOf()
    private var demonstrationTimestamp: Long = -1L

    private fun updateDemonstration(demonstrationLayers: List<Long>?, timestamp: Long) {
        if (timestamp <= demonstrationTimestamp) {
            return
        }
        demonstrationTimestamp = timestamp
        demonstrationLayersList = demonstrationLayers ?: emptyList()
        println("Demonstration layers updated: $demonstrationLayersList, timestamp: $demonstrationTimestamp")
        // todo: add demonstration layers logic here
    }

    private val usersList: MutableList<UserMapDto> = mutableListOf()

    private fun updateUsers(users: List<UserMapDto>) {
        val usersToRemove = mutableListOf<UserMapDto>()
        val usersToUpdate = mutableListOf<Pair<Int, UserMapDto>>()
        // we need to understand which one should be updated, created or removed
        usersList.forEachIndexed { index, currentUser ->
            val found = users.find { userDto -> userDto.id == currentUser.id }
            if (found == null) {
                // this place was deleted
                usersToRemove.add(currentUser)
            } else if (found.timestamp > currentUser.timestamp) {
                usersToUpdate.add(Pair(index, currentUser))
            }
        }
        println("Updated users: $usersToUpdate, removed users: $usersToRemove")
        // update updated users
        usersToUpdate.forEach { indexAndPlace ->
            usersList[indexAndPlace.first] = indexAndPlace.second
            // todo: add update user logic here (?)
        }
        // remove removed users
        usersToRemove.forEach { userDto ->
            usersList.remove(userDto)
            // todo: add remove user logic here (?)
        }
        // add new users
        users.filter { userDto ->
            val find = usersList.find { currentUser -> currentUser.id == userDto.id }
            return@filter find == null
        }.forEach { userDto ->
            println("new user is $userDto")
            usersList.add(userDto)
            // todo: add new user logic here (?)
        }
        println("Users are $usersList")
        // todo: here is an actual list of users
    }

    private fun updateLayers(actualLayersFromServer: List<LayerDto>) {
        val currentLayers: MutableList<LayerItem> = layersList.items
        val layersToRemove = mutableListOf<LayerItem>()
        val layersToUpdate = mutableListOf<Pair<Int, LayerDto>>()
        // we need to understand which one should be updated, created or removed
        currentLayers.forEachIndexed { index, layerItem ->
            val foundLayerDto = actualLayersFromServer.find { layerDto -> layerDto.id.toString() == layerItem.id }
            if (foundLayerDto == null) {
                if (layerItem.id != "0") {
                    // this layer was deleted
                    layersToRemove.add(layerItem)
                }
            } else if (foundLayerDto.timestamp > layerItem.timestamp) {
                layersToUpdate.add(Pair(index, foundLayerDto))
            }
        }

        println("Updated layers: $layersToUpdate, removedLayers: $layersToRemove")
        // update updated layers
        layersToUpdate.forEach { indexAndLayerDto ->
            val layerItem = currentLayers[indexAndLayerDto.first]
            layersList.update(indexAndLayerDto.first, indexAndLayerDto.second.updateLayerItem(layerItem))
        }
        // remove removed layers
        layersToRemove.forEach { layerItem ->
            layersList.remove(layerItem)
        }
        // add new layers
        actualLayersFromServer.filter { layerDto ->
            val find = currentLayers.find { layerItem -> layerItem.id == layerDto.id.toString() }
            return@filter find == null
        }.forEach { layerDto ->
            val newLayer = layerDto.toNewLayerItem()
            println("new layer $newLayer")
            layersList.addLast(newLayer)
        }

        actualLayersFromServer.forEach { layerDto ->
            if (layersToRemove.find { removedLayer -> removedLayer.id == layerDto.id.toString() } == null) {
                updatePlaces(layerDto.places.toMutableList(), layerDto.id)
            } else {
                updatePlaces(mutableListOf(), layerDto.id)
            }
        }
    }

    private val layerToPlacesMap: MutableMap<Long, MutableList<PlaceDto>> = mutableMapOf()

    private fun updatePlaces(places: MutableList<PlaceDto>, layerId: Long) {
        val currentPlaces = layerToPlacesMap.getOrPut(layerId, { mutableListOf() })
        val placesToRemove = mutableListOf<PlaceDto>()
        val placesToUpdate = mutableListOf<Pair<Int, PlaceDto>>()
        // we need to understand which one should be updated, created or removed
        currentPlaces.forEachIndexed { index, currentPlace ->
            val foundPlace = places.find { placeDto -> placeDto.id == currentPlace.id }
            if (foundPlace == null) {
                // this place was deleted
                placesToRemove.add(currentPlace)
            } else if (foundPlace.timestamp > currentPlace.timestamp) {
                placesToUpdate.add(Pair(index, currentPlace))
            }
        }
        println("Updated places: $placesToUpdate, removed places: $placesToRemove for layer $layerId")
        // update updated layers
        placesToUpdate.forEach { indexAndPlace ->
            currentPlaces[indexAndPlace.first] = indexAndPlace.second
            // todo: add update place logic here (?)
        }
        // remove removed layers
        placesToRemove.forEach { layerItem ->
            currentPlaces.remove(layerItem)
            // todo: add remove place logic here (?)
        }
        // add new layers
        places.filter { placeDto ->
            val find = currentPlaces.find { place -> place.id == placeDto.id }
            return@filter find == null
        }.forEach { placeDto ->
            println("new place at layer $layerId is $placeDto")
            currentPlaces.add(placeDto)
            // todo: add new place logic here (?)
        }
        layerToPlacesMap[layerId] = currentPlaces
        println("Places for the layer $layerId are $currentPlaces")
        // todo: here is an actual list of places
    }

    fun hideKeyboard() {
        val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = this.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        view.clearFocus()
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun drawPlaces(places: List<PlaceEntity>) {
        for (place in places) {
            getCategory(
                place
                    .categoryId
            ) { category ->
                placeCategory.put(place.id, category)
                mapview.getMap().getMapObjects().addPlacemark(
                    Point(place.latitude.toDouble(), place.longitude.toDouble()),
                    ImageProvider.fromBitmap(drawSimpleBitmap(category.colorRecourse))
                )
            }
        }
    }

    fun getPlaces(
        layerId: Long, actionsAfter: (
            List<PlaceEntity>
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.let {
                val placesDao = it.placeDao().getByLayerId(layerId)
                withContext(Dispatchers.Main) {
                    actionsAfter.invoke(placesDao)
                }
            }
        }
    }

    fun getUsers(
        mapId: Long, actionsAfter: (
            List<UserEntity>
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.let {
                val userMaps = it.userMapDao().getByMapId(mapId)
                val userIds = userMaps.map { userMapEntity -> userMapEntity.userId }
                val users = it.userDao().getByIds(userIds.filterNot { id -> id == userId })
                withContext(Dispatchers.Main) {
                    actionsAfter.invoke(users)
                }
            }
        }
    }


    fun userItemFromEntity(usersEntities: List<UserEntity>): List<UserItem> {
        val userItems: MutableList<UserItem> = ArrayList()
        for (userEntity in usersEntities) {
            userItems.add(UserItem(userEntity.id.toString(), userEntity.userName))
        }
        return userItems
    }

    fun getCategory(
        categoryId: Long, actionsAfter: (
            CategoryItem
        ) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.let {
                val categorydao = it.categoryDao().getById(categoryId)
                val category = fromCategory(categorydao)
                withContext(Dispatchers.Main) {
                    actionsAfter.invoke(category)
                }
            }
        }
    }

    fun fromCategory(categoryEntity: CategoryEntity): CategoryItem {
        return CategoryItem(
            categoryEntity.id.toString(),
            categoryEntity.name,
            categoryEntity.colorRecourse,
            categoryEntity.ownerId
        )
    }

    override fun getToolbarView(): Toolbar = base_toolbar

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        println("On create options menu")
        menuInflater.inflate(R.menu.map_menu, menu)

        val settingsBtn: MenuItem? = menu?.findItem(R.id.settings_btn)
        settingsBtn?.setOnMenuItemClickListener {
            println("Open settings fragment!")
            router?.showSettingsPage()
            true
        }
        return true
    }

    protected fun createNewPlace(
        name: String,
        ownerId: Long,
        latitude: String,
        longitude: String,
        categoryId: Long,
        actionsAfter: (Long) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            database!!.let {
                val place = PlaceEntity(
                    name,
                    ownerId,
                    latitude,
                    longitude,
                    categoryId
                )
                place.id = it.placeDao().insert(place)
                it.placeCategoryDao().insert(PlaceCategoryEntity(place.id, categoryId))

                withContext(Dispatchers.Main) {
                    actionsAfter.invoke(place.id)
                }
            }
        }
    }

    protected fun deletePlace(placeEntity: PlaceEntity, actionsAfter: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            database!!.placeDao().delete(placeEntity)
            withContext(Dispatchers.Main) {
                actionsAfter.invoke()
            }
        }
    }

    fun Double.round(decimals: Int = 4): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    protected fun getPlaceByParam(
        latitude: Double,
        longitude: Double,
        layerId: Long,
        actionsAfter: (PlaceEntity?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            database!!.let {
                val places = it.placeDao().getByLayerId(layerId)
                var place: PlaceEntity? = null
                for (placeEntity in places) {
                    val plLat = placeEntity.latitude.toDouble().round(3)
                    val pLong = placeEntity.longitude.toDouble().round(3)
                    if (plLat == latitude.round(3) && pLong == longitude.round(3)) {
                        place = placeEntity
                    }
                }
                withContext(Dispatchers.Main) {
                    actionsAfter.invoke(place)
                }
            }
        }
    }

    protected fun getPlaceByParam(latitude: Double, longitude: Double): PlaceEntity? {
        var place: PlaceEntity? = null
        for (placeEntity in currentPlaces) {
            val plLat = placeEntity.latitude.toDouble().round(3)
            val pLong = placeEntity.longitude.toDouble().round(3)
            val lat = latitude.round(3)
            val log = longitude.round(3)
            if (plLat == lat && pLong == log) {
                place = placeEntity
            }
        }
        return place
    }

    override fun onSearchResponse(response: Response) {

        val mapObjects: MapObjectCollection = mapview.getMap().getMapObjects()
        when {
            geoSearch -> {
                val searchRes = response.getCollection().getChildren()
                val geoObject = searchRes[0].obj!!
                val address = geoObject.name
                val desc = geoObject.descriptionText
                category_on_tap_adress_id.setText(address, TextView.BufferType.EDITABLE)
                category_on_tap_place_description_id.setText(desc, TextView.BufferType.EDITABLE)
                category_on_tap_place_name_id.setText(address.toString())
                var plName = address.toString()
                val resultLocation = searchRes[0].obj!!.geometry[0].point
                if (preLoad) {
                    if (resultLocation != null) {
                        val placeEntity =
                            currentPlaces.filter { placeEntity -> placeEntity.id == loadingObjId }
                        if (placeEntity.isNotEmpty()) {
                            category_on_tap_place_name_id.setText(placeEntity[0].name)
                            val category = placeCategory.get(loadingObjId)
                            if (category != null) {
                                category_on_tap_name_id.setText(category.name)
                                category_img.setColorFilter(
                                    ContextCompat.getColor(
                                        baseContext,
                                        category.colorRecourse
                                    ),
                                    android.graphics.PorterDuff.Mode.SRC_IN
                                )
                                selectedPlaceCategory = category
                            }
                        }
                    }
                } else {
                    selectedObjectId = address.toString()
                    selectedObject = geoObject
                    selectedPlaceCategory =
                        CategoryItem("-1", resources.getString(R.string.def_category), R.color.grey)
                    category_on_tap_name_id.setText(selectedPlaceCategory!!.name)
                    category_img.setColorFilter(
                        ContextCompat.getColor(
                            applicationContext,
                            selectedPlaceCategory!!.colorRecourse
                        ),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    checkPlaceMarked()
                    category_on_tap_name_id.setOnClickListener {
                        CategoryChoosingDialog(
                            selectedPlaceCategory!!,
                            placeCategory.values.toMutableList().distinctBy { it.id }
                                .toMutableList(), this
                        ).show(
                            supportFragmentManager,
                            "CategoryChoosingDialog"
                        )
                    }
                    category_on_tap_change_name_id.setOnClickListener {
                        CategoryColorDialog(
                            selectedPlaceCategory!!,
                            placeCategory.values.toMutableList().distinctBy { it.id }
                                .toMutableList(), this
                        ).show(
                            supportFragmentManager,
                            "CategoryColorDialog"
                        )
                    }

                }
                category_on_tap_save_changes_id.setOnClickListener {
                    if (category_on_tap_save_changes_id.text == resources.getText(R.string.save)) {
                        if (resultLocation != null) {
                            //TODO: correct category + save to DB
                            var placeId = -1L
                            val place = PlaceEntity(
                                plName,
                                userId!!,
                                resultLocation.latitude.toString(),
                                resultLocation.longitude.toString(),
                                1
                            )
                            createNewPlace(
                                plName,
                                userId!!,
                                resultLocation.latitude.toString(),
                                resultLocation.longitude.toString(),
                                1
                            ) { newPlaceId: Long ->
                                placeId = newPlaceId
                                if (placeId != -1L) {
                                    currentAddress.put(placeId, address.toString())
                                    place.id = placeId
                                    currentPlaces.add(place)
                                    mapObjects.addPlacemark(
                                        resultLocation,
                                        ImageProvider.fromBitmap(selectedPlaceCategory?.let { it1 ->
                                            drawSimpleBitmap(
                                                it1.colorRecourse
                                            )
                                        })
                                    )
                                }
                            }
                        }

                        hideTagMenu()
                        mapview.map.deselectGeoObject()
                    } else {
                        if (resultLocation != null) {
                            var pl = getPlaceByParam(
                                resultLocation.latitude,
                                resultLocation.longitude
                            )
                            if (pl != null) {
                                deletePlace(pl) {
                                    currentAddress.remove(address.toString())
                                    currentPlaces.remove(pl)
                                    mapObjects.clear()
                                    for (place in currentPlaces) {
                                        val point =
                                            Point(
                                                place.latitude.toDouble(),
                                                place.longitude.toDouble()
                                            )
                                        val category = placeCategory.get(place.id)
                                        if (category != null) {
                                            mapObjects.addPlacemark(
                                                point,
                                                ImageProvider.fromBitmap(drawSimpleBitmap(category.colorRecourse))
                                            )
                                        }
                                    }
                                    selectedObjectId = ""
                                    hideTagMenu()
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                searchResults.clear()
                for (searchResult in response.getCollection().getChildren()) {
                    val geoObject = searchResult.obj!!
                    val address = geoObject.name
                    val desc = geoObject.descriptionText
                    val resultLocation = geoObject.geometry[0].point
                    if (resultLocation != null) {
                        val serch = SearchItem(
                            searchResults.size.toString(),
                            address.toString(),
                            desc.toString(),
                            resultLocation
                        )
                        searchResults.add(serch)
                    }
                }
            }
        }
    }

    fun drawSimpleBitmap(resourceId: Int): Bitmap {
        val source =
            BitmapFactory.decodeResource(this.resources, R.drawable.search_result)
        val bitmap = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        var paint = Paint();
        paint.setColor(resources.getColor(resourceId, theme));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(
            (source.height / 2).toFloat(),
            (source.width / 2).toFloat(),
            (source.width / 2).toFloat() / 2,
            paint
        );
        paint.setColor(Color.WHITE)
        paint.setAntiAlias(true);
        paint.setTextSize(9F);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(
            " ", (source.height / 2).toFloat(),
            (source.width / 2).toFloat() - ((paint.descent() + paint.ascent()) / 2), paint
        );
        return bitmap;
    }

    override fun onSearchError(error: com.yandex.runtime.Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        mapUpdater?.stop()
        super.onStop()
    }

    override fun onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        mapview.onStart()
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun getActivityLayoutId() = R.layout.activity_map

    fun convertDpToPixels(context: Context, dp: Int) =
        (dp * context.resources.displayMetrics.density).toInt()

    private fun getNavigationBarHeight(): Int {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) realHeight - usableHeight else 0
    }

    override fun onMapTap(p0: Map, p1: Point) {
        hideKeyboard()
        search_text_field.visibility = View.INVISIBLE
        search_res_list.visibility = View.INVISIBLE
        search_text_clear.visibility = View.INVISIBLE
        search_text_field.setText("")
        searchResults.clear()
        geoSearch = true
        if (!isLinePointClick) {
            geoSearch = true
            val y = mapview.map.maxZoom.roundToInt()
            searchSession = searchManager!!.submit(p1, y, SearchOptions(), this)
            if (category_on_tap_save_changes_id.text == resources.getText(R.string.delete)) {
                showTagMenu()
            }
        }
    }

    private fun checkPlaceMarked() {
        category_on_tap_save_changes_id.setText(resources.getText(R.string.save))
        if (selectedObject != null) {
            for (place in currentPlaces) {
                val plLat = place.latitude.toDouble().round(3)
                val pLong = place.longitude.toDouble().round(3)
                val sLat = selectedObject!!.geometry[0].point?.latitude?.round(3)
                val sLong = selectedObject!!.geometry[0].point?.longitude?.round(3)
                if (plLat == sLat && pLong == sLong) {
//                    place = placeEntity
//                }
//                if (selectedObgect?.name!! == place.value.name!!) {
                    category_on_tap_save_changes_id.setText(resources.getText(R.string.delete))
                    category_on_tap_place_name_id.setText(place.name)
                    val category = placeCategory.get(place.id)
                    if (category != null) {
                        category_on_tap_name_id.setText(category.name)
                        category_img.setColorFilter(
                            ContextCompat.getColor(applicationContext, category.colorRecourse),
                            android.graphics.PorterDuff.Mode.SRC_IN
                        )
                        selectedPlaceCategory = category
                    }

                }
            }
        }
    }

    private fun dynamicSearch(s: String) {
        var searchOptions = SearchOptions()
        searchOptions.searchTypes = SearchType.GEO.value
        searchSession = searchManager!!.submit(
            s,
            Geometry.fromPoint(Point(59.9408455, 30.3131542)), SearchOptions(), this
        )
    }

    private fun hideTagMenu() {
        val tagBottomSheetBehavior = from(tag_edit_menu)
        tagBottomSheetBehavior.setState(STATE_HIDDEN)
        category_on_tap_save_changes_id.setText(resources.getText(R.string.save))
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        if (!isLinePointClick) {
            val y = mapview.map.maxZoom.roundToInt()
            geoSearch = true
            searchSession = searchManager!!.submit(p1, y, SearchOptions(), this)
            mapview.map.deselectGeoObject()
            showTagMenu()
        }
    }

    private fun showTagMenu() {
        val tagBottomSheetBehavior = from(tag_edit_menu)
        if (!tagBottomSheetBehavior.state.equals(STATE_HALF_EXPANDED)) {
            tagBottomSheetBehavior.isDraggable = true
            tagBottomSheetBehavior.setState(STATE_HALF_EXPANDED)
        }
    }

    private fun showUsersMenu() {
        val usersBottomSheetBehavior = from(users_edit_menu)
        if (!usersBottomSheetBehavior.state.equals(STATE_HALF_EXPANDED)) {
            usersBottomSheetBehavior.isDraggable = true
            usersBottomSheetBehavior.setState(STATE_HALF_EXPANDED)
        }
    }

    override fun getToolbarTitle(): String = getString(R.string.app_name)

    override fun canOpenNavMenuFromToolbar(): Boolean = false

    override fun onSaveParameterClick(item: CategoryItem) {
        selectedPlaceCategory = item
        category_on_tap_name_id.setText(item.name)
        category_img.setColorFilter(
            ContextCompat.getColor(applicationContext, item.colorRecourse),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }
}