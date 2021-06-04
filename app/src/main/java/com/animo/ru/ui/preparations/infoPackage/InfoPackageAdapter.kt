package com.animo.ru.ui.preparations.infoPackage

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
import com.animo.ru.models.InfoPackage
import com.animo.ru.utilities.deleteListener

class InfoPackageAdapter(
    private var infoPackages: MutableMap<Int, InfoPackage>,
    private val listener: OnInfoPackageClickListener
) : RecyclerView.Adapter<InfoPackageAdapter.InfoPackageHolder>() {

    interface OnInfoPackageClickListener {
        fun onItemClick(infoPackage: InfoPackage, id: Int)
    }

    inner class InfoPackageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val infoPackageName: TextView = itemView.findViewById(R.id.ip_name)
        private val infoPackageText: TextView = itemView.findViewById(R.id.ip_text)
        private val infoPackageType: TextView = itemView.findViewById(R.id.ip_type_package)
        private val infoPackageSpec: TextView = itemView.findViewById(R.id.ip_spec)
        private val infoPackageMed: TextView = itemView.findViewById(R.id.ip_med)
        private val infoPackageDesc: TextView = itemView.findViewById(R.id.ip_desc)

        val btnShare: ImageButton = itemView.findViewById(R.id.ip_btn_share)
        val btnArrow: ImageView = itemView.findViewById(R.id.ip_arrow)
        val myGroup: Group = itemView.findViewById(R.id.MyGroup)

        fun bind(infoPackage: InfoPackage) {
            infoPackageName.text = infoPackage.name

            infoPackage.share_description = infoPackage.share_description.replace(
                "[Имя ваше]",
                App.user.first_name!!,
                true
            )

            infoPackageText.text =
                HtmlCompat.fromHtml(
                    "<strong>Текст:</strong> " + infoPackage.share_description,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

            infoPackageType.text =
                HtmlCompat.fromHtml(
                    "<strong>Тип пакета:</strong> " + infoPackage.oa_name,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

            infoPackageSpec.text =
                HtmlCompat.fromHtml(
                    "<strong>Информационный пакет рекомендован для специальностей:</strong> " + infoPackage.specialty,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

            infoPackageMed.text = HtmlCompat.fromHtml(
                "<strong>Информационный пакет рекомендован для препаратов:</strong> " + infoPackage.preparats,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )


            val description = infoPackage.description
            infoPackageDesc.text = HtmlCompat.fromHtml(
                "<strong>Примечание:</strong> " + if (description != null && description.isNotEmpty()) description else "Пусто",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    override fun onViewAttachedToWindow(holder: InfoPackageHolder) {
        holder.btnShare.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                infoPackages[getPositionKey(holder.adapterPosition)]?.let {
                    listener.onItemClick(
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
                    holder.btnArrow.animate().rotation(90F)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: InfoPackageHolder) {
        deleteListener(holder.btnShare)
        deleteListener(holder.btnArrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoPackageHolder {
        return InfoPackageHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_info_package, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InfoPackageHolder, position: Int) {
        val infoPackage = infoPackages[getPositionKey(position)]
        infoPackage?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = infoPackages.size


    private fun getPositionKey(position: Int): Int {
        var curIndex = 0
        for ((key, _) in infoPackages) {
            if (curIndex == position) return key
            curIndex++
        }

        return 0
    }

    fun getPositionForId(id: Int): Int {
        var curIndex = 0

        for ((key, _) in infoPackages) {
            if (id == key) break
            curIndex++
        }

        return curIndex
    }

    fun update(modelList: MutableMap<Int, InfoPackage>) {
        infoPackages = modelList
        notifyDataSetChanged()
    }
}