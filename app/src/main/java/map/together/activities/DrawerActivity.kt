package map.together.activities

import android.os.Bundle
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.mikepenz.materialdrawer.holder.ImageHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.util.addItems
import kotlinx.android.synthetic.main.activity_base_fragment.*
import kotlinx.coroutines.InternalCoroutinesApi
import map.together.R
import map.together.controllers.DrawerController
import map.together.controllers.NavigationMenuController
import map.together.navmenu.NavigationMenuListener
import map.together.repository.AuthRepository
import map.together.repository.CurrentUserRepository


abstract class DrawerActivity : BaseActivity() {

    companion object {
        const val SETTINGS_ITEM_ID = 4L
        const val EXIT_ITEM_ID = 5L
    }

    private val profile: ProfileDrawerItem = ProfileDrawerItem()

    private var drawerController: NavigationMenuController? = null

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerController = DrawerController(root_drawer, slider)
        setupItems()
        slider.apply {
            onDrawerItemClickListener = { _, item, _ ->
                when (item.identifier) {
                    EXIT_ITEM_ID -> {
                        buildExitDialog()
                    }
                }
                closeNavMenu()
                true
            }
        }
        attachCurrentUserToSlider()
        CurrentUserRepository.currentUser.observe(this, Observer { attachCurrentUserToSlider() })
    }

    private fun buildExitDialog() {
        MaterialDialog(this).show {
            title(R.string.quit)
            message(R.string.quit_confirm_msg)
            positiveButton(R.string.preference_exit) {
                AuthRepository.doOnLogout(this@DrawerActivity)
            }
            negativeButton(R.string.close) {
                it.cancel()
            }
        }
    }

    private fun setupItems() {

        val settingsItem = PrimaryDrawerItem().apply {
            name = StringHolder(R.string.drawer_item_settings)
            identifier = SETTINGS_ITEM_ID
            isSelectable = false
        }

        val exitItem = PrimaryDrawerItem().apply {
            name = StringHolder(R.string.preference_exit)
            identifier = EXIT_ITEM_ID
            isSelectable = false
        }

        slider.addItems(
                DividerDrawerItem(),
                settingsItem,
                exitItem
        )
    }

    override fun onBackPressed() {
        if (root_drawer.isDrawerOpen(slider)) {
            root_drawer.closeDrawer(slider)
        } else {
            super.onBackPressed()
        }
    }

    fun openNavMenu() {
        drawerController?.openMenu()
    }

    fun closeNavMenu() {
        drawerController?.closeMenu()
    }

    fun addListener(listener: NavigationMenuListener) {
        drawerController?.addMenuListener(listener)
    }

    fun removeListener(listener: NavigationMenuListener) {
        drawerController?.removeMenuListener(listener)
    }

    private fun attachCurrentUserToSlider() {
        val userInfo = CurrentUserRepository.currentUser.value
        userInfo?.let {
            when {
                it.pictureUrlStr != null -> profile.icon = ImageHolder(it.pictureUrlStr)
                else -> profile.icon = null
            }
            profile.name = StringHolder(it.userName)
            profile.description = StringHolder(it.email)
        }
    }

    protected open fun canOpenNavMenuFromToolbar(): Boolean = false
}