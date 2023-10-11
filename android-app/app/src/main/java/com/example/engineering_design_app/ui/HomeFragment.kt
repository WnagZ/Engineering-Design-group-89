package com.example.engineering_design_app.ui

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.engineering_design_app.R
import com.example.engineering_design_app.model.DeviceViewModel
import com.google.android.material.snackbar.Snackbar
import me.aflak.arduino.Arduino


class HomeFragment : Fragment() {
    private var addDeviceButton: Button? = null
    private var deviceViewModel: DeviceViewModel? = null
    private var connectFragment = ConnectFragment()
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
            val ft = fragmentManager?.beginTransaction()
            ft?.replace(R.id.container, connectFragment)
            ft?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft?.addToBackStack(null)
            ft?.commit()
    }
}