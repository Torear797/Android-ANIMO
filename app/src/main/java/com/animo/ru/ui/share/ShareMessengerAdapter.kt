package com.animo.ru.ui.share

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.Messenger

class ShareMessengerAdapter(
    private var shareMessengersList: MutableMap<Int, Messenger>,
    private val listener: OnMessengerClickListener
) : RecyclerView.Adapter<ShareMessengerAdapter.MessengerHolder>() {

    interface OnMessengerClickListener {
        fun onItemClick(messenger: Messenger)
    }

    inner class MessengerHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var image: ImageView = itemView.findViewById(R.id.msg_icon)
        var appName: TextView = itemView.findViewById(R.id.msg_name)

        init {
            image.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION) {
                shareMessengersList[getPositionKey(adapterPosition)]?.let { listener.onItemClick(it) }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessengerHolder {
        return MessengerHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_messenger, parent, false)
        )
    }

    override fun getItemCount(): Int = shareMessengersList.size

    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in shareMessengersList) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    override fun onBindViewHolder(holder: MessengerHolder, position: Int) {
        val messenger = shareMessengersList[getPositionKey(position)]
        holder.appName.text = messenger!!.appName
        holder.image.setImageDrawable(
            ContextCompat.getDrawable(
                holder.image.context,
                messenger.icon
            )
        )
        holder.image.setBackgroundResource(messenger.backgroundColor)
    }
}