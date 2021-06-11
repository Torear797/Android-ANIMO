package com.animo.ru.utilities

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class MultiSelectionSpinner : AppCompatSpinner, OnMultiChoiceClickListener {
    interface OnMultipleItemsSelectedListener {
        fun selectedIndices(indices: List<Int?>?, spinner: MultiSelectionSpinner?)
        fun selectedStrings(strings: List<String?>?, spinner: MultiSelectionSpinner?)
    }

    private var listener: OnMultipleItemsSelectedListener? = null
    var _items: Array<String>? = null
    var mSelection: BooleanArray? = null
    var mSelectionAtStart: BooleanArray? = null
    var _itemsAtStart: String? = null
    var title = "Please select!!!"
    var simple_adapter: ArrayAdapter<String>

    constructor(context: Context) : super(context) {
        simple_adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item
        )
        super.setAdapter(simple_adapter)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        simple_adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item
        )
        super.setAdapter(simple_adapter)
    }

    fun setListener(listener: OnMultipleItemsSelectedListener?) {
        this.listener = listener
    }

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        if (mSelection != null && which < mSelection!!.size) {
            mSelection!![which] = isChecked
            simple_adapter.clear()
            simple_adapter.add(buildSelectedItemString())
        } else {
            throw IllegalArgumentException(
                "Argument 'which' is out of bounds."
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(title)
        builder.setMultiChoiceItems(_items, mSelection, this)
        _itemsAtStart = selectedItemsAsString
        builder.setNeutralButton("Выбрать все"
        ) { _, _ -> selectAll() }
        builder.setPositiveButton("OK"
        ) { _, _ ->
            System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection!!.size)
            listener!!.selectedIndices(selectedIndices, this@MultiSelectionSpinner)
            listener!!.selectedStrings(selectedStrings, this@MultiSelectionSpinner)
        }
        builder.setNegativeButton("Закрыть"
        ) { _, _ ->
            simple_adapter.clear()
            simple_adapter.add(_itemsAtStart)
            System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart!!.size)
        }
        builder.show()
        return true
    }

    private fun selectAll() {
        Arrays.fill(mSelection, true)
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    override fun setAdapter(adapter: SpinnerAdapter) {
        throw RuntimeException(
            "setAdapter is not supported by MultiSelectSpinner."
        )
    }

    fun setItems(items: Array<String>?) {
        _items = items
        mSelection = BooleanArray(_items!!.size)
        mSelectionAtStart = BooleanArray(_items!!.size)
        simple_adapter.clear()
        simple_adapter.add(_items!![0])
        Arrays.fill(mSelection, false)
        mSelection!![0] = true
        mSelectionAtStart!![0] = true
    }

    fun setItems(items: List<String>) {
        _items = items.toTypedArray()
        mSelection = BooleanArray(_items!!.size)
        mSelectionAtStart = BooleanArray(_items!!.size)
        simple_adapter.clear()
        simple_adapter.add(_items!![0])
        Arrays.fill(mSelection, false)
        mSelection!![0] = true
    }

    fun setSelection(selection: Array<String>) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart!![i] = false
        }
        for (cell: String in selection) {
            for (j in _items!!.indices) {
                if (_items!![j] == cell) {
                    mSelection!![j] = true
                    mSelectionAtStart!![j] = true
                }
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    fun setSelection(selection: List<String>) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart!![i] = false
        }
        for (sel: String in selection) {
            for (j in _items!!.indices) {
                if (_items!![j] == sel) {
                    mSelection!![j] = true
                    mSelectionAtStart!![j] = true
                }
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    override fun setSelection(index: Int) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart!![i] = false
        }
        if (index >= 0 && index < mSelection!!.size) {
            mSelection!![index] = true
            mSelectionAtStart!![index] = true
        } else {
            throw IllegalArgumentException(
                "Index " + index
                        + " is out of bounds."
            )
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    fun setSelection(selectedIndices: IntArray) {
        for (i in mSelection!!.indices) {
            mSelection!![i] = false
            mSelectionAtStart!![i] = false
        }
        for (index: Int in selectedIndices) {
            if (index >= 0 && index < mSelection!!.size) {
                mSelection!![index] = true
                mSelectionAtStart!![index] = true
            } else {
                throw IllegalArgumentException(
                    ("Index " + index
                            + " is out of bounds.")
                )
            }
        }
        simple_adapter.clear()
        simple_adapter.add(buildSelectedItemString())
    }

    val selectedStrings: List<String?>
        get() {
            val selection: MutableList<String?> = LinkedList()
            for (i in _items!!.indices) {
                if (mSelection!![i]) {
                    selection.add(_items!![i])
                }
            }
            return selection
        }
    val selectedIndices: List<Int?>
        get() {
            val selection: MutableList<Int?> = LinkedList()
            for (i in _items!!.indices) {
                if (mSelection!![i]) {
                    selection.add(i)
                }
            }
            return selection
        }

    private fun buildSelectedItemString(): String {
        val sb = StringBuilder()
        var foundOne = false
        for (i in _items!!.indices) {
            if (mSelection!![i]) {
                if (foundOne) {
                    sb.append(", ")
                }
                foundOne = true
                sb.append(_items!![i])
            }
        }
        return sb.toString()
    }

    val selectedItemsAsString: String
        get() {
            val sb = StringBuilder()
            var foundOne = false
            for (i in _items!!.indices) {
                if (mSelection!![i]) {
                    if (foundOne) {
                        sb.append(", ")
                    }
                    foundOne = true
                    sb.append(_items!![i])
                }
            }
            return sb.toString()
        }
}