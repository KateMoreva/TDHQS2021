package map.together.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.category_color_dialog.*
import kotlinx.android.synthetic.main.category_color_dialog.view.*
import map.together.R

class CategoryColorDialog : DialogFragment() {
    var selectedColor: Int = 0
    var drawableColor: ImageView? = null
    var categoryName: String = ""

    var colorRed: ImageView? = null
    var colorOrange: ImageView? = null
    var colorYellow: ImageView? = null
    var colorGreen: ImageView? = null
    var colorLightBlue: ImageView? = null
    var colorBlue: ImageView? = null
    var colorPurple: ImageView? = null
    var colorGray: ImageView? = null
    var colorBlack: ImageView? = null
    var colorPink: ImageView? = null
    var colorLightGreen: ImageView? = null
    var colorAcid: ImageView? = null
    var save: Button? = null
    var selecded: ImageView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.category_color_dialog, null)
            builder.setView(view)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            colorRed = view.category_color_red
            colorOrange = view.category_color_orange
            colorYellow = view.category_color_yellow
            colorGreen = view.category_color_green
            colorLightBlue = view.category_color_light_blue
            colorBlue = view.category_color_blue
            colorPurple = view.category_color_purple
            colorGray = view.category_color_gray
            colorBlack = view.category_color_black
            colorPink = view.category_color_pink
            colorLightGreen = view.category_color_light_green
            colorAcid = view.category_color_acid
            save = view.save_category
            selecded = view.selected_category_color

            colorRed!!.setOnClickListener {
                selectedColor = R.color.red
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_red

            }
            colorOrange!!.setOnClickListener {
                selectedColor = R.color.orange
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_orange

            }
            colorYellow!!.setOnClickListener {
                selectedColor = R.color.yellow
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_yellow

            }
            colorGreen!!.setOnClickListener {
                selectedColor = R.color.green
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_green

            }
            colorLightBlue!!.setOnClickListener {
                selectedColor = R.color.l_blue
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_light_blue

            }
            colorBlue!!.setOnClickListener {
                selectedColor = R.color.blue
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_blue

            }
            colorPurple!!.setOnClickListener {
                selectedColor = R.color.purple
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_purple

            }
            colorGray!!.setOnClickListener {
                selectedColor = R.color.grey
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_gray

            }
            colorBlack!!.setOnClickListener {
                selectedColor = R.color.black
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_black

            }
            colorPink!!.setOnClickListener {
                selectedColor = R.color.pink
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_pink

            }
            colorLightGreen!!.setOnClickListener {
                selectedColor = R.color.l_green
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_light_green

            }
            colorAcid!!.setOnClickListener {
                selectedColor = R.color.acid
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), selectedColor),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_acid

            }

            save!!.setOnClickListener {
                categoryName = view.category_name.text.toString()
                dialog.dismiss()
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}