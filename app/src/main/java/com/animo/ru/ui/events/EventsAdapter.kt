package com.animo.ru.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.models.Event

class EventsAdapter(
    private var Events: MutableMap<Int, Event>,
    private val listener: EventsAdapter.OnEventsClickListener
) : RecyclerView.Adapter<EventsAdapter.EventsHolder>() {

    interface OnEventsClickListener {
        fun onEventClick(event: Event, id: Int)
    }

    inner class EventsHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var infoPackageName: TextView = itemView.findViewById(R.id.ip_name)
        var infoPackageText: TextView = itemView.findViewById(R.id.ip_text)
        var infoPackageType: TextView = itemView.findViewById(R.id.ip_type_package)
        var infoPackageMed: TextView = itemView.findViewById(R.id.ip_med)

        var btnShare: ImageButton = itemView.findViewById(R.id.ip_btn_share)
        var btnArrow: ImageView = itemView.findViewById(R.id.ip_arrow)
        var myGroup: Group = itemView.findViewById(R.id.MyGroup)

        init {
            btnShare.setOnClickListener(this)


            btnArrow.setOnClickListener {
                if (myGroup.visibility == View.VISIBLE) {
                    myGroup.visibility = View.GONE
                    btnArrow.animate().rotation(360F)
                } else {
                    myGroup.visibility = View.VISIBLE
                    btnArrow.animate().rotation(180F)
                }
            }
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Events[getPositionKey(adapterPosition)]?.let {
                    listener.onEventClick(
                        it,
                        getPositionKey(adapterPosition)
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapter.EventsHolder {
        return EventsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventsAdapter.EventsHolder, position: Int) {
        val event = Events[getPositionKey(position)]

        holder.infoPackageName.text = event!!.name

        event.text = event.text.replace(
            "[Имя ваше]",
            App.user.first_name!!,
            true
        )

        holder.infoPackageText.text =
            HtmlCompat.fromHtml(
                "<strong>Текст:</strong> " + event.text,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        holder.infoPackageType.text =
            HtmlCompat.fromHtml(
                "<strong>Тип:</strong> " + event.oa_name,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        holder.infoPackageMed.text = HtmlCompat.fromHtml(
            "<strong>Препараты:</strong> " + event.preparations,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    override fun getItemCount(): Int = Events.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in Events) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    fun update(modelList: MutableMap<Int, Event>) {
        Events = modelList
        notifyDataSetChanged()
    }

}