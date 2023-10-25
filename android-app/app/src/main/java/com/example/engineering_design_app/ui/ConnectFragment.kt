package com.example.engineering_design_app.ui

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.engineering_design_app.R
import com.example.engineering_design_app.model.AppDatabase
import com.example.engineering_design_app.model.Device
import com.example.engineering_design_app.model.DeviceViewModel
import java.util.*


class ConnectFragment : Fragment() {
    private var displayTextView: TextView? = null
    private var editText: EditText? = null
    private var sendBtn: Button? = null
    private var deviceViewModel: DeviceViewModel? = null
    private var channel: WifiP2pManager.Channel? = null
    private var manager: WifiP2pManager? = null
//    private val url = "http://192.168.187.46/"
    private val url = "http://192.168.2.10/"

    private var queue: RequestQueue? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect, container, false)
        displayTextView = view.findViewById(R.id.diplayTextView)
        editText = view.findViewById(R.id.editText)
        sendBtn = view.findViewById(R.id.sendBtn)
        displayTextView?.movementMethod = ScrollingMovementMethod()
        queue = Volley.newRequestQueue(requireActivity())
        sendBtn?.setOnClickListener {
            editText?.text?.clear()
            val editTextString = editText?.text.toString()
            getInfoArduino()
            editText?.text?.clear()
        }

        deviceViewModel = ViewModelProvider(requireActivity()).get()

        deviceViewModel!!.getPeers().observe(requireActivity()) {
            display(it.deviceList.toString())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceViewModel!!.getChannel().observe(requireActivity()) {
            channel = it
        }
        deviceViewModel!!.getManager().observe(requireActivity()) {
            manager = it
        }
    }

    private fun getInfoArduino() {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val dao = AppDatabase.getInstance(requireActivity()).deviceDao()
                val waterUsage = response.getJSONArray("water_usage")
                waterUsage.getJSONObject(0).put("time", Calendar.getInstance().time)
                val device = Device(response.getString("name"), response.getString("location"), waterUsage.toString())
                val oldDevice = dao.findByName(device.name)
                dao.deleteAll()
                if (oldDevice != null && deviceViewModel?.getSelectedDevice()?.value != null) {
                    deviceViewModel?.setData(oldDevice)
                    deviceViewModel?.setData(device)
                    deviceViewModel!!.getSelectedDevice().value?.let {
                        dao.updateWaterUsage(
                            it
                        )
                    }
                } else {
                    deviceViewModel?.setData(device)
                    deviceViewModel!!.getSelectedDevice().value?.let {
                        dao.insertAll(
                            it
                        )
                    }
                }

                val d = AppDatabase.getInstance(requireActivity()).deviceDao().findByName(device.name)
                display("Response: \n name: ${d.name}\n " +
                        "location: ${d.location} \n " +
                        "water usage: ${d.waterUsage}  ")
            },
            { error ->
                // Handle error
                display("ERROR: $error")
            })
// Add the request to the RequestQueue.
        queue?.add(jsonObjectRequest)
    }

    private fun display(message: String) {
        activity?.runOnUiThread { displayTextView!!.append(message) }
    }
}