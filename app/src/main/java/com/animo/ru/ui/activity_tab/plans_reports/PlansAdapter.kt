package com.animo.ru.ui.activity_tab.plans_reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Plan
import com.animo.ru.utilities.deleteListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter

class PlansAdapter(
    private val plans: MutableMap<Int, Plan>,
    private val listener: OnPlansClickListener
) : RecyclerSwipeAdapter<PlansAdapter.PlansHolder>() {

    interface OnPlansClickListener {
        fun onDeletePlan(plan: Plan, id: Int, position: Int)
        fun onSendPlan(plan: Plan, id: Int)
    }

    inner class PlansHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val plan_description: TextView = itemView.findViewById(R.id.plan_description)
        private val plan_data: TextView = itemView.findViewById(R.id.plan_data)
        private val plan_weak_name: TextView = itemView.findViewById(R.id.plan_weak_name)
        private val plan_fio: TextView = itemView.findViewById(R.id.plan_fio)

        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipeLayout)

        val deleteBtn: ImageView = itemView.findViewById(R.id.delete)
        val sendBtn: ImageView = itemView.findViewById(R.id.send)

        fun bind(plan: Plan) {
            plan_fio.text = "Пользователь: ${plan.user}"
            plan_description.text = "Примечание: ${plan.note}"
            plan_data.text = plan.date
            plan_weak_name.text = plan.dateWeek

            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        }
    }

    override fun onViewAttachedToWindow(holder: PlansHolder) {
        super.onViewAttachedToWindow(holder)
        holder.deleteBtn.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                plans[getPositionKey(holder.adapterPosition)]?.let {
                    listener.onDeletePlan(
                        it,
                        getPositionKey(holder.adapterPosition),
                        holder.adapterPosition
                    )
                }
            }
        }
        holder.sendBtn.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                plans[getPositionKey(holder.adapterPosition)]?.let {
                    listener.onSendPlan(
                        it,
                        getPositionKey(holder.adapterPosition)
                    )
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PlansHolder) {
        super.onViewDetachedFromWindow(holder)
        deleteListener(holder.deleteBtn)
        deleteListener(holder.sendBtn)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlansHolder {
        return PlansHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_plan, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlansHolder, position: Int) {
        val plan = plans[getPositionKey(position)]
        plan?.let { holder.bind(it) }
        mItemManger.bindView(holder.itemView, holder.adapterPosition)
    }

    override fun getItemCount(): Int = plans.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in plans) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    fun update(modelList: Map<Int, Plan>) {
        plans.clear()
        plans.putAll(modelList)
        notifyDataSetChanged()
    }

    fun removeItem(id: Int, position: Int) {
        plans.remove(id)
        notifyItemRemoved(position)
    }

    fun addList(newList: MutableMap<Int, Plan>) {
//        plans.addAll(newList)
//        notifyDataSetChanged()
    }

    fun clear() {
        plans.clear()
        notifyDataSetChanged()
    }
}