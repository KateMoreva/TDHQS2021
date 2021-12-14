package map.together.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import kotlinx.android.synthetic.main.fragment_map.*
import map.together.R


private val listener = object : InputListener {
    override fun onMapLongTap(p0: Map, p1: Point) {}
    override fun onMapTap(p0: Map, p1: Point) {
        MapFragment.polyline.points.add(p1)
        p0.mapObjects.clear()
        p0.mapObjects.addPolyline(Polyline(MapFragment.polyline.points))
    }
}

class MapFragment : BaseFragment() {

    var is_line_point_click = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!is_init) {
            MapKitFactory.setApiKey("91d7da16-2d86-4367-9ee8-4092731bbd2f")
            MapKitFactory.initialize(inflater.context)
            is_init = true
        }
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_text_field.visibility = View.INVISIBLE

        mapview.map.move(
            CameraPosition(Point(59.9408455, 30.3131542), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )

        zoom_in_id.setOnClickListener(fun(it: View) {
            mapview.map.move(
                CameraPosition(
                    mapview.map.cameraPosition.target,
                    mapview.map.cameraPosition.zoom.plus(1.0f), 0.0f, 0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1F),
                null
            )
        })

        zoom_out_id.setOnClickListener(fun(it: View) {
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

        line_point.setOnClickListener(fun(it: View) {
            is_line_point_click = !is_line_point_click
            if (is_line_point_click) {
                polyline = Polyline(ArrayList<Point>())
                mapview.map.addInputListener(listener)
            } else {
                mapview.map.removeInputListener(listener)
                mapview.map.mapObjects.clear()
            }
        })

        search_button_id.setOnClickListener(fun(it: View) {
            if (search_text_field.visibility == View.INVISIBLE) {
                search_text_field.visibility = View.VISIBLE
            } else {
                search_text_field.visibility = View.INVISIBLE
            }
        })
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_map

    companion object {
        var is_init = false
        var polyline: Polyline = Polyline()
    }

    override fun onStop() {
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }
}