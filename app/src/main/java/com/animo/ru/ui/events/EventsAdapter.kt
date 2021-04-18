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
import com.animo.ru.utilities.deleteListener

class EventsAdapter(
    private var Events: MutableMap<Int, Event>,
    private val listener: OnEventsClickListener
) : RecyclerView.Adapter<EventsAdapter.EventsHolder>() {

    interface OnEventsClickListener {
        fun onEventClick(event: Event, id: Int)
    }

    inner class EventsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val infoPackageName: TextView = itemView.findViewById(R.id.ip_name)
        private val infoPackageText: TextView = itemView.findViewById(R.id.ip_text)
        private val infoPackageType: TextView = itemView.findViewById(R.id.ip_type_package)
        private val infoPackageMed: TextView = itemView.findViewById(R.id.ip_med)

        val btnShare: ImageButton = itemView.findViewById(R.id.ip_btn_share)
        val btnArrow: ImageView = itemView.findViewById(R.id.ip_arrow)
        val myGroup: Group = itemView.findViewById(R.id.MyGroup)

        fun bind(event: Event) {
            infoPackageName.text = event.name

            event.text = event.text.replace(
                "[Имя ваше]",
                App.user.first_name!!,
                true
            )

            infoPackageText.text =
                HtmlCompat.fromHtml(
                    "<strong>Текст:</strong> " + event.text,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

            infoPackageType.text =
                HtmlCompat.fromHtml(
                    "<strong>Тип:</strong> " + event.oa_name,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

            infoPackageMed.text = HtmlCompat.fromHtml(
                "<strong>Препараты:</strong> " + event.preparations,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapter.EventsHolder {
        return EventsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
        )
    }

    override fun onViewAttachedToWindow(holder: EventsHolder) {
        holder.btnShare.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                Events[getPositionKey(holder.adapterPosition)]?.let {
                    listener.onEventClick(
                        it,
                        getPositionKey(holder.adapterPosition)
                    )
                }
            }
        }

        holder.btnArrow.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                if (holder.myGroup.visibility == View.VISIBLE) {
                    holder.myGroup.visibility = View.GONE
                    holder.btnArrow.animate().rotation(0F)
                } else {
                    holder.myGroup.visibility = View.VISIBLE
                    holder.btnArrow.animate().rotation(-180F)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: EventsHolder) {
        deleteListener(holder.btnShare)
        deleteListener(holder.btnArrow)
    }

    override fun onBindViewHolder(holder: EventsAdapter.EventsHolder, position: Int) {
        val event = Events[getPositionKey(position)]
        event?.let { holder.bind(it) }
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