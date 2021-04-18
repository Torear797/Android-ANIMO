package com.animo.ru.ui.preparations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.LastInfoPackage
import com.animo.ru.utilities.deleteListener

class LastInfoPackageAdapter(
    private val lastInfoPackages: MutableMap<Int, LastInfoPackage>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<LastInfoPackageAdapter.LastInfoPackageHolder>() {

    interface OnItemClickListener {
        fun onItemClick(medicationId: Int, jumpId: Int)
    }

    inner class LastInfoPackageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textInfoPackage: TextView = itemView.findViewById(R.id.lip_text)

        fun bind(lastInfoPackage: LastInfoPackage) {
            textInfoPackage.text = lastInfoPackage.pip_name
        }
    }

    override fun onViewAttachedToWindow(holder: LastInfoPackageHolder) {
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                lastInfoPackages[getPositionKey(holder.adapterPosition)]?.let {
                    listener.onItemClick(
                        it.id_preparat,
                        getPositionKey(holder.adapterPosition)
                    )
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: LastInfoPackageHolder) {
        deleteListener(holder.itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastInfoPackageHolder {
        return LastInfoPackageHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_last_info_package, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LastInfoPackageHolder, position: Int) {
        val lastInfoPackage = lastInfoPackages[getPositionKey(position)]
        lastInfoPackage?.let { holder.bind(it) }
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