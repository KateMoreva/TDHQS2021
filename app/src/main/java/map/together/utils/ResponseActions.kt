package map.together.utils

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import map.together.R
import map.together.utils.logger.Logger
import retrofit2.Response

object ResponseActions {

    fun<T> onResponse(response: Response<T>, context: Context, okStatus: Int, failStatus: Int, okCallback: (T?) -> Unit) {
        when (response.code()) {
            okStatus -> {
                okCallback.invoke(response.body())
                Logger.d(this, "successful action with code ${response.code()}")
            }
            failStatus -> {
                MaterialDialog(context).show {
                    title(R.string.error)
                    message(text = response.errorBody()?.string())
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                Logger.d(this, response.code())
            }
            else -> {
                MaterialDialog(context).show {
                    title(R.string.error)
                    message(R.string.server_not_available)
                    negativeButton(R.string.close) {
                        it.cancel()
                    }
                }
                Logger.d(this, "unsupported code ${response.code()}")
            }
        }
    }

    fun onFail(throwable: Throwable, context: Context) {
        MaterialDialog(context).show {
            title(R.string.error)
            message(R.string.failed_response)
            negativeButton(R.string.close) {
                it.cancel()
            }
        }
        Logger.e(this, "Action failed : ${throwable.message}")
    }

}