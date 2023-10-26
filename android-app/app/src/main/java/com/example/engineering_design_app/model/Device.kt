package com.example.engineering_design_app.model

import java.util.*

class Device (
    var name: String = "device",

    var location : String? = null,

    var waterUsage : MutableList<Pair<Date, Long>>? = null,
) {
    var id:Int? = null

    override fun toString(): String {
        return "Name: $name\nLocation: $location"
    }
}