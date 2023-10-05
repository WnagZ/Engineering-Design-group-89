package com.example.engineering_design_app.ui

import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.engineering_design_app.R
import me.aflak.arduino.Arduino
import me.aflak.arduino.ArduinoListener


class ConnectActivity : AppCompatActivity(), ArduinoListener {
    private var arduino: Arduino? = null
    private var displayTextView: TextView? = null
    private var editText: EditText? = null
    private var sendBtn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)
        displayTextView = findViewById(R.id.diplayTextView)
        editText = findViewById(R.id.editText)
        sendBtn = findViewById(R.id.sendBtn)
        displayTextView?.movementMethod = ScrollingMovementMethod()
        sendBtn?.setOnClickListener {
            val editTextString = editText?.text.toString()
            arduino!!.send(editTextString.toByteArray())
            editText?.text?.clear()
        }
        arduino = Arduino(this)
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
        display(kotlin.String())
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
        runOnUiThread { displayTextView!!.append(message) }
    }
}