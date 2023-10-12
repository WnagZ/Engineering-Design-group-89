package com.example.engineering_design_app.ui

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.engineering_design_app.R
import com.example.engineering_design_app.arduino.ArduinoDiscoveryTask
import kotlinx.coroutines.launch
import me.aflak.arduino.Arduino


class HomeFragment : Fragment() {
    private var addDeviceButton: Button? = null

    private var connectFragment = ConnectFragment()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        lifecycleScope.launch {
            val result = ArduinoDiscoveryTask().discoverArduino()

        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addDeviceButton = view.findViewById(R.id.add_button)

        addDeviceButton?.setOnClickListener{
            val ft = fragmentManager?.beginTransaction()
            ft?.replace(R.id.container, connectFragment)
            ft?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft?.addToBackStack(null)
            ft?.commit()
        }
    }
}