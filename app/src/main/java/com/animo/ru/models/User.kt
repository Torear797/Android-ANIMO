package com.animo.ru.models

import com.animo.ru.App
import com.orhanobut.hawk.Hawk

data class User(
    var id: Int? = null,
    var first_name: String? = "",
    var surname: String? = "",
    var patronymic: String? = "",
    var phone: String? = "",
    var email: String? = "",
    var direction: Direction? = null,
    var role: List<Role>? = null,
    var regions: MutableMap<Int, String>? = null,
    var create_date: String? = "",
    var token: String? = "",
    var exp: String? = "",
    var refreshToken: String? = "",
    var refreshExp: String? = ""
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

    fun getRolesIds(): List<Int> {
        val arrayRoles: MutableList<Int> = mutableListOf()

        role?.forEach {
            it.id?.let { it1 -> arrayRoles.add(it1) }
        }

        return arrayRoles
    }

    fun getRolesArrayName(): List<String> {
        val arrayRoles: MutableList<String> = mutableListOf()

        role?.forEach {
            it.name?.let { it1 -> arrayRoles.add(it1) }
        }

        return arrayRoles
    }
}