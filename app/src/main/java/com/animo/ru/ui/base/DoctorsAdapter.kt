package com.animo.ru.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Doctor
import com.animo.ru.utilities.deleteListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import java.util.*

class DoctorsAdapter(
    private val doctors: TreeMap<Int, Doctor>,
    private val listener: OnItemClickListener
) : RecyclerSwipeAdapter<DoctorsAdapter.DoctorHolder>() {

    interface OnItemClickListener {
        fun onAttachDoctor(doctorId: Int, doctor: Doctor, position: Int)
        fun onAddToPlanDoctor(doctorId: Int)
        fun onEditDoctor(doctorId: Int)
    }

    inner class DoctorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fio: TextView = itemView.findViewById(R.id.docFio)
        private val docInfo: TextView = itemView.findViewById(R.id.docInfo)
        val docExtendedInfo: TextView = itemView.findViewById(R.id.docExtendedInfo)

        val arrow: ImageView = itemView.findViewById(R.id.arrow)

        private val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipeLayout)
        val attach: ImageView = itemView.findViewById(R.id.attach)
        val addToPlan: ImageView = itemView.findViewById(R.id.addToPlan)
        val edit: ImageView = itemView.findViewById(R.id.edit)

        fun bind(doctor: Doctor) {
            fio.text = doctor.fio
            docInfo.text =
                "Последний ОВ: ${doctor.ov}, Последний ДВ: ${doctor.dv}, Визитов: ${doctor.countVisits}, Специальность: ${doctor.specName}"
            docExtendedInfo.text = HtmlCompat.fromHtml(
                "<strong>Организация: </strong> ${doctor.organization}, " +
                        "<strong>Регион: </strong> ${doctor.regionName}, <strong>Город: </strong> ${doctor.city}, " +
                        "<strong>Название и адрес организации: </strong> ${doctor.lpu}, <strong>Должность: </strong> ${doctor.post}" +
                        ", <strong>Городской тел.: </strong> ${doctor.city_phone}, <strong>ОЛ: </strong> ${doctor.ol}" +
                        ", <strong>Сегментация IMS 2019: </strong> ${doctor.IMS}, <strong>Активен: </strong> ${doctor.isActive}" +
                        ", <strong>Заметки: </strong> ${doctor.note}, <strong>ДР: </strong> ${doctor.birthday}",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            if (doctor.isOpen) {
                docExtendedInfo.visibility = View.VISIBLE
                arrow.rotation = 90F
            } else {
                docExtendedInfo.visibility = View.GONE
                arrow.rotation = 0F
            }

            swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        }
    }

    override fun onViewAttachedToWindow(holder: DoctorHolder) {
        super.onViewAttachedToWindow(holder)

        holder.arrow.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                val doctor = doctors[getPositionKey(holder.adapterPosition)]

                if (doctor!!.isOpen) {
                    holder.docExtendedInfo.visibility = View.GONE
                    holder.arrow.animate().rotation(0F)
                    doctor.isOpen = false
                } else {
                    holder.docExtendedInfo.visibility = View.VISIBLE
                    holder.arrow.animate().rotation(90F)
                    doctor.isOpen = true
                }
            }
        }

        holder.attach.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                val id = getPositionKey(holder.adapterPosition)
                doctors[id]?.let { it1 -> listener.onAttachDoctor(id, it1, holder.adapterPosition) }
            }
        }

        holder.addToPlan.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                listener.onAddToPlanDoctor(getPositionKey(holder.adapterPosition))
            }
        }

        holder.edit.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                closeAllItems()
                listener.onEditDoctor(getPositionKey(holder.adapterPosition))
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: DoctorHolder) {
        super.onViewDetachedFromWindow(holder)
        deleteListener(holder.arrow)
        deleteListener(holder.attach)
        deleteListener(holder.addToPlan)
        deleteListener(holder.edit)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsAdapter.DoctorHolder {
        return DoctorHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doctor, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DoctorsAdapter.DoctorHolder, position: Int) {
        val doctor = doctors[getPositionKey(position)]
        doctor?.let { holder.bind(it) }
        mItemManger.bindView(holder.itemView, position)
    }

    override fun getItemCount(): Int = doctors.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in doctors) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    fun removeItem(id: Int, position: Int) {
        doctors.remove(id)
        notifyItemRemoved(position)
    }

    fun update(modelList: TreeMap<Int, Doctor>) {
        doctors.clear()
        notifyDataSetChanged()
        doctors.putAll(modelList)
        notifyItemRangeInserted(0, modelList.size)
    }

    fun insertItems(position: Int, modelList: TreeMap<Int, Doctor>) {
        doctors.putAll(modelList)
        notifyItemRangeInserted(position, modelList.size)
    }
}