package com.animo.ru.ui.preparations.infoPackage

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.animo.ru.R
import com.animo.ru.models.InfoPackage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel


class ShareBottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(infoPackage: InfoPackage): ShareBottomSheetDialog {
            val fragment = ShareBottomSheetDialog()
            val args = Bundle()
            args.putSerializable("infoPackage", infoPackage)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_share, container, false)

        val COUNTRIES = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")

        val adapter = context?.let {
            ArrayAdapter(
                it,
                R.layout.item_spinner,
                COUNTRIES
            )
        }

        val editTextFilledExposedDropdown: AutoCompleteTextView =
            view.findViewById(R.id.filled_exposed_dropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)


        val allTags = mutableListOf("Love", "Passion", "Peace", "Hello", "Test")
        val currentTags = mutableListOf<String>()

        val chipGroup: ChipGroup = view.findViewById(R.id.mainTagChipGroup)
        val autoCompleteTextView: AutoCompleteTextView =
            view.findViewById(R.id.mainTagAutoCompleteTextView)

        loadTagsUi(autoCompleteTextView, chipGroup, currentTags, allTags)

        return view
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        val currentPlan: Plan = arguments?.getSerializable("clickPlan") as Plan


//        visit_btn.setOnClickListener {
//            if (startTimeValue == "") {
//                startTimeValue = sdf.format(Date())
//                start_time.text = "Начало визита: $startTimeValue"
//                visit_btn.text = getString(R.string.btn_text_end_visit)
//            } else if (endTimeValue == "") {
//                endTimeValue = sdf.format(Date())
//                endTime.text = "Конец визита: $endTimeValue"
//                visit_btn.visibility = View.GONE
//            }
//        }


    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog;

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //In the EXPANDED STATE apply a new MaterialShapeDrawable with rounded cornes
                    val newMaterialShapeDrawable: MaterialShapeDrawable =
                        createMaterialShapeDrawable(bottomSheet)
                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable {
        val shapeAppearanceModel =
            //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
            ShapeAppearanceModel.builder(context, 0, R.style.CustomShapeAppearanceBottomSheetDialog)
                .build()

        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottoSheet)
        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        //Copy the attributes in the new MaterialShapeDrawable
        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }


    private fun loadTagsUi(
        autoCompleteTextView: AutoCompleteTextView,
        chipGroup: ChipGroup,
        currentTags: MutableList<String>,
        allTags: List<String>
    ) {

        val adapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_dropdown_item_1line,
                allTags
            )
        }
        autoCompleteTextView.setAdapter(adapter)

        fun addTag(name: String) {
            if (name.isNotEmpty() && !currentTags.contains(name) && allTags.contains(name)) {
                addChipToGroup(name, chipGroup, currentTags)
                currentTags.add(name)
            }
        }

        // select from auto complete
        autoCompleteTextView.setOnItemClickListener { adapterView, _, position, _ ->
            autoCompleteTextView.text = null
            val name = adapterView.getItemAtPosition(position) as String
            addTag(name)
        }

        // done keyboard button is pressed
        autoCompleteTextView.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val name = textView.text.toString()
                textView.text = null
                addTag(name)
                return@setOnEditorActionListener true
            }
            false
        }

        // space or comma is detected
        autoCompleteTextView.addTextChangedListener {
            if (it != null && it.isEmpty()) {
                return@addTextChangedListener
            }

            if (it?.last() == ',' || it?.last() == ' ') {
                val name = it.substring(0, it.length - 1)
                addTag(name)

                autoCompleteTextView.text = null
            }
        }

        // initialize
        for (tag in currentTags) {
            addChipToGroup(tag, chipGroup, currentTags)
        }
    }

    private fun addChipToGroup(name: String, chipGroup: ChipGroup, items: MutableList<String>) {
        val chip = Chip(context)
        chip.text = name

        chip.isClickable = true
        chip.isCheckable = false
        chip.isCloseIconVisible = true

        chipGroup.addView(chip)

        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            items.remove(name)
        }
    }
}