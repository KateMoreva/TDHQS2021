package map.together.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.fragments.MapFragment
import map.together.lifecycle.Page
import map.together.lifecycle.Page.Fragment.MainMap.clazz
import kotlin.reflect.full.createInstance

class MapActivity : BaseFragmentActivity() {

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapview.map.move(
            CameraPosition(Point(59.9408455, 30.3131542), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )


    }


    override fun onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart()
        print("jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj")
        MapKitFactory.getInstance().onStart()
    }

    override fun getActivityLayoutId() = R.layout.activity_map
}