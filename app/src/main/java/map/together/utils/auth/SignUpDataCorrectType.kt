package map.together.utils.auth

import androidx.annotation.IntDef

@IntDef(
        SignUpDataCorrectType.INCORRECT_EMAIL,
        SignUpDataCorrectType.INCORRECT_PASSWORD,
        SignUpDataCorrectType.INCORRECT_CONFIRM_PASSWORD,
        SignUpDataCorrectType.INCORRECT_NAME,
        SignUpDataCorrectType.CORRECT
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class SignUpDataCorrectType {
    companion object {
        const val INCORRECT_EMAIL = 0
        const val INCORRECT_PASSWORD = 1
        const val INCORRECT_CONFIRM_PASSWORD = 2
        const val INCORRECT_NAME = 3
        const val CORRECT = 4
    }
}