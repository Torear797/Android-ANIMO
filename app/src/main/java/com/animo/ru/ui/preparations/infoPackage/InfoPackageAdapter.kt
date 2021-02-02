package com.animo.ru.ui.preparations.infoPackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.animo.ru.R
import com.animo.ru.models.InfoPackage


class InfoPackageAdapter(
    private var infoPackages: MutableMap<Int, InfoPackage>,
    private val listener: OnInfoPackageClickListener
) : RecyclerView.Adapter<InfoPackageAdapter.InfoPackageHolder>() {

    interface OnInfoPackageClickListener {
        fun onItemClick(infoPackage: InfoPackage)
    }

    inner class InfoPackageHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var infoPackageName: TextView = itemView.findViewById(R.id.ip_name)
        var infoPackageText: TextView = itemView.findViewById(R.id.ip_text)
        var infoPackageType: TextView = itemView.findViewById(R.id.ip_type_package)
        var infoPackageSpec: TextView = itemView.findViewById(R.id.ip_spec)
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
                infoPackages[getPositionKey(adapterPosition)]?.let { listener.onItemClick(it) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoPackageHolder {
        return InfoPackageHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_info_package, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InfoPackageHolder, position: Int) {
        val infoPackage = infoPackages[getPositionKey(position)]
        holder.infoPackageName.text = infoPackage!!.name

        holder.infoPackageText.text =
            HtmlCompat.fromHtml(
                "<strong>Текст:</strong> " + infoPackage.share_description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        holder.infoPackageType.text =
            HtmlCompat.fromHtml(
                "<strong>Тип пакета:</strong> " + infoPackage.oa_name,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        holder.infoPackageSpec.text =
            HtmlCompat.fromHtml(
                "<strong>Информационный пакет рекомендован для специальностей:</strong> " + infoPackage.specialty,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        holder.infoPackageMed.text = HtmlCompat.fromHtml(
            "<strong>Информационный пакет рекомендован для препаратов:</strong> " + infoPackage.preparats,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
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