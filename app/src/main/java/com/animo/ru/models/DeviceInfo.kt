package com.animo.ru.models

import android.graphics.Point

class DeviceInfo(
    val resolution: Point? = null,
    val deviceId: String? = null
) {
    fun getResolution(): String {
        return resolution?.x.toString() + "x" + resolution?.y.toString()
    }
}