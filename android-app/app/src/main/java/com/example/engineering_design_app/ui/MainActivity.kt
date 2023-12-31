package com.example.engineering_design_app.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.engineering_design_app.R
import com.example.engineering_design_app.arduino.WiFiDirectBroadcastReceiver
import com.example.engineering_design_app.model.Device
import com.example.engineering_design_app.model.DeviceViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class MainActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private var homeFragment = HomeFragment()
    private var waterFragment = WaterFragment()
    private var profileFragment = ProfileFragment()
//    private var deviceNameList: Array<Device>? = null
//    private var deviceArray: Array<WifiP2pDevice>? = null
    private var deviceViewModel: DeviceViewModel? = null

//    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
//        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
//    }

//    private var channel: WifiP2pManager.Channel? = null
//    private var receiver: BroadcastReceiver? = null
//
//    private val intentFilter = IntentFilter().apply {
//        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
//        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
//        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
//        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
        supportActionBar?.hide()
        deviceViewModel = ViewModelProvider(this).get()

//        channel = manager?.initialize(this, mainLooper, null)
//        channel?.also { channel ->
//            receiver = manager?.let { WiFiDirectBroadcastReceiver(it, channel, this) }
//        }
//
//        manager?.let { deviceViewModel!!.setData(it) }
//        channel?.let { deviceViewModel!!.setData(it) }


        bottomNavigationView?.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, homeFragment).commit()
                return@OnItemSelectedListener true
            }

            R.id.nav_water_info -> {
                supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, waterFragment).commit()
                return@OnItemSelectedListener true
            }

            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, profileFragment).commit()
                return@OnItemSelectedListener true
            }
        }
            false
        })

    }

    /* register the broadcast receiver with the intent values to be matched */
//    override fun onResume() {
//        super.onResume()
//        receiver?.also { receiver ->
//            registerReceiver(receiver, intentFilter)
//        }
//    }
//
//    /* unregister the broadcast receiver */
//    override fun onPause() {
//        super.onPause()
//        receiver?.also { receiver ->
//            unregisterReceiver(receiver)
//        }
//    }
//
//    fun changePeers(peers: WifiP2pDeviceList) {
//        deviceViewModel?.setData(peers)
//    }
}