package com.animo.ru.utilities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.animo.ru.models.GuideData

class GuideSpinnerAdapter(ctx: Context, data: ArrayList<GuideData>) :
    ArrayAdapter<GuideData>(ctx, 0, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val item = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            android.R.layout.simple_spinner_item,
            parent,
            false
        )

        val text: TextView = view.findViewById(android.R.id.text1)

        item.let {
            text.text = item?.name ?: ""

        }
        return view
    }
}