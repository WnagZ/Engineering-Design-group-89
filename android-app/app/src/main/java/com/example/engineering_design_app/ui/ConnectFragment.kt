package com.example.engineering_design_app.ui

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.CountDownTimer
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
import com.example.engineering_design_app.model.Device
import com.example.engineering_design_app.model.DeviceViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


class ConnectFragment : Fragment() {
    private var displayTextView: TextView? = null
    private var editText: EditText? = null
    private var sendBtn: Button? = null
    private var cancelBtn: Button? = null

    private var deviceViewModel: DeviceViewModel? = null
    private var channel: WifiP2pManager.Channel? = null
    private var manager: WifiP2pManager? = null
    private val url = "http://192.168.187.46/"
//    private val url = "http://192.168.1.30"
    private var reoccurrence = false
    private var future: ScheduledFuture<*>? = null
    private var executor: ScheduledExecutorService? = null


    private var queue: RequestQueue? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect, container, false)
        displayTextView = view.findViewById(R.id.diplayTextView)
//        editText = view.findViewById(R.id.editText)
        sendBtn = view.findViewById(R.id.sendBtn)
        cancelBtn = view.findViewById(R.id.cancelBtn)
        displayTextView?.movementMethod = ScrollingMovementMethod()
        queue = Volley.newRequestQueue(requireActivity())
        sendBtn?.setOnClickListener {
//            editText?.text?.clear()
//            val editTextString = editText?.text.toString()
            getInfoArduino()
//            editText?.text?.clear()
        }
        cancelBtn?.setOnClickListener {
            cancel()
        }

        deviceViewModel = ViewModelProvider(requireActivity()).get()

//        deviceViewModel!!.getPeers().observe(requireActivity()) {
//            display(it.deviceList.toString())
//        }
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
                val temp_list = mutableListOf((Calendar.getInstance().time to response.getLong("water_usage")))
                val device = Device(response.getString("name"), response.getString("location"),
                temp_list)
                deviceViewModel!!.setData(device)
                val d = deviceViewModel!!.getSelectedDevice()
                display("Response: \n name: ${d.value?.name}\n " +
                        "location: ${d.value?.location} \n " +
                        "water usage: ${d.value?.waterUsage}  ")
                countdown()
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

    private fun connected() {
        Snackbar.make(requireActivity().findViewById(android.R.id.content), "Connected", Snackbar.LENGTH_LONG).show()
//        val homeFragment = HomeFragment()
//        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
    }

    private fun countdown() {
        if(!reoccurrence) {
            executor = Executors.newSingleThreadScheduledExecutor()

            val task = Runnable {
                // Your code to be executed every 10 seconds goes here
                getInfoArduino()
            }
            // Schedule the task to run every 10 seconds
            future = executor?.scheduleAtFixedRate(task, 0, 10, TimeUnit.SECONDS)
            reoccurrence = true
            connected()
        }
    }

    private fun cancel() {
        future?.cancel(true)
        executor?.shutdown()
        reoccurrence = false
        Snackbar.make(requireActivity().findViewById(android.R.id.content), "Disconnected", Snackbar.LENGTH_LONG).show()
    }
}