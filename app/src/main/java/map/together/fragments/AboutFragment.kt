package map.together.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_setting_about.*
import map.together.R

class AboutFragment : BaseFragment() {

    private var about: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        about = information_about_text_field_id
        return rootView
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_setting_about
    override fun getAppbarTitle(): Int = R.string.about_app
}