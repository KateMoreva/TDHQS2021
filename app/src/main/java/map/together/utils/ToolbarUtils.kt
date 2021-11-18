package map.together.utils

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import map.together.R


fun AppCompatActivity.initHomeButton() {
    this.supportActionBar?.displayOptions = ActionBar.DISPLAY_HOME_AS_UP
    this.supportActionBar?.setHomeButtonEnabled(true)
    this.supportActionBar?.setDisplayShowHomeEnabled(true)
    this.supportActionBar?.setHomeAsUpIndicator(this.getDrawable(R.drawable.ic_home_button))
}
