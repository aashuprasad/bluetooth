package com.example.bluetoothexample

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
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
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
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
            bAdapter.disable()
            bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
            Toast.makeText(this,"Bluetooth turned off", Toast.LENGTH_LONG).show()

            } }
        //discoverable
        discoverableBtn.setOnClickListener {
            if(!bAdapter.isDiscovering){
                Toast.makeText(this,"Making device discoverable",Toast.LENGTH_LONG).show()
                var intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }
        }
        //paired devices
        pairedBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                pairedTv.text = "Paired devices"
                //get list of paired devices
                val devices = bAdapter.bondedDevices
                for(device in devices){
                    val deviceName = device.name

                    pairedTv.append("\nDevice:  $deviceName, $device")
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