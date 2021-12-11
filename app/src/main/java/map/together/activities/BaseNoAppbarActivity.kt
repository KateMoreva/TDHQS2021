package map.together.activities

import map.together.R


abstract class BaseNoAppbarActivity : BaseActivity() {

    override fun getActivityLayoutId(): Int = R.layout.activity_no_appbar
}