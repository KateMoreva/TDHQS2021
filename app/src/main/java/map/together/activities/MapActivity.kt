package map.together.activities


import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
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
import map.together.db.entity.PlaceEntity
import map.together.dto.db.PlaceDto
import map.together.items.CategoryItem
import map.together.items.ItemsList
import map.together.items.LayerItem
import map.together.utils.recycler.adapters.LayersAdapter
import map.together.viewholders.LayerViewHolder
import java.lang.Exception
import kotlin.math.roundToInt


class MapActivity : AppbarActivity(), GeoObjectTapListener, InputListener,
    Session.SearchListener {

    val currentUserID = 0L

    val currentPlaces: MutableList<PlaceEntity> = ArrayList()
    val currentAddress: MutableList<String> = ArrayList()
    var polyline: Polyline = Polyline()
    var prevPolyline: Polyline = Polyline()
    var isLinePointClick = false
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null
    var geoSearch = false
    var selectedLayerId: String = ""
    var selectedObjectAddress = ""

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
        SearchFactory.initialize(this);
        //TODO: LOAD places from sever
        val layerPlaces = mutableListOf<PlaceEntity>()
        layerPlaces.add(PlaceEntity("new", 0, "59.9408455", "30.3131542", 0))
        drawPlaces(layerPlaces)

        currentPlaces.addAll(layerPlaces);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)
        search_text_field.visibility = View.INVISIBLE
        mapview.map.move(
            CameraPosition(Point(59.9408455, 30.3131542), 11.0f, 0.0f, 0.0f),
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
                mapview.map.mapObjects.clear()
            }
        })

        search_button_id.setOnClickListener(fun(_: View) {
            if (search_text_field.visibility == View.INVISIBLE) {
                search_text_field.visibility = View.VISIBLE
            } else {
                search_text_field.visibility = View.INVISIBLE
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

    override fun onSearchResponse(response: Response) {

        val mapObjects: MapObjectCollection = mapview.getMap().getMapObjects()
        when {
            geoSearch -> {
                val searchRes = response.getCollection().getChildren()
                val address = searchRes[0].obj!!.name
                val desc = searchRes[0].obj!!.descriptionText
                val meta = searchRes[0].obj!!.metadataContainer
                category_on_tap_adress_id.setText(address, TextView.BufferType.EDITABLE)
                category_on_tap_place_description_id.setText(desc, TextView.BufferType.EDITABLE)
                var plName = category_on_tap_place_name_id.text.toString()
                if (plName == "Place name" && address != null) {
                    plName = address
                }
                selectedObjectAddress = address.toString()
                checkPlaceMarked()
                category_on_tap_save_changes_id.setOnClickListener {
                    val resultLocation = searchRes[0].obj!!.geometry[0].point
                    if (category_on_tap_save_changes_id.text == resources.getText(R.string.save)) {
                        if (resultLocation != null) {
                            currentAddress.add(address.toString())
                            currentPlaces.add(
                                PlaceEntity(
                                    plName,
                                    currentUserID,
                                    resultLocation.latitude.toString(),
                                    resultLocation.longitude.toString(),
                                    1
                                )
                            )
                            mapObjects.addPlacemark(
                                resultLocation,
                                ImageProvider.fromBitmap(drawSimpleBitmap(Color.BLUE))
                            )
                        }

                        hideTagMenu()
                        mapview.map.deselectGeoObject()
                    } else {
                        if (resultLocation != null) {
                            currentAddress.remove(address.toString())
                            currentPlaces.remove(
                                PlaceDto(
                                    -1,
                                    plName,
                                    currentUserID,
                                    resultLocation.latitude.toString(),
                                    resultLocation.longitude.toString()
                                )
                            )
                            mapObjects.clear()
                            for (place in currentPlaces) {
                                val point =
                                    Point(place.latitude.toDouble(), place.longitude.toDouble())
                                val category = Color.BLUE
                                mapObjects.addPlacemark(
                                    point,
                                    ImageProvider.fromBitmap(drawSimpleBitmap(category))
                                )
                            }
                            selectedObjectAddress = ""
                            hideTagMenu()
                        }
                    }
                    geoSearch = false
                }
            }
            else -> {
                for (searchResult in response.getCollection().getChildren()) {
                    //Normal
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
        if (selectedObjectAddress.isNotEmpty()) {
            for (place in currentAddress) {
                if (place == selectedObjectAddress) {
                    category_on_tap_save_changes_id.setText(resources.getText(R.string.delete))
                }
            }
        }
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

    override fun getToolbarTitle(): String = getString(R.string.app_name)

    override fun canOpenNavMenuFromToolbar(): Boolean = false
}