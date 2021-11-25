package map.together.controllers

import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView
import map.together.navmenu.NavigationMenuListener


class DrawerController(
        private val drawerLayout: DrawerLayout,
        private val sliderView: MaterialDrawerSliderView
) : NavigationMenuController {

    private val listeners: MutableList<NavigationMenuListener> = mutableListOf()

    override fun openMenu() {
        drawerLayout.openDrawer(sliderView)
        for (listener in listeners) {
            listener.onOpen()
        }
    }

    override fun closeMenu() {
        drawerLayout.closeDrawer(sliderView)
        for (listener in listeners) {
            listener.onClose()
        }
    }

    override fun addMenuListener(listener: NavigationMenuListener) {
        listeners.add(listener)
    }

    override fun removeMenuListener(listener: NavigationMenuListener) {
        listeners.remove(listener)
    }
}