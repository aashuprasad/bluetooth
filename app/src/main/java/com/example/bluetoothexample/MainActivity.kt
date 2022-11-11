package com.example.bluetoothexample

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
import android.bluetooth.BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_DISCOVERABLE_BT: Int = 2
    private val REQUEST_CODE_ENABLE_BT: Int = 1
    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

    //bluetooth adapter
    private lateinit var bAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        //register for broadcasts when a device is discovered
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

//        registerReceiver(receiver, filter)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        //check if BT is on/off
        if (bAdapter == null) {
            bluetoothStatusTv.text = "Bluetooth is not available"
        } else {
            bluetoothStatusTv.text = "Bluetooth is available"
        }
        //set image according to bluetooth status
        if (bAdapter.isEnabled) {
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
        } else {
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        }
        //turn on bluetooth
        turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled) {
                //already on
                Toast.makeText(this, "Already on", Toast.LENGTH_LONG).show()
            } else {
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                            2
                        )
                    }
                    return@setOnClickListener
                }
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }
        //turn off bluetooth
        turnOffBtn.setOnClickListener {
            if (!bAdapter.isEnabled) {
                //already off
                Toast.makeText(this, "Already off", Toast.LENGTH_LONG).show()
            } else {
                bAdapter.disable()
                bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
                Toast.makeText(this, "Bluetooth turned off", Toast.LENGTH_LONG).show()
            }
        }
        //discoverable
        discoverableBtn.setOnClickListener {


//            if(!bAdapter.isDiscovering){
//                Toast.makeText(this,"Making device discoverable",Toast.LENGTH_LONG).show()
//                var intent:Intent = Intent(ACTION_REQUEST_DISCOVERABLE).apply {
//                    putExtra(EXTRA_DISCOVERABLE_DURATION, 300)}
//                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
//            }
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(p0: Context?, p1: Intent?) {
                    val action: String? = intent.action
                    when (action) {
                        BluetoothDevice.ACTION_FOUND -> {
                            //discovery has found a device.
                            //get the BLuetoothDevice object and its info from the Intent.
                            val device: BluetoothDevice? =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                            if (ActivityCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    ActivityCompat.requestPermissions(
                                        this@MainActivity,
                                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                        2
                                    )
                                }
                                return
                            }
                            val deviceName = device?.name
                            val deviceHardwareAddress = device?.address
                            availableTv.append("\nDevice: $deviceName\t\t\tMAC: $deviceHardwareAddress")

                        }
                    }
                }
            }
            registerReceiver(receiver, filter)
        }

        //paired devices
        pairedBtn.setOnClickListener {
            if (bAdapter.isEnabled) {
                pairedTv.text = "Paired devices"
                //get list of paired devices
                val pairedDevices: Set<BluetoothDevice>? = bAdapter?.bondedDevices
                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    pairedTv.append("\nDevice: $deviceName\t\t\tMAC: $deviceHardwareAddress")
                }
            } else {
                Toast.makeText(this, "Turn on BT", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(this, "Bluetooth is on", Toast.LENGTH_LONG).show()
                } else {
                    //user denied to turn on bluetooth from confirmation dialog
                    Toast.makeText(this, "Bluetooth not able to switch on!", Toast.LENGTH_LONG)
                        .show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    //create a BroadcastReceiver for ACTION_FOUND
//    discoverableBtn.setOnClickListener{
//        private val receiver = object : BroadcastReceiver() {
//            override fun onReceive(p0: Context?, p1: Intent?) {
//                val action: String? = intent.action
//                when (action) {
//                    BluetoothDevice.ACTION_FOUND -> {
//                        //discovery has found a device.
//                        //get the BLuetoothDevice object and its info from the Intent.
//                        val device: BluetoothDevice? =
//                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//
//                        if (ActivityCompat.checkSelfPermission(
//                                this@MainActivity,
//                                Manifest.permission.BLUETOOTH_CONNECT
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                                ActivityCompat.requestPermissions(
//                                    this@MainActivity,
//                                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
//                                    2
//                                )
//                            }
//                            return
//                        }
//                        val deviceName = device?.name
//                        val deviceHardwareAddress = device?.address
//                        availableTv.append("\nDevice: $deviceName\t\t\tMAC: $deviceHardwareAddress")
//
//                    }
//                }
//            }
//        }
//    }
}