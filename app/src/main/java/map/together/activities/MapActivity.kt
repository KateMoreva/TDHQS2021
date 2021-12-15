package map.together.activities

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.coroutines.InternalCoroutinesApi
import com.yandex.runtime.image.ImageProvider
import map.together.R
import com.yandex.mapkit.map.GeoObjectSelectionMetadata

import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import map.together.utils.logger.Logger
import com.yandex.mapkit.ZoomRange

import kotlin.reflect.jvm.internal.impl.types.checker.ClassicTypeSystemContext.DefaultImpls.projection

import com.yandex.mapkit.layers.LayerOptions

import android.R.style
import android.graphics.PointF
import com.yandex.mapkit.layers.Layer
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Rect
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.tiles.TileProvider

import com.yandex.mapkit.resource_url_provider.ResourceUrlProvider
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder


class MapActivity : BaseFragmentActivity(), GeoObjectTapListener, InputListener {

    private val DRAGGABLE_PLACEMARK_CENTER = Point(59.948, 30.323)
    private val ANIMATED_PLACEMARK_CENTER = Point(59.948, 30.318)

    val currentPlaces: MutableList<Point> = ArrayList()
    var polyline: Polyline = Polyline()
    var prevPolyline: Polyline = Polyline()
    var mapObjects: MapObjectCollection? = null
    var is_line_point_click = false

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
        val selectionMetadata = geoObjectTapEvent
            .geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        if (selectionMetadata != null) {
            mapview.map.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
        }
        return selectionMetadata != null
    }

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
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
            if (is_line_point_click) {
                prevPolyline = polyline
            }
            is_line_point_click = !is_line_point_click
            if (is_line_point_click) {
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
    override fun onMapTap(p0: Map, p1: Point) {
        TODO("Not yet implemented")
    }

    override fun onMapLongTap(p0: Map, p1: Point) {
        p0.mapObjects.addPlacemark(p1)
        //НЕ РОБИТ!
        p0.mapObjects.addPlacemark(
            p1,
            ImageProvider.fromResource(this, R.drawable.ic_baseline_location_on_24), IconStyle(
                PointF(p1.latitude.toFloat(), p1.longitude.toFloat()),
                RotationType.NO_ROTATION,
                1F,
                false,
                true,
                1F,
                Rect(
                    PointF(p1.latitude.toFloat(), p1.longitude.toFloat()),
                    PointF(p1.latitude.toFloat(), p1.longitude.toFloat())
                )
            )
        )
    }
}