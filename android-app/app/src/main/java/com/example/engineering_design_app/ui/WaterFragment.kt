package com.example.engineering_design_app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.engineering_design_app.R
import com.example.engineering_design_app.model.DeviceViewModel
import com.github.mikephil.charting.charts.LineChart


class WaterFragment : Fragment() {

    private var chart: LineChart? = null
    private var deviceViewModel: DeviceViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chart = view?.findViewById(R.id.chart1)

        return inflater.inflate(R.layout.fragment_water, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // background color
        chart!!.setBackgroundColor(Color.WHITE)

        // disable description text
        chart!!.description.isEnabled = false

        // enable touch gestures
        chart!!.setTouchEnabled(true)

        // set listeners
        chart!!.setDrawGridBackground(false)

    }
    fun setData() {

    }
}