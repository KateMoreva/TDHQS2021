package map.together.activities

import android.os.Bundle
import android.view.View
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R


class MapActivity : BaseFragmentActivity() {

    var polyline: Polyline = Polyline()
    var prevPolyline: Polyline = Polyline()


    var is_line_point_click = false

    private val listener = object : InputListener {
        override fun onMapLongTap(p0: Map, p1: Point) {}
        override fun onMapTap(p0: Map, p1: Point) {
            polyline.points.add(p1)
            p0.mapObjects.clear()
            p0.mapObjects.addPolyline(Polyline(polyline.points))
        }
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

        line_point.setOnClickListener(fun(_: View) {
            if (is_line_point_click) {
                prevPolyline = polyline
            }
            is_line_point_click = !is_line_point_click
            if (is_line_point_click) {
                polyline = Polyline(ArrayList<Point>())
                mapview.map.addInputListener(listener)
            } else {
                mapview.map.removeInputListener(listener)
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
}