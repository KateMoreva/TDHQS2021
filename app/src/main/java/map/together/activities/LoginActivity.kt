package map.together.activities

import androidx.fragment.app.Fragment
import map.together.fragments.LoginFragment

class LoginActivity : BaseNoAppbarActivity() {

    override fun getSupportingFragment(): Fragment = LoginFragment()
}
