package com.animo.ru.ui.preparations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.LastInfoPackage

class LastInfoPackageAdapter(
    private val lastInfoPackages: MutableMap<Int, LastInfoPackage>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<LastInfoPackageAdapter.LastInfoPackageHolder>() {

    interface OnItemClickListener {
        fun onItemClick(clickLIP: LastInfoPackage)
    }

    inner class LastInfoPackageHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var textInfoPackage: TextView = itemView.findViewById(R.id.lip_text)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                lastInfoPackages[getPositionKey(adapterPosition)]?.let { listener.onItemClick(it) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastInfoPackageHolder {
        return LastInfoPackageHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_last_info_package, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LastInfoPackageHolder, position: Int) {
        val lastInfoPackage = lastInfoPackages[getPositionKey(position)]
        holder.textInfoPackage.text = lastInfoPackage!!.pip_name
    }

    override fun getItemCount(): Int = lastInfoPackages.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in lastInfoPackages) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }
}