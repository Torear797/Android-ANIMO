package com.animo.ru.models

import com.animo.ru.App
import com.orhanobut.hawk.Hawk

data class User(
    var id: Int? = null,
    var first_name: String? = null,
    var surname: String? = null,
    var patronymic: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var direction: Direction? = null,
    var role: List<Role>? = null,
    var regions: MutableMap<Int, String>? = null,
    var create_date: String? = null,
    var token: String? = null,
    var exp: String? = null,
    var refreshToken: String? = null,
    var refreshExp: String? = null
) {
    fun getInitials(): String =
        (first_name?.get(0) ?: "0").toString() + (surname?.get(0) ?: "0").toString()

    fun getRegionString(): String {
        var regionsString = ""
        regions?.forEach {
            if (regionsString.isNotEmpty()) regionsString += ", "
            regionsString += it.value
        }
        return regionsString
    }

    fun getRoleString(): String {
        var resault = "";

        val iterator = role?.toList()?.iterator()

        iterator?.forEach { role ->

            if (resault.isNotEmpty()) resault += ", "
            resault += role.description
        }

        return resault
    }

    fun setNewTokens(token: String?, exp: String?, refreshToken: String?, refreshExp: String?) {
        this.token = token
        this.exp = exp
        this.refreshToken = refreshToken
        this.refreshExp = refreshExp

        Hawk.put("user", App.user)
    }
}