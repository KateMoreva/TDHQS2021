package map.together.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.activities.AppbarActivity
import map.together.activities.BaseActivity
import map.together.db.AppDatabase
import map.together.lifecycle.Router

@InternalCoroutinesApi
abstract class BaseFragment : Fragment() {
    protected var router: Router? = null
    protected val taskContainer: CompositeDisposable = CompositeDisposable()
    protected var database: AppDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        router = (activity as BaseActivity).router
        database = (activity as BaseActivity).database
        return inflater.inflate(getFragmentLayoutId(), container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppbarActivity)?.setToolbarTitle(getString(getAppbarTitle()))
    }

    abstract fun getFragmentLayoutId(): Int

    abstract fun getAppbarTitle(): Int
}