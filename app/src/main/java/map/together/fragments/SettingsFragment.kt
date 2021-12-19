package map.together.fragments


import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings.*
import map.together.R
import map.together.activities.BaseFragmentActivity

class SettingsFragment : BaseFragment() {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        account_button_id?.setOnClickListener {
            (activity as? BaseFragmentActivity)?.router?.showSettingsProfilePage()
        }
        change_category_button_id?.setOnClickListener {
            (activity as? BaseFragmentActivity)?.router?.showSettingsCategoriesPage()
        }
        about_button_id?.setOnClickListener {
            (activity as? BaseFragmentActivity)?.router?.showSettingsAboutPage()
        }

    }
}