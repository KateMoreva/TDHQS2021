package map.together.activities

import androidx.fragment.app.Fragment
import map.together.fragments.LoginFragment

class LoginActivity : BaseNoAppbarActivity() {

    fun getSupportingFragment(): Fragment = LoginFragment()
}
