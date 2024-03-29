package com.medlinx.websocket.example

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.medlinx.websocket.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var connectBtn: Button
    private lateinit var sendBtn: Button
    private val vm: MainViewModel by viewModels()
    private var firstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.vm = vm
        binding.lifecycleOwner = this

        vm.init(this)

        connectBtn = findViewById(R.id.connect_btn)
        sendBtn = findViewById(R.id.send_btn)
        connectBtn.setOnClickListener(this)
        sendBtn.setOnClickListener(this)

        vm.errorMessage.observe(this, Observer {
            Toast.makeText(this , it, Toast.LENGTH_LONG).show()
        })
    }

    override fun onResume() {
        super.onResume()
        if (!firstLaunch) vm.connectWebSocket()
        firstLaunch = false
    }

    override fun onPause() {
        super.onPause()
        vm.closeWebsSocket()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.connect_btn -> {
                if (vm.isConnected.value == true) {
                    vm.closeWebsSocket()
                } else {
                    vm.connectWebSocket()
                 }
            }
            R.id.send_btn -> {
                vm.sendToWebSocket()
            }
        }
    }
}