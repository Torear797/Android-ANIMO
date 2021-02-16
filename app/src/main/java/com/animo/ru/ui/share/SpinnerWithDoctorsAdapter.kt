package com.animo.ru.ui.share

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.animo.ru.R
import com.animo.ru.models.answers.ShareDoctor


class SpinnerWithDoctorsAdapter(ctx: Context, doctors: ArrayList<ShareDoctor>) :
    ArrayAdapter<ShareDoctor>(ctx, 0, doctors) {
    override fun getItem(position: Int): ShareDoctor? {
        return super.getItem(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val doctor = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner_doctor,
            parent,
            false
        )

        val fio:TextView = view.findViewById(R.id.fio)

        val copy:ImageView = view.findViewById(R.id.ic_copy)
        val email:ImageView = view.findViewById(R.id.ic_email)
        val viber:ImageView = view.findViewById(R.id.ic_viber)
        val whatsApp:ImageView = view.findViewById(R.id.ic_whatsapp)
        val telegram:ImageView = view.findViewById(R.id.ic_telegram)

        copy.visibility = View.GONE
        email.visibility = View.GONE
        viber.visibility = View.GONE
        whatsApp.visibility = View.GONE
        telegram.visibility = View.GONE

        doctor?.let {
            fio.text = doctor.fio

            doctor.sent?.forEach { messenger ->
                when(messenger){
                    "copy" -> copy.visibility = View.VISIBLE
                    "Email" -> email.visibility = View.VISIBLE
                    "Viber" -> viber.visibility = View.VISIBLE
                    "WhatsApp" -> whatsApp.visibility = View.VISIBLE
                    "Telegram" -> telegram.visibility = View.VISIBLE
                }
            }
        }
        return view
    }
}