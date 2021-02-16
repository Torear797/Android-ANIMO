package com.animo.ru.models

import android.graphics.Point

class DeviceInfo(
    var resolution: Point? = null,
    var deviceId: String? = null
) {
    fun getResolution(): String {
        return resolution?.x.toString() + "x" + resolution?.y.toString()
    }
}