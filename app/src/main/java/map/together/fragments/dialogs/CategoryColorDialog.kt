package map.together.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.category_color_dialog.*
import kotlinx.android.synthetic.main.category_color_dialog.view.*
import map.together.R
import map.together.items.CategoryItem

class CategoryColorDialog(
    private val item: CategoryItem,
    private val existing: MutableList<CategoryItem>,
    private val listener: CategoryDialogListener
) : DialogFragment() {

    companion object {
        val COLORS_ARRAY = listOf(
                R.color.red,
                R.color.orange,
                R.color.yellow,
                R.color.green,
                R.color.l_blue,
                R.color.blue,
                R.color.purple,
                R.color.grey,
                R.color.black,
                R.color.pink,
                R.color.l_green,
                R.color.acid,
        )
    }

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
            view.category_name.setText(item.name)
            val oldName = item.name
            selectedColor = item.colorRecourse
            selecded!!.setColorFilter(
                ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[item.colorRecourse]),
                android.graphics.PorterDuff.Mode.SRC_IN
            );

            colorRed!!.setOnClickListener {
                selectedColor = 0
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_red

            }
            colorOrange!!.setOnClickListener {
                selectedColor = 1
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_orange

            }
            colorYellow!!.setOnClickListener {
                selectedColor = 2
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_yellow

            }
            colorGreen!!.setOnClickListener {
                selectedColor = 3
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_green

            }
            colorLightBlue!!.setOnClickListener {
                selectedColor = 4
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_light_blue

            }
            colorBlue!!.setOnClickListener {
                selectedColor = 5
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_blue

            }
            colorPurple!!.setOnClickListener {
                selectedColor = 6
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_purple

            }
            colorGray!!.setOnClickListener {
                selectedColor = 7
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_gray

            }
            colorBlack!!.setOnClickListener {
                selectedColor = 8
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_black

            }
            colorPink!!.setOnClickListener {
                selectedColor = 9
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_pink

            }
            colorLightGreen!!.setOnClickListener {
                selectedColor = 10
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_light_green

            }
            colorAcid!!.setOnClickListener {
                selectedColor = 11
                selecded!!.setColorFilter(
                    ContextCompat.getColor(this.requireContext(), COLORS_ARRAY[selectedColor]),
                    android.graphics.PorterDuff.Mode.SRC_IN
                );
                drawableColor = category_color_acid

            }

            save!!.setOnClickListener {
                categoryName = view.category_name.text.toString()
                val names = existing.map { elem -> elem.name }
                if (categoryName != oldName && names.contains(categoryName)) {
                    MaterialDialog(this.requireContext()).show {
                        message(R.string.already_created_category)
                        negativeButton(R.string.close) {
                            it.cancel()
                        }
                    }
                } else if (categoryName.isEmpty()) {
//                    Toast.makeText(this.requireContext(), "Имя категории пустое", Toast.LENGTH_SHORT).show()
                    MaterialDialog(this.requireContext()).show {
                        message(R.string.empty_category)
                        negativeButton(R.string.close) {
                            it.cancel()
                        }
                    }
                } else {
                    item.name = categoryName
                    item.colorRecourse = selectedColor
                    listener.onSaveParameterClick(item)
                    dialog.dismiss()
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    interface CategoryDialogListener {
        fun onSaveParameterClick(item: CategoryItem)
    }
}