package com.animo.ru.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Pharmacy
import com.animo.ru.utilities.deleteListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import java.util.*

class PharmacyAdapter(
    private val pharmacyList: TreeMap<Int, Pharmacy>,
    private val listener: OnItemClickListener
) : RecyclerSwipeAdapter<PharmacyAdapter.PharmacyHolder>() {

    interface OnItemClickListener {
        fun onAttachPharmacy(pharmacy: Pharmacy, position: Int)
        fun onAddToPlanPharmacy(pharmacyId: Int)
        fun onEditPharmacy(pharmacyId: Int)
    }

    inner class PharmacyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fio: TextView = itemView.findViewById(R.id.pharmFio)
        private val pharmInfo: TextView = itemView.findViewById(R.id.pharmInfo)
        val pharmExtendedInfo: TextView = itemView.findViewById(R.id.pharmExtendedInfo)

        val arrow: ImageView = itemView.findViewById(R.id.arrow)

        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipeLayout)
        val attach: ImageView = itemView.findViewById(R.id.attach)
        val addToPlan: ImageView = itemView.findViewById(R.id.addToPlan)
        val edit: ImageView = itemView.findViewById(R.id.edit)

        fun bind(pharmacy: Pharmacy) {
            fio.text = "${pharmacy.name} ${pharmacy.fio} ${pharmacy.post}"
            pharmInfo.text =
                "Последний визит: ${pharmacy.lastVisit}, Визитов: ${pharmacy.countVisits}"
            pharmExtendedInfo.text = HtmlCompat.fromHtml(
                "<strong>Сетевая: </strong> ${pharmacy.isNetworking}, <strong>Договор: </strong> ${pharmacy.regionName}," +
                        "<strong>Регион: </strong> ${pharmacy.regionName}, <strong>Город: </strong> ${pharmacy.city}, " +
                        "<strong>Адрес: </strong> ${pharmacy.address}, <strong>Email: </strong> ${pharmacy.email}" +
                        ", <strong>Телефон: </strong> ${pharmacy.phone}, <strong>Активная: </strong> ${pharmacy.isActive}" +
                        ", <strong>Категория: </strong> ${pharmacy.category}" +
                        ", <strong>Заметки: </strong> ${pharmacy.note}",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            if (pharmacy.isOpen) {
                pharmExtendedInfo.visibility = View.VISIBLE
                arrow.rotation = 90F
            } else {
                pharmExtendedInfo.visibility = View.GONE
                arrow.rotation = 0F
            }

            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        }
    }

    override fun onViewAttachedToWindow(holder: PharmacyAdapter.PharmacyHolder) {
        super.onViewAttachedToWindow(holder)

        holder.arrow.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val pharmacy = pharmacyList[getPositionKey(holder.adapterPosition)]

                if (pharmacy!!.isOpen) {
                    holder.pharmExtendedInfo.visibility = View.GONE
                    holder.arrow.animate().rotation(0F)
                    pharmacy.isOpen = false
                } else {
                    holder.pharmExtendedInfo.visibility = View.VISIBLE
                    holder.arrow.animate().rotation(90F)
                    pharmacy.isOpen = true
                }
            }
        }

        holder.attach.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                val id = getPositionKey(holder.adapterPosition)
                pharmacyList[id]?.let { it1 ->
                    listener.onAttachPharmacy(
                        it1,
                        holder.adapterPosition
                    )
                }
            }
        }

        holder.addToPlan.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                listener.onAddToPlanPharmacy(getPositionKey(holder.adapterPosition))
            }
        }

        holder.edit.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                listener.onEditPharmacy(getPositionKey(holder.adapterPosition))
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PharmacyAdapter.PharmacyHolder) {
        super.onViewDetachedFromWindow(holder)
        deleteListener(holder.arrow)
        deleteListener(holder.attach)
        deleteListener(holder.addToPlan)
        deleteListener(holder.edit)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PharmacyAdapter.PharmacyHolder {
        return PharmacyHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pharmacy, parent, false)
        )
    }

    override fun onBindViewHolder(p0: PharmacyAdapter.PharmacyHolder, p1: Int) {
        val pharmacy = pharmacyList[getPositionKey(p1)]
        pharmacy?.let { p0.bind(it) }
        mItemManger.bindView(p0.itemView, p1)
    }

    override fun getItemCount(): Int = pharmacyList.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in pharmacyList) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    fun removeItem(id: Int, position: Int) {
        pharmacyList.remove(id)
        notifyItemRemoved(position)
    }

    fun update(modelList: TreeMap<Int, Pharmacy>) {
        pharmacyList.clear()
        notifyDataSetChanged()
        pharmacyList.putAll(modelList)
        notifyItemRangeInserted(0, modelList.size)
    }

    fun insertItems(position: Int, modelList: TreeMap<Int, Pharmacy>) {
        pharmacyList.putAll(modelList)
        notifyItemRangeInserted(position, modelList.size)
    }

}