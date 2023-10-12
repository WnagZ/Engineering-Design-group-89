package com.example.engineering_design_app.arduino

import android.os.AsyncTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.Socket

class ArduinoDiscoveryTask {

    suspend fun discoverArduino(): String {
        val ipAddress = "Arduino_IP_Address" // Replace with the Arduino's IP address
        val port = 12345 // Replace with the port you want to communicate on

        return try {
            val result = withContext(Dispatchers.IO) {
                val socket = Socket(InetAddress.getByName(ipAddress), port)
                socket.close()
                "Arduino found at $ipAddress:$port"
            }
            result
        } catch (e: Exception) {
            "Arduino not found"
        }
    }
}