package map.together.utils.auth

import map.together.model.UserSignUpInfo


object AuthorizationHelper {

    private const val EMAIL_PATTERN =
        "^(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$"

    private const val PASSWORD_PATTERN = "[a-zA-Z0-9_/!?&*.,]{6,40}"

    private const val NAME_PATTERN = "[a-zA-Zа-яА-Я0-9]{2,50}"

    @SignUpDataCorrectType
    fun checkCorrectnessInputData(userSignUpInfo: UserSignUpInfo): Int {
        return if (!isCorrectEmail(userSignUpInfo.email)) {
            SignUpDataCorrectType.INCORRECT_EMAIL
        } else if (!isCorrectPassword(userSignUpInfo.password)) {
            SignUpDataCorrectType.INCORRECT_PASSWORD
        } else if (userSignUpInfo.password != userSignUpInfo.confirmPassword) {
            SignUpDataCorrectType.INCORRECT_CONFIRM_PASSWORD
        } else if (!isCorrectName(userSignUpInfo.userName)) {
            SignUpDataCorrectType.INCORRECT_NAME
        } else {
            SignUpDataCorrectType.CORRECT
        }
    }

    fun isCorrectName(name: String): Boolean = name.matches(Regex(NAME_PATTERN))

    private fun isCorrectPassword(password: String): Boolean = password.matches(Regex(PASSWORD_PATTERN))

    private fun isCorrectEmail(email: String): Boolean = email.matches(Regex(EMAIL_PATTERN))
}