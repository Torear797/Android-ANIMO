package com.animo.ru.ui.currentVisits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Plan
import com.google.android.material.card.MaterialCardView

class PlansAdapter(
    private val visits: MutableList<Plan>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<PlansAdapter.PlanHolder>() {

    interface OnItemClickListener {
        fun onItemClick(clickPlan: Plan)
    }

    inner class PlanHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val dataText: TextView = itemView.findViewById(R.id.plan_data)
        val descriptionText: TextView = itemView.findViewById(R.id.plan_description)
        val fioText: TextView = itemView.findViewById(R.id.plan_fio)
        val plan: MaterialCardView = itemView.findViewById(R.id.plan)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if(adapterPosition != RecyclerView.NO_POSITION)
            listener.onItemClick(visits[adapterPosition])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanHolder {
        return PlanHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_plan, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlanHolder, position: Int) {
        val visit = visits[position]

        holder.dataText.text = visit.date
        holder.descriptionText.text = "Примечание: ${visit.note}"
        holder.fioText.text = visit.user

    }

    override fun getItemCount(): Int = visits.size
}