package com.animo.ru.ui.currentVisits

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.animo.ru.R
import com.animo.ru.models.Plan
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import java.text.SimpleDateFormat
import java.util.*


class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private var startTimeValue: String = ""
    private var endTimeValue: String = ""
    private val sdf = SimpleDateFormat("hh : mm : ss", Locale.getDefault())


    companion object {
        fun newInstance(clickPlan: Plan): CustomBottomSheetDialogFragment {
            val fragment = CustomBottomSheetDialogFragment()
            val args = Bundle()
            args.putSerializable("clickPlan", clickPlan)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start_visit, container, false)
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

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog;
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)


//        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
//            BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    //In the EXPANDED STATE apply a new MaterialShapeDrawable with rounded cornes
//                    val newMaterialShapeDrawable: MaterialShapeDrawable =
//                        createMaterialShapeDrawable(bottomSheet)
//                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
//        })


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

//    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable {
//        val shapeAppearanceModel =
//            //Create a ShapeAppearanceModel with the same shapeAppearanceOverlay used in the style
//            ShapeAppearanceModel.builder(context, 0, R.style.CustomShapeAppearanceBottomSheetDialog)
//                .build()
//
//        //Create a new MaterialShapeDrawable (you can't use the original MaterialShapeDrawable in the BottoSheet)
//        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
//        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
//        //Copy the attributes in the new MaterialShapeDrawable
//        newMaterialShapeDrawable.initializeElevationOverlay(context)
//        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
//        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
//        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
//        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
//        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
//        return newMaterialShapeDrawable
//    }
}