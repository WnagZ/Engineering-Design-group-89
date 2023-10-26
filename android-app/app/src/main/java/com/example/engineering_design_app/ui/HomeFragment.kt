package com.example.engineering_design_app.ui

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.engineering_design_app.R
import com.example.engineering_design_app.model.Device
import com.example.engineering_design_app.model.DeviceViewModel
import com.google.android.material.snackbar.Snackbar
import me.aflak.arduino.Arduino


class HomeFragment : Fragment() {
    private var addDeviceButton: Button? = null
    private var deviceViewModel: DeviceViewModel? = null
    private var deviceListView: ListView? = null
    private var connectFragment = ConnectFragment()
    private var devices: List<Device> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        deviceViewModel = ViewModelProvider(requireActivity()).get()

        return inflater.inflate(R.layout.fragment_home, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDeviceButton = view.findViewById(R.id.add_button)
        addDeviceButton?.setOnClickListener {
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.container, connectFragment)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(null)
            ft.commit()
        }

        deviceListView = view.findViewById(R.id.list_view)

        val deviceObserver = Observer<Device> { device ->
            devices = if(device != null) {
                listOf(device)
            } else {
                listOf()
            }
        }
        deviceViewModel?.getSelectedDevice()?.observe(requireActivity(), deviceObserver)
        val arrayAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, devices)
        deviceListView?.adapter = arrayAdapter
    }
}