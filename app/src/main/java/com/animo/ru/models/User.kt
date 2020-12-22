package com.animo.ru.models

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

    fun getRoleString() :String{
        var resault = "";

        val iterator = role?.toList()?.iterator()

        iterator?.forEach { role ->

            if (resault.isNotEmpty()) resault += ", "
            resault += role.description
        }

        return resault;
    }
}