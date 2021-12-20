package map.together.activities


import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import map.together.R
import map.together.db.entity.CategoryEntity
import map.together.db.entity.PlaceCategoryEntity
import map.together.db.entity.PlaceEntity
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.items.LayerItem
import map.together.items.SearchItem
import map.together.utils.recycler.adapters.LayersAdapter
import map.together.utils.recycler.adapters.SearchResAdapter
import map.together.viewholders.LayerViewHolder
import map.together.viewholders.SearchViewHolder
import kotlin.math.roundToInt
import kotlin.math.round


class MapActivity : AppbarActivity(), GeoObjectTapListener, InputListener,
    Session.SearchListener {
    val SPB = Point(59.9408455, 30.3131542)
    //TODO: loading from meta
    val currentUserID = 1L
    val currentMapId = 1L
    val currentLayerId = 1L

    val currentPlaces: MutableList<PlaceEntity> = ArrayList()
    val currentAddress: MutableMap<Long, String> = HashMap()
    val currentGeoObjects: MutableMap<Long, GeoObject> = HashMap()
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

    private val polylineListener = object : InputListener {
        override fun onMapLongTap(p0: Map, p1: Point) {}
        override fun onMapTap(p0: Map, p1: Point) {
            polyline.points.add(p1)
            p0.mapObjects.addPlacemark(p1)
            p0.mapObjects.addPolyline(Polyline(polyline.points))
        }
    }

    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        preLoad = false
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
        SearchFactory.initialize(this);
        //TODO: LOAD meta from sever

        val layerPlaces = mutableListOf<PlaceEntity>()

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
            for (place in places) {
                val y = mapview.map.maxZoom.roundToInt()
                preLoad = true
                geoSearch = true
                loadingObjId = place.id
                searchSession = searchManager!!.submit(
                    Point(
                        place.latitude.toDouble(),
                        place.longitude.toDouble()
                    ), y, SearchOptions(), this
                )
            }
            currentPlaces.addAll(layerPlaces)
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
                for (obj in currentGeoObjects) {
                    val point = obj.value.geometry[0].point
                    if (point != null) {
                        mapview.getMap().getMapObjects().addPlacemark(
                            Point(point.latitude, point.longitude),
                            ImageProvider.fromBitmap(drawSimpleBitmap(Color.BLUE))
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
                search_text_clear.visibility = View.VISIBLE
                search_text_field.setSelection(search_text_field.length())
                imm.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_FORCED, 0)
            } else {
                search_text_field.visibility = View.INVISIBLE
                search_res_list.visibility = View.INVISIBLE
                search_text_clear.visibility = View.INVISIBLE
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
                if (s.length > 2) {
                    geoSearch = false
                    val adapter = SearchResAdapter(
                        holderType = SearchViewHolder::class,
                        layoutId = R.layout.item_search_result,
                        dataSource = ItemsList(searchResults),
                        onClick = { item ->
                            //TODO: Fix onClick
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

        val layers = mutableListOf(
                LayerItem("0", "Нажмите \"Показать всем\" для демонстрации", false, 0L, false, true),
                LayerItem("1", "Слой 1", true, 2, false),
                LayerItem("2", "Слой 2", false, 2, false)
        )
        val layersList = ItemsList(layers)
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

        stop_demonstrate_card.visibility = View.INVISIBLE

    }

    fun drawPlaces(places: List<PlaceEntity>) {
        for (place in places) {
            getCategory(
                place
                    .categoryId
            ) { category ->
                if (category.colorRecourse != null) {
                    mapview.getMap().getMapObjects().addPlacemark(
                        Point(place.latitude.toDouble(), place.longitude.toDouble()),
                        ImageProvider.fromBitmap(drawSimpleBitmap(category.colorRecourse!!))
                    )
                }
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

    private fun submitQuery(query: String) {
        searchSession = searchManager?.submit(
            query,
            VisibleRegionUtils.toPolygon(mapview.map.visibleRegion),
            SearchOptions(),
            this
        )
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
        for (placeEntity in currentGeoObjects) {
            val plLat = placeEntity.value.geometry[0].point?.latitude?.round(2)
            val pLong = placeEntity.value.geometry[0].point?.longitude?.round(2)
            val lat = latitude.round(2)
            val log = longitude.round(2)
            if (plLat == lat && pLong == log) {
                place = currentPlaces.find { plaace -> place?.id == placeEntity.key }
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
                        currentGeoObjects.put(loadingObjId, geoObject)
                    }
                } else {
                    selectedObjectId = address.toString()
                    selectedObject = geoObject
                    checkPlaceMarked()
                }
                category_on_tap_save_changes_id.setOnClickListener {
                    if (category_on_tap_save_changes_id.text == resources.getText(R.string.save)) {
                        if (resultLocation != null) {
                            //TODO: correct category + save to DB
                            var placeId = -1L
                            createNewPlace(
                                plName,
                                currentUserID,
                                resultLocation.latitude.toString(),
                                resultLocation.longitude.toString(),
                                1
                            ) { newPlaceId: Long ->
                                placeId = newPlaceId
                                if (placeId != -1L) {
                                    currentAddress.put(placeId, address.toString())
                                    currentGeoObjects.put(placeId, geoObject)
                                    mapObjects.addPlacemark(
                                        resultLocation,
                                        ImageProvider.fromBitmap(drawSimpleBitmap(Color.BLUE))
                                    )
                                }
                            }
                        }

                        hideTagMenu()
                        mapview.map.deselectGeoObject()
                    } else {
                        if (resultLocation != null) {
                            category_on_tap_save_changes_id.setText(resources.getText(R.string.save))
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
                                        val category = Color.BLUE
                                        mapObjects.addPlacemark(
                                            point,
                                            ImageProvider.fromBitmap(drawSimpleBitmap(category))
                                        )
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

    fun drawSimpleBitmap(color: Int): Bitmap {
        val source =
            BitmapFactory.decodeResource(this.resources, R.drawable.search_result)
        val bitmap = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        var paint = Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(
            (source.height / 2).toFloat(),
            (source.width / 2).toFloat(),
            (source.width / 2).toFloat() / 2,
            paint
        );
        paint.setColor(Color.WHITE);
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
        ///TODO: Обработка попадания на тэг
        preLoad = false
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
        if (preLoad) {
            return
        }
        if (selectedObject != null) {
            for (place in currentGeoObjects) {
                val plLat = place.value.geometry[0].point?.latitude?.round(2)
                val pLong = place.value.geometry[0].point?.longitude?.round(2)
                val sLat = selectedObject!!.geometry[0].point?.latitude?.round(2)
                val sLong = selectedObject!!.geometry[0].point?.longitude?.round(2)
                if (plLat == sLat && pLong == sLong) {
//                    place = placeEntity
//                }
//                if (selectedObgect?.name!! == place.value.name!!) {
                    category_on_tap_save_changes_id.setText(resources.getText(R.string.delete))
                    val placeName =
                        currentPlaces.filter { placeEntity -> placeEntity.id == place.key }
                    if (placeName.isNotEmpty()) {
                        category_on_tap_place_name_id.setText(placeName[0].name)
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
        preLoad = false
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

    override fun getToolbarTitle(): String = getString(R.string.app_name)

    override fun canOpenNavMenuFromToolbar(): Boolean = false
}