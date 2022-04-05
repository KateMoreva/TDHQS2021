package map.together.utils

import android.content.Context
import android.text.InputType
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.InputCallback
import com.afollestad.materialdialogs.input.input
import kotlin.math.max

@JvmOverloads
fun showSimpleMaterialDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveClicked: DialogCallback? = null,
    onNegativeClicked: DialogCallback? = null,
) {
    MaterialDialog(context)
        .title(text = title)
        .message(text = message)
        .positiveButton(
            text = positiveButtonText,
            click = onPositiveClicked
        ).negativeButton(
            text = negativeButtonText,
            click = onNegativeClicked
        ).show()
}

@JvmOverloads
fun showSimpleInputMaterialDialog(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String? = null,
    negativeButtonText: String? = null,
    onPositiveClicked: DialogCallback? = null,
    onNegativeClicked: DialogCallback? = null,
    prefillData: CharSequence? = null,
    inputType: Int = InputType.TYPE_CLASS_TEXT,
    maxLength: Int? = null,
    inputCallback: InputCallback = null
) {
    MaterialDialog(context)
        .input(
            prefill = prefillData,
            inputType = inputType,
            maxLength = maxLength,
            callback = inputCallback
        )
        .title(text = title)
        .message(text = message)
        .positiveButton(
            text = positiveButtonText,
            click = onPositiveClicked
        ).negativeButton(
            text = negativeButtonText,
            click = onNegativeClicked
        ).show()
}