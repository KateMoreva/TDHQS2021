package map.together.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_base.*
import map.together.R

abstract class BaseActivity : AppCompatActivity() {

    private var mainContainer: FrameLayout? = null
    private var coordinator: CoordinatorLayout? = null
    private var toolbar: Toolbar? = null
    private var appbarLayout: AppBarLayout? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(this::class.qualifiedName, "Base activity start onCreate")
        super.onCreate(savedInstanceState)

        setContentView(getActivityLayoutId())
        setupActivity()
        setupToolbar()
        setFragments()

        Log.d(this::class.qualifiedName, "Base activity end onCreate")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupActivity() {
        mainContainer = main_container
        coordinator = coordinator_layout
        toolbar = base_toolbar
        appbarLayout = appbar_layout
    }

    private fun setupToolbar() {
        if (toolbar != null && appbarLayout != null) {
            if (isToolbarEnabled()) {
                toolbar?.title = getToolbarTitle()
                setSupportActionBar(toolbar)
            } else {
                appbarLayout?.visibility = View.GONE
            }
        }
    }

    private fun setFragments() {
        Log.d(this::class.qualifiedName, "start add fragment")
        supportFragmentManager.beginTransaction().add(R.id.main_container, getSupportingFragment()).commit()
        supportFragmentManager.executePendingTransactions()
        Log.d(this::class.qualifiedName, "end add fragment")
    }

    protected open fun getToolbarTitle(): String = getString(R.string.app_name)

    protected open fun isToolbarEnabled(): Boolean = true

    protected open fun getActivityLayoutId(): Int = R.layout.activity_base

    abstract fun getSupportingFragment(): Fragment
}