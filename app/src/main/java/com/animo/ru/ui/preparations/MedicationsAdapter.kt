package com.animo.ru.ui.preparations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Medication

class MedicationsAdapter(
    private val preparations: MutableMap<Int, Medication>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<MedicationsAdapter.MedicationHolder>() {

    interface OnItemClickListener {
        fun onItemClick(medicationId: Int)
    }

    inner class MedicationHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var medicationName: TextView = itemView.findViewById(R.id.med_name)
        var medicationCount: TextView = itemView.findViewById(R.id.count)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION)
                preparations[getPositionKey(adapterPosition)]?.let { listener.onItemClick(getPositionKey(adapterPosition)) }
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
        holder.medicationName.text = medication!!.name
        holder.medicationCount.text = medication.cntInfPack.toString()
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