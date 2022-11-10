package com.example.bluetoothexample

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_DISCOVERABLE_BT: Int = 2
    private val REQUEST_CODE_ENABLE_BT:Int = 1

    //bluetooth adapter
    lateinit var bAdapter:BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        //check if BT is on/off
        if(bAdapter==null){
            bluetoothStatusTv.text = "Bluetooth is not available"
        }
        else{
            bluetoothStatusTv.text = "Bluetooth is available"
        }
        //set image according to bluetooth status
        if(bAdapter.isEnabled){
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
        }
        else{
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        //turn on bluetooth
        turnOnBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                //already on
                Toast.makeText(this,"Already on", Toast.LENGTH_LONG).show()
            }
            else{
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),2)
                    }
                    return@setOnClickListener
                }
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
            }
        }
        //turn off bluetooth
        turnOffBtn.setOnClickListener {
            if(!bAdapter.isEnabled){
            //already off
            Toast.makeText(this,"Already off", Toast.LENGTH_LONG).show()
        }
        else{
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),2)
                    }
                    return@setOnClickListener
                }
            bAdapter.disable()
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
            Toast.makeText(this,"Bluetooth turned off", Toast.LENGTH_LONG).show()

            } }
        //discoverable
        discoverableBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),2)
                }
                return@setOnClickListener
            }
            if(!bAdapter.isDiscovering){
                Toast.makeText(this,"Making device discoverable",Toast.LENGTH_LONG).show()
                var intent:Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                    putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)}
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }
        //paired devices
        pairedBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                pairedTv.text = "Paired devices"
                //get list of paired devices
                //val devices = bAdapter.bondedDevices


                val pairedDevices: Set<BluetoothDevice>? = bAdapter?.bondedDevices
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),2)
                    }
                    return@setOnClickListener
                }
                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                }

            }
            else{
                Toast.makeText(this,"Turn on BT",Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            REQUEST_CODE_ENABLE_BT->
                if(resultCode==Activity.RESULT_OK){
                    bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(this,"Bluetooth is on", Toast.LENGTH_LONG).show()
                }
            else{
                    //user denied to turn on bluetooth from comfirmation dialog
                    Toast.makeText(this,"Bluetooth not able to switch on!", Toast.LENGTH_LONG).show()
                }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}