package map.together.api

import okhttp3.Credentials

object ApiUtils {
    fun encodeEmailAndPasswordToAuthorizationHeader(email: String, password: String): String =
        Credentials.basic(email, password)
}
