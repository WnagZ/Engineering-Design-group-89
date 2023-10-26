package com.example.engineering_design_app.model

import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel : ViewModel() {

    private val selectedDevice = MutableLiveData<Device>()
    private val peers = MutableLiveData<WifiP2pDeviceList>()
    private val manager = MutableLiveData<WifiP2pManager>()
    private val channel = MutableLiveData<WifiP2pManager.Channel>()


    fun setData(m: WifiP2pManager) {
        manager.value = m
    }

    fun getManager(): LiveData<WifiP2pManager> {
        return manager
    }

    fun setData(c: WifiP2pManager.Channel) {
        channel.value = c
    }

    fun getChannel(): LiveData<WifiP2pManager.Channel> {
        return channel
    }

    fun setData(device: Device) {
        if(selectedDevice.value == null){
            selectedDevice.value = device
        }
        else if(selectedDevice.value!!.name == device.name) {
//            device.waterUsage?.let { selectedDevice.value!!.waterUsage?.putAll(it) }
            device.waterUsage?.let { selectedDevice.value!!.waterUsage?.addAll(it) }
        }
    }

    fun getSelectedDevice(): LiveData<Device> {
        return selectedDevice
    }

    fun removeSelectedDevice() {
        selectedDevice.value = null
    }

    fun setData(p: WifiP2pDeviceList) {
        peers.value = p
    }

    fun getPeers(): LiveData<WifiP2pDeviceList> {
        return peers
    }
}