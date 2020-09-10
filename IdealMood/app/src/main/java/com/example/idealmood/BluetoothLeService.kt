package com.example.idealmood

import android.app.Service
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.jetbrains.anko.toast
import java.util.*

class BluetoothLeService: Service() {

    var connectionState = Companion.STATE_DISCONNECTED
    private var bluetoothGatt: BluetoothGatt? = null
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    companion object {
        const val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        const val EXTRA_DATA = "EXTRA_DATA"
        val UUID_DATA_NOTIFY: UUID = UUID.fromString("0000fff1-0000-1000-80000-00805f9b34fb")
        val UUID_DATA_WRITE: UUID = UUID.fromString("0000fff2-0000-1000-80000-00805f9b34fb")
        val CLIENT_CHARACTERISTIC_CONFIG: UUID
                = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
    }

    // 블루투스 연결과 관련한 변경이 있을 때 호출됨
    private val gattCallback = object: BluetoothGattCallback() {
        // 연결 상태가 바뀌었을 때
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            var intentAction = ""

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectionState = STATE_CONNECTED
                    broadcastUpdate(ACTION_GATT_CONNECTED, null)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    connectionState = Companion.STATE_DISCONNECTED
                    broadcastUpdate(ACTION_GATT_DISCONNECTED, null)
                }
            }
        }

        // GATT가 되는 기기를 찾았을 때
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, null)
                else -> {}
            }
        }

        //  ?????
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)

            when (status) {
                BluetoothGatt.GATT_SUCCESS -> broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
                else -> {}
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, data: String) {
        characteristic.setValue(data)
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        bluetoothGatt?.writeCharacteristic(characteristic)
    }

    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, enable: Boolean) {
        bluetoothGatt?.setCharacteristicNotification(characteristic, enable)

        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG).apply {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        }
        bluetoothGatt?.writeDescriptor(descriptor)
    }

    private fun broadcastUpdate(
        action: String,
        characteristic: BluetoothGattCharacteristic?
    ) {
        val intent = Intent(action)

        characteristic?.let {
            when (it.uuid) {
                UUID_DATA_NOTIFY -> {
                    val flag = characteristic.properties
//                    val format = when (flag and 0x01) {
//                        0x01 -> {
//                            BluetoothGattCharacteristic.FORMAT_UINT16
//                        }
//                        else -> {
//                            BluetoothGattCharacteristic.FORMAT_UINT8
//                        }
//                    }

                    // TODO: format change
                    val format = BluetoothGattCharacteristic.FORMAT_UINT8
                    val heartRate = characteristic.getIntValue(format, 1)

                    toast("heartRate: $heartRate")

                    intent.putExtra(EXTRA_DATA, heartRate.toString())
                }
                else -> {
                    val data: ByteArray? = characteristic.value

                    if (data?.isNotEmpty() == true) {
                        val hexString: String = data.joinToString(separator = " ") {
                            String.format("%02X", it)
                        }
                        intent.putExtra(EXTRA_DATA, "$data\n$hexString")
                    } else {

                    }
                }
            }
        }

        sendBroadcast(intent)
    }

    inner class LocalBinder: Binder() {
        val service = this@BluetoothLeService
    }

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? = binder

    fun connect(address: String): Boolean {
        bluetoothGatt?.let {
            if (address == it.device.address) {
                return if (it.connect()) {
                    connectionState = STATE_CONNECTING
                    true
                } else {
                    false
                }
            }
        }

        val device = bluetoothAdapter.getRemoteDevice(address)
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
        connectionState = STATE_CONNECTING

        return true
    }

    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
    }

    private fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
