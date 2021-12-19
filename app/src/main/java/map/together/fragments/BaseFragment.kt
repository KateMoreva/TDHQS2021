package map.together.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import map.together.R
import map.together.activities.AppbarActivity

abstract class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getFragmentLayoutId(), container, false) as ViewGroup;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppbarActivity)?.setToolbarTitle(getString(getAppbarTitle()))
    }

    abstract fun getFragmentLayoutId(): Int

    abstract fun getAppbarTitle(): Int
}