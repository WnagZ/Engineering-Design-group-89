package com.example.engineering_design_app.ui

import android.hardware.usb.UsbDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
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
import com.example.engineering_design_app.R
import com.example.engineering_design_app.model.DeviceViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.channelFlow
import me.aflak.arduino.Arduino
import me.aflak.arduino.ArduinoListener


class ConnectFragment : Fragment(), ArduinoListener {
    private var arduino: Arduino? = null
    private var displayTextView: TextView? = null
    private var editText: EditText? = null
    private var sendBtn: Button? = null
    private var deviceViewModel: DeviceViewModel? = null
    private var channel: WifiP2pManager.Channel? = null
    private var manager: WifiP2pManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                          savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_connect, container, false)
        displayTextView = view.findViewById(R.id.diplayTextView)
        editText = view.findViewById(R.id.editText)
        sendBtn = view.findViewById(R.id.sendBtn)
        displayTextView?.movementMethod = ScrollingMovementMethod()
        sendBtn?.setOnClickListener {
            editText?.text?.clear()
            val editTextString = editText?.text.toString()
            arduino!!.send(editTextString.toByteArray())
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), "Discover Peers Success!", Snackbar.LENGTH_LONG).show()
                }

                override fun onFailure(reasonCode: Int) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        "Discover Peers Failed Code: $reasonCode", Snackbar.LENGTH_LONG).show()
                }
            })
            editText?.text?.clear()
        }
        arduino = Arduino(activity)

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

    override fun onStart() {
        super.onStart()
        arduino!!.setArduinoListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        arduino!!.unsetArduinoListener()
        arduino!!.close()
    }

    override fun onArduinoAttached(device: UsbDevice) {
        display("arduino attached...")
        arduino!!.open(device)
    }

    override fun onArduinoDetached() {
        display("arduino detached.")
    }

    override fun onArduinoMessage(bytes: ByteArray) {
        display(String())
    }

    override fun onArduinoOpened() {
        val str = "arduino opened..."
        arduino!!.send(str.toByteArray())
    }

    override fun onUsbPermissionDenied() {
        display("Permission denied. Attempting again in 3 sec...")
        Handler().postDelayed({ arduino!!.reopen() }, 3000)
    }

    private fun display(message: String) {
        activity?.runOnUiThread { displayTextView!!.append(message) }
    }
}