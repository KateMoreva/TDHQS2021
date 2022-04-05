package map.together.utils.logger

import android.util.Log

object Logger {

    fun d(source: Any, vararg message: Any) {
        Log.d(source::class.qualifiedName, message.joinToString(separator = " "))
    }

    fun e(source: Any, vararg message: Any) {
        Log.e(source::class.qualifiedName, message.joinToString(separator = " "))
    }

    fun i(source: Any, vararg message: Any) {
        Log.e(source::class.qualifiedName, message.joinToString(separator = " "))
    }

    fun w(source: Any, vararg message: Any) {
        Log.w(source::class.qualifiedName, message.joinToString(separator = " "))
    }
}