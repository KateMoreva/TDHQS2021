package map.together.mockActivities.auth

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.category_on_tap_fragment.*
import kotlinx.android.synthetic.main.item_layers_menu.*
import kotlinx.android.synthetic.main.item_menu.*
import kotlinx.android.synthetic.main.item_users.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.activities.AppbarActivity
import map.together.api.Api
import map.together.db.entity.MapEntity
import map.together.db.entity.PlaceCategoryEntity
import map.together.db.entity.PlaceEntity
import map.together.db.entity.UserEntity
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
import map.together.utils.ResponseActions
import map.together.utils.recycler.adapters.LayersAdapter
import map.together.utils.recycler.adapters.SearchResAdapter
import map.together.utils.recycler.adapters.UsersAdapter
import map.together.viewholders.LayerViewHolder
import map.together.viewholders.SearchViewHolder
import map.together.viewholders.UsersViewHolder
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection
import kotlin.math.roundToInt

class FakeMapActivity : AppbarActivity(), GeoObjectTapListener, InputListener,
    Session.SearchListener,
    CategoryColorDialog.CategoryDialogListener {
    val SPB = Point(59.9408455, 30.3131542)
    val layersList: ItemsList<LayerItem> = ItemsList(mutableListOf())
    var mapUpdater: FakeMapUpdater? = null

    //TODO: loading from bundle
    var token = ""
    var currentMapEntity: MapEntity? = null
    var currentLayerId = 0L

    val placeCategory: MutableMap<Long, CategoryItem> = HashMap()

    var polyline: Polyline = Polyline()
    var prevPolyline: Polyline = Polyline()
    var isLinePointClick = false
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    var geoSearch = true
    var selectedObjectId = ""
    var selectedObject: GeoObject? = null
    val searchResults: MutableList<SearchItem> = ArrayList()
    var selectedPlaceCategory: CategoryItem? = null
    var categories = mutableListOf<CategoryItem>()

    var usersItemsList = ItemsList(mutableListOf<UserItem>())

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

        val usersAdapter = UsersAdapter(
            holderType = UsersViewHolder::class,
            layoutId = R.layout.item_user,
            dataSource = usersItemsList,
            onClick = { user ->
                print("Layer $user clicked")
            },
            onChangeRole = { item ->
                println(item)
                taskContainer.add(
                    Api.fakechangeRole(token, item.id, item.name, item.email, item.role).subscribe(
                        {
                            ResponseActions.onResponse(
                                it,
                                applicationContext,
                                HttpsURLConnection.HTTP_OK,
                                HttpsURLConnection.HTTP_FORBIDDEN
                            ) { dto ->
                                println("Success!")
                            }
                        },
                        { ResponseActions.onFail(it, applicationContext) }
                    )
                )
            },
            onRemove = { item ->
                println(item)
                taskContainer.add(
                    Api.fakechangeRole(token, item.id, item.name, item.email, 0).subscribe(
                        {
                            ResponseActions.onResponse(
                                it,
                                applicationContext,
                                HttpsURLConnection.HTTP_OK,
                                HttpsURLConnection.HTTP_FORBIDDEN
                            ) { dto ->
                                println("Success!")
                            }
                        },
                        { ResponseActions.onFail(it, applicationContext) }
                    )
                )
            },

            )

        users_list.adapter = usersAdapter
        val usersManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        users_list.layoutManager = usersManager

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        search_text_field.visibility = View.INVISIBLE
        mapview.map.move(
            CameraPosition(SPB, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )

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
                // TODO: save polygons here
                drawPlaces()
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
                                    this@FakeMapActivity
                                )
                                search_text_field.visibility = View.INVISIBLE
                                search_res_list.visibility = View.GONE
                                searchResults.clear()
                                val imm =
                                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(
                                    this@FakeMapActivity.currentFocus?.windowToken,
                                    0
                                )
                                showTagMenu()
                            }
                        },
                    )
                    search_res_list.adapter = adapter
                    val layoutManager =
                        LinearLayoutManager(this@FakeMapActivity, RecyclerView.VERTICAL, false)
                    search_res_list.layoutManager = layoutManager
                    search_res_list.visibility = View.VISIBLE

                    dynamicSearch(s.toString())
                }
            }
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(layers_menu)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        layers_btn.setOnClickListener {
            bottomSheetBehavior.isDraggable = true
            hide_menu_btn.visibility = View.GONE
            hide_menu_img.visibility = View.VISIBLE
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        }

        hide_menu_btn.setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = resizable_layers_menu.layoutParams
                val fullHeight = Resources.getSystem().displayMetrics.heightPixels
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    layoutParams.height = fullHeight - getNavigationBarHeight()
                    bottomSheetBehavior.isDraggable = false
                    hide_menu_btn.visibility = View.VISIBLE
                    hide_menu_img.visibility = View.GONE
                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
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

        val tagBottomSheetBehavior = BottomSheetBehavior.from(tag_edit_menu)
        tagBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        tagBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = resizable_tag_menu.layoutParams
                val fullHeight = Resources.getSystem().displayMetrics.heightPixels
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    layoutParams.height = (fullHeight - getNavigationBarHeight()) / 2
                }
                resizable_tag_menu.layoutParams = layoutParams
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


        val usersBottomSheetBehavior = BottomSheetBehavior.from(users_edit_menu)
        usersBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        usersBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val layoutParams = users_edit_menu.layoutParams
                val fullHeight = Resources.getSystem().displayMetrics.heightPixels
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
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

        layersList.setData(mutableListOf())
        val adapter = LayersAdapter(
            holderType = LayerViewHolder::class,
            layoutId = R.layout.item_layer,
            dataSource = layersList,
            onClick = { layer ->
                print("Layer $layer clicked")
                if (layer.isVisible && currentLayerId.toString() != layer.id) {
                    layersList.items.forEach {
                        it.selected = false
                    }
                    layer.selected = true
                    currentLayerId = layer.id.toLong()
                } else {
                    layer.isVisible = !layer.isVisible
                }
                layersList.rangeUpdate(0, layersList.size())
            },
            onRemove = {
                // todo: check that user can delete this layer and delete it
                tryRemoveLayer(it) {
                    layersList.remove(it)
                }
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
            router?.showPlacesPage(currentMapEntity!!.serverId, layersIds.toLongArray())
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
            val newLayerName = "Новый слой " + layersList.size()
            Api.fakecreateLayer(
                CurrentUserRepository.getCurrentUserToken(applicationContext)!!,
                newLayerName,
                currentMapEntity!!.serverId
            ).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        applicationContext,
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_FORBIDDEN
                    ) { layerDto ->
                        println("Success! Layer {$layerDto} created")
                        val newLayer = layerDto!!.toNewLayerItem()
                        layersList.addLast(newLayer)
                        layers_list.smoothScrollToPosition(layersList.size() - 1)
                    }
                },
                { ResponseActions.onFail(it, applicationContext) }
            )
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

        val bottomSheetBehavior2 = BottomSheetBehavior.from(bottom_sheet)
        bottomSheetBehavior2.state = BottomSheetBehavior.STATE_HIDDEN
        menu.setOnClickListener {
            bottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        stop_demonstrate_card.visibility = View.INVISIBLE

        //TODO: LOAD meta from sever

        val mapLocalId = (intent.extras?.get(Page.MAP_ID_KEY)) as Long

        getMapFromDatabase(mapLocalId) { mapEntity ->
            currentMapEntity = mapEntity
            token = CurrentUserRepository.getCurrentUserToken(applicationContext)!!
            loadCategories(token)
            mapUpdater = FakeMapUpdater(
                5000000,
                token,
                mapEntity.id,
                applicationContext,
                taskContainer,
                database!!
            ) { mapInfo ->
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

    }

    private fun loadCategories(token: String) {
        taskContainer.add(
            Api.fakegetMyCategories(token).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        applicationContext,
                        HttpsURLConnection.HTTP_OK,
                        HttpURLConnection.HTTP_BAD_REQUEST
                    ) { categoriesDtos ->
                        categories = categoriesDtos!!.map { dto ->
                            CategoryItem(dto.id.toString(), dto.name, dto.color, dto.ownerId)
                        }.toMutableList()
                    }
                },
                { ResponseActions.onFail(it, applicationContext) }
            )
        )
    }

    private fun tryRemoveLayer(layerToRemove: LayerItem, onDelete: () -> Unit) {
        this.taskContainer.add(
            Api.fakeremoveLayer(token, currentMapEntity!!.serverId, layerToRemove.id.toLong())
                .subscribe(
                    {
                        ResponseActions.onResponse(
                            it,
                            applicationContext,
                            HttpsURLConnection.HTTP_OK,
                            HttpsURLConnection.HTTP_FORBIDDEN
                        ) { layerDto ->
                            println("Success! Layer {$layerDto} was deleted")
                            onDelete.invoke()
                        }
                    },
                    { ResponseActions.onFail(it, applicationContext) })
        )
    }

    private fun getMapFromDatabase(mapLocalId: Long, actionsAfter: (MapEntity) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            database?.let {
                val mapEntity = it.mapDao().getById(mapLocalId)
                withContext(Dispatchers.Main) {
                    actionsAfter.invoke(mapEntity)
                }
            }
        }
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
            usersItemsList.update(indexAndPlace.first, indexAndPlace.second.toUserItem())
        }
        // remove removed users
        usersToRemove.forEach { userDto ->
            usersList.remove(userDto)
            usersItemsList.remove(userDto.toUserItem())
            // todo: add remove user logic here (?)
        }
        // add new users
        users.filter { userDto ->
            val find = usersList.find { currentUser -> currentUser.id == userDto.id }
            return@filter find == null
        }.forEach { userDto ->
            println("new user is $userDto")
            usersList.add(userDto)
            usersItemsList.addLast(userDto.toUserItem())
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
            val foundLayerDto =
                actualLayersFromServer.find { layerDto -> layerDto.id.toString() == layerItem.id }
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
            layersList.update(
                indexAndLayerDto.first,
                indexAndLayerDto.second.updateLayerItem(layerItem)
            )
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
        if (layersList.items.isNotEmpty() && currentLayerId == 0L) {
            currentLayerId = layersList.items[0].id.toLong()
        } else if (layersList.items.isEmpty()) {
            currentLayerId = 0L
        }

        actualLayersFromServer.forEach { layerDto ->
            if (layersToRemove.find { removedLayer -> removedLayer.id == layerDto.id.toString() } == null) {
                updatePlaces(layerDto.places.toMutableList(), layerDto.id)
            } else {
                updatePlaces(mutableListOf(), layerDto.id)
            }
        }
        drawPlaces()
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
            mapview.map.mapObjects.addPlacemark(
                Point(placeDto.latitude.toDouble(), placeDto.longitude.toDouble()),
                ImageProvider.fromBitmap(drawSimpleBitmap(placeDto.categoryColor))
            )
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

    private fun drawPlaces() {
        val places: MutableList<PlaceDto> = layerToPlacesMap[currentLayerId] ?: return
        mapview.map.mapObjects.clear()
        for (place in places) {
            val category = categories.find { category ->
                category.id == place.categoryId.toString()
            } ?: continue
            placeCategory[place.id] = category
            mapview.map.mapObjects.addPlacemark(
                Point(place.latitude.toDouble(), place.longitude.toDouble()),
                ImageProvider.fromBitmap(drawSimpleBitmap(category.colorRecourse))
            )
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

    override fun getToolbarView(): Toolbar = base_toolbar

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        println("On create options menu")
        menuInflater.inflate(R.menu.map_menu, menu)

        val settingsBtn: MenuItem? = menu.findItem(R.id.settings_btn)
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

    protected fun deletePlace(placeEntity: PlaceEntity, actionsAfter: (PlaceDto) -> Unit) {
        taskContainer.add(
            Api.fakeremovePlace(
                token,
                currentMapEntity!!.serverId,
                currentLayerId,
                placeEntity.serverId
            ).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        applicationContext,
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_FORBIDDEN
                    ) { placeDto ->
                        println("Success! Place {$placeDto} created")
                        actionsAfter.invoke(placeDto!!)
                    }
                },
                { ResponseActions.onFail(it, applicationContext) }
            )
        )

    }

    fun Double.round(decimals: Int = 4): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    protected fun getPlaceByParam(latitude: Double, longitude: Double): PlaceEntity? {
        var place: PlaceEntity? = null
        layerToPlacesMap[currentLayerId]?.forEach { placeEntity ->
            val plLat = placeEntity.latitude.toDouble().round(3)
            val pLong = placeEntity.longitude.toDouble().round(3)
            val lat = latitude.round(3)
            val log = longitude.round(3)
            if (plLat == lat && pLong == log) {
                place = placeEntity.toPlaceEntity()
            }
        }
        return place
    }

    override fun onSearchResponse(response: Response) {
        when {
            geoSearch -> {
                if (categories.isEmpty()) {
                    return
                }

                val searchRes = response.collection.children
                val geoObject = searchRes[0].obj!!
                val address = geoObject.name
                val desc = geoObject.descriptionText
                category_on_tap_adress_id.setText(address, TextView.BufferType.EDITABLE)
                category_on_tap_place_description_id.setText(desc, TextView.BufferType.EDITABLE)
                category_on_tap_place_name_id.setText(address.toString())
                val resultLocation = searchRes[0].obj!!.geometry[0].point

                selectedObjectId = address.toString()
                selectedObject = geoObject
                selectedPlaceCategory = categories[0]
                category_on_tap_name_id.text = selectedPlaceCategory!!.name
                category_img.setColorFilter(
                    ContextCompat.getColor(
                        applicationContext,
                        CategoryColorDialog.COLORS_ARRAY[selectedPlaceCategory!!.colorRecourse]
                    ),
                    PorterDuff.Mode.SRC_IN
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
                category_on_tap_save_changes_id.setOnClickListener {
                    if (category_on_tap_save_changes_id.text == resources.getText(R.string.save)) {
                        val placeName = category_on_tap_place_name_id.text.toString()
                        createPlace(
                            resultLocation,
                            placeName,
                            selectedPlaceCategory!!.id.toLong()
                        ) { placeDto ->
                            // todo: настроить отображение категорий
                            layerToPlacesMap[currentLayerId]?.add(placeDto)
                            drawPlaces()
                        }
                        hideTagMenu()
                        mapview.map.deselectGeoObject()
                    } else {
                        if (resultLocation == null) {
                            return@setOnClickListener
                        }
                        val placeByParams: PlaceEntity = getPlaceByParam(
                            resultLocation.latitude,
                            resultLocation.longitude
                        ) ?: return@setOnClickListener

                        deletePlace(placeByParams) { placeDto ->
                            layerToPlacesMap[currentLayerId]?.removeIf { dto -> dto.id == placeDto.id }
                            drawPlaces()
                            selectedObjectId = ""
                            hideTagMenu()
                        }
                    }
                }
            }
            else -> {
                searchResults.clear()
                for (searchResult in response.collection.children) {
                    val geoObject = searchResult.obj!!
                    val address = geoObject.name
                    val desc = geoObject.descriptionText
                    val resultLocation = geoObject.geometry[0].point
                    if (resultLocation != null) {
                        val search = SearchItem(
                            searchResults.size.toString(),
                            address.toString(),
                            desc.toString(),
                            resultLocation
                        )
                        searchResults.add(search)
                    }
                }
            }
        }
    }

    private fun createPlace(
        resultLocation: Point?,
        placeName: String,
        categoryId: Long,
        onCreated: (PlaceDto) -> Unit
    ) {
        if (resultLocation == null) {
            return
        }
        val latitude = resultLocation.latitude.toString()
        val longitude = resultLocation.longitude.toString()
        taskContainer.add(
            Api.fakecreatePlace(
                token,
                placeName,
                currentMapEntity!!.serverId,
                currentLayerId,
                latitude,
                longitude,
                categoryId
            ).subscribe(
                {
                    ResponseActions.onResponse(
                        it,
                        applicationContext,
                        HttpsURLConnection.HTTP_OK,
                        HttpsURLConnection.HTTP_FORBIDDEN
                    ) { placeDto ->
                        println("Success! Place {$placeDto} created")
                        onCreated.invoke(placeDto!!)
                    }
                },
                { ResponseActions.onFail(it, applicationContext) }
            )

        )
    }

    fun drawSimpleBitmap(colorIdAtArray: Int): Bitmap {
        val source = BitmapFactory.decodeResource(this.resources, R.drawable.search_result)
        val bitmap = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = resources.getColor(CategoryColorDialog.COLORS_ARRAY[colorIdAtArray], theme)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(
            (source.height / 2).toFloat(),
            (source.width / 2).toFloat(),
            (source.width / 2).toFloat() / 2,
            paint
        )
        paint.color = Color.WHITE
        paint.isAntiAlias = true
        paint.textSize = 9F
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            " ", (source.height / 2).toFloat(),
            (source.width / 2).toFloat() - ((paint.descent() + paint.ascent()) / 2), paint
        )
        return bitmap
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
        if (isLinePointClick) {
            return
        }
        geoSearch = true
        val y = mapview.map.maxZoom.roundToInt()
        searchSession = searchManager!!.submit(p1, y, SearchOptions(), this)
        if (category_on_tap_save_changes_id.text == resources.getText(R.string.delete)) {
            showTagMenu()
        }
    }

    private fun checkPlaceMarked() {
        category_on_tap_save_changes_id.text = resources.getText(R.string.save)
        if (selectedObject == null) {
            return
        }
        val sLat = selectedObject!!.geometry[0].point?.latitude?.round(3)
        val sLong = selectedObject!!.geometry[0].point?.longitude?.round(3)
        layerToPlacesMap[currentLayerId]?.forEach { place ->
            val plLat = place.latitude.toDouble().round(3)
            val pLong = place.longitude.toDouble().round(3)
            if (plLat != sLat || pLong != sLong) {
                return@forEach
            }
            category_on_tap_save_changes_id.text = resources.getText(R.string.delete)
            category_on_tap_place_name_id.setText(place.name)
            val category = placeCategory[place.id] ?: return@forEach
            category_on_tap_name_id.text = category.name
            category_img.setColorFilter(
                ContextCompat.getColor(
                    applicationContext,
                    CategoryColorDialog.COLORS_ARRAY[category.colorRecourse]
                ),
                PorterDuff.Mode.SRC_IN
            )
            selectedPlaceCategory = category
        }
    }

    private fun dynamicSearch(s: String) {
        val searchOptions = SearchOptions()
        searchOptions.searchTypes = SearchType.GEO.value
        searchSession = searchManager!!.submit(
            s,
            Geometry.fromPoint(Point(59.9408455, 30.3131542)), SearchOptions(), this
        )
    }

    private fun hideTagMenu() {
        val tagBottomSheetBehavior = BottomSheetBehavior.from(tag_edit_menu)
        tagBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        category_on_tap_save_changes_id.text = resources.getText(R.string.save)
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
        val tagBottomSheetBehavior = BottomSheetBehavior.from(tag_edit_menu)
        if (!tagBottomSheetBehavior.state.equals(BottomSheetBehavior.STATE_HALF_EXPANDED)) {
            tagBottomSheetBehavior.isDraggable = true
            tagBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    private fun showUsersMenu() {
        val usersBottomSheetBehavior = BottomSheetBehavior.from(users_edit_menu)
        if (!usersBottomSheetBehavior.state.equals(BottomSheetBehavior.STATE_HALF_EXPANDED)) {
            usersBottomSheetBehavior.isDraggable = true
            usersBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }

    override fun getToolbarTitle(): String = getString(R.string.app_name)

    override fun canOpenNavMenuFromToolbar(): Boolean = false

    override fun onSaveParameterClick(item: CategoryItem) {
        selectedPlaceCategory = item
        category_on_tap_name_id.text = item.name
        category_img.setColorFilter(
            ContextCompat.getColor(
                applicationContext,
                CategoryColorDialog.COLORS_ARRAY[item.colorRecourse]
            ),
            PorterDuff.Mode.SRC_IN
        )
    }
}