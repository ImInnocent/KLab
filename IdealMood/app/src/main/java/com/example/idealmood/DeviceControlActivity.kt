package com.example.idealmood

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_device_control.*
import org.jetbrains.anko.toast

class DeviceControlActivity: AppCompatActivity() {
    private var deviceAddress: String = ""
    private var bluetoothService: BluetoothLeService? = null
    var connected = false
    private var functionCallStack: String = ""

    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null

    // BluetoothLeService에서 broadcastUpdate를 하면 호출됨
    private val gattUpdateReceiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            var action = intent?.action

            when (action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    // CONNECTED to GATT
                    connected = true
                    connectionText.text = "Connected: Yes"

                    toast("BLE: Connected")
                    updateCallStack("BluetoothLeService.ACTION_GATT_CONNECTED")
                }

                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    connectionText.text = "Connected: NO"

                    toast("BLE: Disconnected")
                    updateCallStack("BluetoothLeService.ACTION_GATT_DISCONNECTED")
                }

                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    bluetoothService?.let {
                        // TODO
//                        selectCharacteristicData(it.servi ())
                    }

                    updateCallStack("BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED")
                }

                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    // 데이터를 받았을 때
                    val resp: String? = intent?.getStringExtra(BluetoothLeService.EXTRA_DATA)
                    heartRateText.text = "HeartRate: $resp"

                    toast("ACTION_DATA_AVAILABLE: $resp")
                    updateCallStack("BluetoothLeService.ACTION_DATA_AVAILABLE: $resp")
                }
            }
        }
    }

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bluetoothService = (service as BluetoothLeService.LocalBinder).service
            bluetoothService?.connect(deviceAddress)

            updateCallStack("onServiceConnected: $name, $deviceAddress")
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bluetoothService = null

            updateCallStack("onServiceDisconnected: $p0")
        }

    }

    private fun updateCallStack(newString: String) {
        functionCallStack += newString + "\n"
        functionCallStackText.text = functionCallStack
    }

    private fun selectCharacteristicData(gattServices: List<BluetoothGattService>): Boolean {
        for (gattService in gattServices) {
            var gattCharacteristics: List<BluetoothGattCharacteristic> = gattService.characteristics

            for (gattCharacteristic in gattCharacteristics) {
                when (gattCharacteristic.uuid) {
                    BluetoothLeService.UUID_DATA_WRITE -> writeCharacteristic = gattCharacteristic
                    BluetoothLeService.UUID_DATA_NOTIFY -> notifyCharacteristic = gattCharacteristic
                }
            }
        }

        return true
    }

    private fun sendData(data: String) {
        writeCharacteristic?.let {
            if (it.properties or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE > 0) {
                bluetoothService?.writeCharacteristic(it, data)
            }
        }

        notifyCharacteristic?.let {
            if (it.properties or BluetoothGattCharacteristic.PROPERTY_NOTIFY > 0) {
                bluetoothService?.setCharacteristicNotification(it, true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        deviceAddress = intent.getStringExtra("address")

        heartRateText.text = "HeartRate: Not Get Yet"
        connectionText.text = "Connected: NO"

        updateCallStack("=========== Call Stack ============")

        registerReceiver(gattUpdateReceiver, IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED).apply {
            addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
        })

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)

        updateCallStack("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
        bluetoothService = null

        updateCallStack("onDestroy")
    }
}