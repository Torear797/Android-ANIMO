package com.animo.ru.ui.activity_tab.plans_reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.LoyaltyQuestion
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import java.util.ArrayList


class LoyaltyQuestionsAdapter(
    private val questions: MutableMap<Int, LoyaltyQuestion>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LoyaltyQuestionsAdapter.BaseViewHolder<*>>() {

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    interface OnItemClickListener {
        fun onChangeOption()
    }

    inner class QuestionHolder(itemView: View) : BaseViewHolder<LoyaltyQuestion>(itemView) {
        private val questionName: CheckBox = itemView.findViewById(R.id.checkBox)


        override fun bind(item: LoyaltyQuestion) {
            questionName.text = item.text

            if (item.isChecked) {
                questionName.isChecked = true
            }
        }
    }

    inner class QuestionHolderWithOptions(itemView: View) :
        BaseViewHolder<LoyaltyQuestion>(itemView) {
        private val layoutTextView: TextInputLayout = itemView.findViewById(R.id.layout)
        private val autoCompleteTextView: AutoCompleteTextView = itemView.findViewById(R.id.mainTagAutoCompleteTextView)
        private val chipGroup: ChipGroup = itemView.findViewById(R.id.mainTagChipGroup)
        private val currentTags = mutableListOf<String>()

        override fun bind(item: LoyaltyQuestion) {
            layoutTextView.hint = item.text
            val list: MutableList<String> = ArrayList<String>()

            item.options.forEach { (_, value) -> list.add(value.text) }

            loadTagsUi(autoCompleteTextView, chipGroup, list)
        }

        private fun loadTagsUi(
            autoCompleteTextView: AutoCompleteTextView,
            chipGroup: ChipGroup,
            allTags: List<String>
        ) {

            val adapter = ArrayAdapter(
                autoCompleteTextView.context,
                android.R.layout.simple_spinner_dropdown_item,
                allTags
            )
            autoCompleteTextView.setAdapter(adapter)

            fun addTag(name: String) {
                if (name.isNotEmpty() && !currentTags.contains(name) && allTags.contains(name)) {
                    currentTags.add(name)
                    addChipToGroup(name, chipGroup)
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
                addChipToGroup(tag, chipGroup)
            }
        }

        private fun addChipToGroup(name: String, chipGroup: ChipGroup) {
            val chip = Chip(chipGroup.context)
            chip.text = name

            chip.isClickable = true
            chip.isCheckable = false
            chip.isCloseIconVisible = true

            chipGroup.addView(chip)


            chip.setOnCloseIconClickListener {
                chipGroup.removeView(chip)
                currentTags.remove(name)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val countOptions = questions[getPositionKey(position)]?.options?.size

        return if (countOptions != null && countOptions > 0) {
            R.layout.item_multi_spinner
        } else {
            R.layout.item_checkbox
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<*> {

        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)

        return if (viewType == R.layout.item_checkbox) {
            QuestionHolder(view)
        } else {
            QuestionHolderWithOptions(view)
        }

    }

    override fun getItemCount(): Int = questions.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in questions) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val data = questions[getPositionKey(position)]

        when (holder) {
            is QuestionHolder -> holder.bind(data as LoyaltyQuestion)
            is QuestionHolderWithOptions -> holder.bind(data as LoyaltyQuestion)
        }
    }
}