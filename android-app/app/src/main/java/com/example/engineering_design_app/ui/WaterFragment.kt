package com.example.engineering_design_app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.engineering_design_app.R
import com.example.engineering_design_app.model.DateTimeValueFormatter
import com.example.engineering_design_app.model.Device
import com.example.engineering_design_app.model.DeviceViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class WaterFragment : Fragment() {

    private var chart: LineChart? = null
    private var deviceViewModel: DeviceViewModel? = null
    private var currentDevice: Device? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        deviceViewModel = ViewModelProvider(requireActivity()).get()
        return inflater.inflate(R.layout.fragment_water, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chart = view.findViewById(R.id.chart1)

        // background color
        chart!!.setBackgroundColor(Color.WHITE)

        // disable description text
        chart!!.description.isEnabled = false

        // enable touch gestures
        chart!!.setTouchEnabled(true)

        // set listeners
        chart!!.setDrawGridBackground(false)
        val deviceObserver = Observer<Device> { device ->
            // Update the UI, in this case, a TextView.
            currentDevice = device
            transform()
        }
        deviceViewModel?.getSelectedDevice()?.observe(requireActivity(), deviceObserver)
    }


    fun transform() {
        val entries = deviceViewModel?.getSelectedDevice()?.value?.waterUsage?.mapIndexed { index, (dateTime, value) ->
            Entry(dateTime.time.toInt().toFloat(), value.toFloat())

        }
        val lineDataSet = LineDataSet(entries, "Water Usage")
        val lineDataSets: ArrayList<ILineDataSet> = ArrayList()
        lineDataSets.add(lineDataSet)
        val lineData = LineData(lineDataSets)
        chart!!.data = lineData
        // Format X-axis labels (replace with your date format)
        chart!!.xAxis.valueFormatter = DateTimeValueFormatter()
        // Customize the appearance of the chart (e.g., colors, labels, etc.)
        lineDataSet.color = R.color.purple_500
        lineDataSet.setDrawValues(true)

        // Refresh the chart
        chart!!.invalidate()
    }
}