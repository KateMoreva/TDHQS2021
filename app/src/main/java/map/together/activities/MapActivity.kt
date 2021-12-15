package map.together.activities

import android.content.Context
import android.content.res.Resources
import android.graphics.PointF
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.Rect
import com.yandex.mapkit.map.RotationType
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.item_layers_menu.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.items.ItemsList
import map.together.items.LayerItem
import map.together.utils.recycler.adapters.LayersAdapter
import map.together.viewholders.LayerViewHolder
import com.yandex.mapkit.search.SearchOptions

import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.Session
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.yandex.runtime.network.NetworkError

import com.yandex.runtime.network.RemoteError

import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import kotlinx.android.synthetic.main.category_on_tap_fragment.*
import map.together.utils.logger.Logger
import kotlin.math.roundToInt


class MapActivity : BaseFragmentActivity(), GeoObjectTapListener, InputListener,
    Session.SearchListener {

    private val DRAGGABLE_PLACEMARK_CENTER = Point(59.948, 30.323)
    private val ANIMATED_PLACEMARK_CENTER = Point(59.948, 30.318)

    val currentPlaces: MutableList<Point> = ArrayList()
    var polyline: Polyline = Polyline()
    var prevPolyline: Polyline = Polyline()
    var mapObjects: MapObjectCollection? = null
    var isLinePointClick = false
    private var searchManager: SearchManager? = null
    private var searchSession: Session? = null

    private val polylineListener = object : InputListener {
        override fun onMapLongTap(p0: Map, p1: Point) {}
        override fun onMapTap(p0: Map, p1: Point) {
            polyline.points.add(p1)
            p0.mapObjects.clear()
            p0.mapObjects.addPlacemark(p1)
            p0.mapObjects.addPolyline(Polyline(polyline.points))
        }
    }


    override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
        var objName = geoObjectTapEvent.geoObject.name
        var disc = geoObjectTapEvent.geoObject.descriptionText
        var metadata = geoObjectTapEvent.geoObject
        if (objName != null) {
            Logger.d(this, objName)
        }
        if (disc != null) {
            Logger.d(this, disc)
        }
        if (metadata != null) {
            Logger.d(this, metadata)
        }
        val selectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        if (selectionMetadata != null) {
            mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
            var geo = geoObjectTapEvent.geoObject.geometry[0].point
            if (geo != null) {
                mapview.map.mapObjects.addPlacemark(geo)
                val tagBottomSheetBehavior = from(tag_edit_menu)
                if (!tagBottomSheetBehavior.state.equals(STATE_HALF_EXPANDED) or !tagBottomSheetBehavior.state.equals(
                        STATE_EXPANDED
                    )
                )
                    tagBottomSheetBehavior.isDraggable = true
                tagBottomSheetBehavior.setState(STATE_HALF_EXPANDED)
            }
        }
        return selectionMetadata != null
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE);
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
                if (newState == STATE_EXPANDED) {
                    layoutParams.height = fullHeight - getNavigationBarHeight()
                    tagBottomSheetBehavior.isDraggable = false
                } else if (newState == STATE_HALF_EXPANDED) {
                    layoutParams.height = (fullHeight - getNavigationBarHeight()) / 2
                }
                tag_edit_menu.layoutParams = layoutParams
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        val layers = mutableListOf(LayerItem("1", "Слой 1", true), LayerItem("2", "Слой 2", false))
        val layersList = ItemsList(layers)
        val adapter = LayersAdapter(
            holderType = LayerViewHolder::class,
            layoutId = R.layout.item_layer,
            dataSource = layersList,
            onClick = { layer ->
                print("Layer $layer clicked")
                layer.isVisible = !layer.isVisible
                },
                onRemove = {
                    // todo: check that user can delete this layer and delete it
                },
        )
        layers_list.adapter = adapter
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        layers_list.layoutManager = layoutManager

        hide_all_card.setOnClickListener {
            show_all_card.visibility = View.VISIBLE
            hide_all_card.visibility = View.INVISIBLE
            layersList.items.forEach {
                it.isVisible = false
            }
            layersList.rangeUpdate(0, layersList.size())
        }

        show_all_card.setOnClickListener {
            hide_all_card.visibility = View.VISIBLE
            show_all_card.visibility = View.INVISIBLE
            layersList.items.forEach {
                it.isVisible = true
            }
            layersList.rangeUpdate(0, layersList.size())
        }

//
//        menu.setOnClickListener {
//            val bottomSheet = bottom_sheet
//            bottomSheet.visibility = View.VISIBLE
//            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet!!)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }
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
        mapObjects.clear()
        for (searchResult in response.getCollection().getChildren()) {
            val naa = searchResult.obj!!.name
            val mm = searchResult.obj!!.descriptionText
            val resultLocation = searchResult.obj!!.geometry[0].point
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(this, R.drawable.search_result)
                )
            }
        }
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

    }

    override fun onMapLongTap(p0: Map, p1: Point) {
//        p0.mapObjects.addPlacemark(p1)
        //НЕ РОБИТ!
        val y = mapview.map.cameraPosition.zoom.roundToInt()
        searchSession = searchManager!!.submit(p1, y, SearchOptions(), this)
        mapview.getMap().getMapObjects().addPlacemark(
            p1,
            ImageProvider.fromResource(this, R.drawable.search_result)

        )
        val tagBottomSheetBehavior = from(tag_edit_menu)
        tagBottomSheetBehavior.isDraggable = true
        tagBottomSheetBehavior.setState(STATE_HALF_EXPANDED)
    }
}