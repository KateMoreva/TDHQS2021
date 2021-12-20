package map.together.fragments


import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_settings.*
import map.together.R
import map.together.activities.BaseFragmentActivity
import map.together.utils.showSimpleMaterialDialog

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
        bug_report_button_id?.setOnClickListener {
            showSimpleMaterialDialog(
                context = view.context,
                title = view.context.getString(R.string.error),
                message = view.context.getString(R.string.error),
                positiveButtonText = view.context.getString(R.string.yes),
                negativeButtonText = view.context.getString(R.string.cancel),
                onPositiveClicked = { /* do smth */ },
            )
        }

        about_button_id?.setOnClickListener {
            (activity as? BaseFragmentActivity)?.router?.showSettingsAboutPage()
        }

    }

    override fun getAppbarTitle(): Int = R.string.settings
}