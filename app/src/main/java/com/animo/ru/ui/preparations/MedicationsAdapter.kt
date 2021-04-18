package com.animo.ru.ui.preparations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.LastInfoPackage
import com.animo.ru.models.Medication
import com.animo.ru.utilities.deleteListener

class MedicationsAdapter(
    private val preparations: MutableMap<Int, Medication>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<MedicationsAdapter.MedicationHolder>() {

    interface OnItemClickListener {
        fun onItemClick(medicationId: Int)
    }

    inner class MedicationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicationName: TextView = itemView.findViewById(R.id.med_name)
        private val medicationCount: TextView = itemView.findViewById(R.id.count)

        fun bind(medication: Medication) {
            medicationName.text = medication.name
            medicationCount.text = medication.cntInfPack.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationHolder {
        return MedicationHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_medication, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MedicationHolder, position: Int) {
        val medication = preparations[getPositionKey(position)]
        medication?.let { holder.bind(it) }
    }

    override fun onViewAttachedToWindow(holder: MedicationHolder) {
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                preparations[getPositionKey(holder.adapterPosition)]?.let {
                    listener.onItemClick(
                        getPositionKey(holder.adapterPosition)
                    )
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: MedicationHolder) {
        deleteListener(holder.itemView)
    }

    override fun getItemCount(): Int = preparations.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in preparations) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }
}