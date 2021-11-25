package map.together.controllers

import androidx.drawerlayout.widget.DrawerLayout
import map.together.navmenu.NavigationMenuListener


class DrawerController(
        private val drawerLayout: DrawerLayout,
) : NavigationMenuController {

    private val listeners: MutableList<NavigationMenuListener> = mutableListOf()

    override fun openMenu() {
        for (listener in listeners) {
            listener.onOpen()
        }
    }

    override fun closeMenu() {
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