package com.example.engineering_design_app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "device")
class Device (
    @ColumnInfo(name = "name")
    var name: String? = "device",

    @ColumnInfo(name="location")
    var location : String? = null,

    @ColumnInfo(name="water_usage")
    var waterUsage : List<Int>? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}