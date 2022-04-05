package map.together.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_login.*
import map.together.R


class LoginFragment : BaseFragment() {

    private var imageView: ImageView? = null
    private var emailEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var forgotPasswordTextView: TextView? = null
    private var notExistAccTextView: TextView? = null
    private var loginButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        emailEditText = email_et
        passwordEditText = password_et
        forgotPasswordTextView = forgot_password_tv
        notExistAccTextView = not_exist_acc_tv
        loginButton = login_button
        return rootView
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_login
    override fun getAppbarTitle(): Int = R.string.login
}