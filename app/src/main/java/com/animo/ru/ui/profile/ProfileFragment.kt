package com.animo.ru.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.animo.ru.App
import com.animo.ru.R
import com.animo.ru.ui.authorization.LoginActivity
import com.orhanobut.hawk.Hawk


class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        val avatarText: TextView = view.findViewById(R.id.avatar_textView)
        val fio: TextView = view.findViewById(R.id.tv_fio)
        val regions: TextView = view.findViewById(R.id.tv_regions)
        val directionName: TextView = view.findViewById(R.id.line_name)
        val roleName: TextView = view.findViewById(R.id.roleName)
        val phone: TextView = view.findViewById(R.id.phone)
        val email: TextView = view.findViewById(R.id.email)
        val createDate: TextView = view.findViewById(R.id.create_date)
        val logoutBtn: LinearLayout = view.findViewById(R.id.logout)

        avatarText.text = App.user.getInitials()
        fio.text = """${App.user.first_name} ${App.user.surname}"""
        regions.text = App.user.getRegionString()
        directionName.text = App.user.direction?.name ?: ""
        roleName.text = App.user.getRoleString()
        phone.text = App.user.phone
        email.text = App.user.email
        createDate.text = App.user.create_date

        logoutBtn.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        val login: String = Hawk.get("login")
        Hawk.deleteAll()
        Hawk.put("login", login)

        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
    }

}