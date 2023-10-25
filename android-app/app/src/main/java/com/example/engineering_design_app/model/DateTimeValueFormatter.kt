package com.example.engineering_design_app.model

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat

class DateTimeValueFormatter : IndexAxisValueFormatter() {
    private val dateFormat = SimpleDateFormat("MMM dd, HH:mm")

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val date = value.toInt()
        return dateFormat.format(date)
    }
}