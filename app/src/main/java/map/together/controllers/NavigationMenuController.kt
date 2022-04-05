package map.together.controllers

import map.together.navmenu.NavigationMenuListener

interface NavigationMenuController {

    fun openMenu()

    fun closeMenu()

    fun addMenuListener(listener: NavigationMenuListener)

    fun removeMenuListener(listener: NavigationMenuListener)

}