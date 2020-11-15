package com.example.idealmood

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.jetbrains.anko.toast
import java.io.*
import java.util.*

open class BluetoothConnectActivity : AppCompatActivity() {
    private var mTvBluetoothStatus: TextView? = null
    private var mTvReceiveData: TextView? = null
    private var mTvSendData: TextView? = null
    private var mBtnBluetoothOn: Button? = null
    private var mBtnBluetoothOff: Button? = null
    private var mBtnConnect: Button? = null
    private var mBtnSendData: Button? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevices: Set<BluetoothDevice>? = null
    private var mListPairedDevices: MutableList<String>? = null
    private var mBluetoothHandler: Handler? = null
    private var mThreadConnectedBluetooth: ConnectedBluetoothThread? = null
    private var mBluetoothDevice: BluetoothDevice? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    private val myDBHelper = MyDBHelper(this)

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_connect)
        mTvBluetoothStatus = findViewById<TextView>(R.id.tvBluetoothStatus)
        mTvReceiveData = findViewById<TextView>(R.id.tvReceiveData)
        mTvSendData = findViewById<EditText>(R.id.tvSendData)
        mBtnBluetoothOn = findViewById<Button>(R.id.btnBluetoothOn)
        mBtnBluetoothOff = findViewById<Button>(R.id.btnBluetoothOff)
        mBtnConnect = findViewById<Button>(R.id.btnConnect)
        mBtnSendData = findViewById<Button>(R.id.btnSendData)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBtnBluetoothOn!!.setOnClickListener { bluetoothOn() }
        mBtnBluetoothOff!!.setOnClickListener { bluetoothOff() }
        mBtnConnect!!.setOnClickListener { listPairedDevices() }
        mBtnSendData!!.setOnClickListener {
            if (mThreadConnectedBluetooth != null) {
                mThreadConnectedBluetooth!!.write(mTvSendData!!.text.toString())
                mTvSendData!!.text = ""
            }
        }

        mBluetoothHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == BT_MESSAGE_READ) {
                    var readMessage: String? = null
                    try {
                        readMessage = String((msg.obj as ByteArray),  charset("UTF-8"))
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }

                    val filteredMsg: String = readMessage!!.filter { it.isDigit() }
                    toast(readMessage)
                    if (filteredMsg.isNotEmpty())
                        if (DataManager.getInstance().addHeartBeat(filteredMsg.toInt())) {
                            // 심박수가 COUNT이상 채워져서 records에 data가 넘어오면 DB에 저장하기
                            val HBarray = DataManager.getInstance().records.split(", ")
                            var HBsum = 0
                            for (i in HBarray)
                                HBsum += i.toInt()
                            myDBHelper.HB_insertData(HBsum / 6) // 심박수 분당 평균 DB에 저장
                        }
                    mTvReceiveData!!.text = readMessage
                }
            }
        }
    }

    fun bluetoothOn() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_LONG)
                .show()
        } else {
            if (mBluetoothAdapter!!.isEnabled) {
                Toast.makeText(applicationContext, "블루투스가 이미 활성화 되어 있습니다.", Toast.LENGTH_LONG)
                    .show()
                mTvBluetoothStatus!!.text = "활성화"
            } else {
                Toast.makeText(applicationContext, "블루투스가 활성화 되어 있지 않습니다.", Toast.LENGTH_LONG)
                    .show()
                val intentBluetoothEnable = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(
                    intentBluetoothEnable,
                    BT_REQUEST_ENABLE
                )
            }
        }
    }

    private fun bluetoothOff() {
        if (mBluetoothAdapter!!.isEnabled) {
            mBluetoothAdapter!!.disable()
            Toast.makeText(applicationContext, "블루투스가 비활성화 되었습니다.", Toast.LENGTH_SHORT).show()
            mTvBluetoothStatus!!.text = "비활성화"
        } else {
            Toast.makeText(applicationContext, "블루투스가 이미 비활성화 되어 있습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            BT_REQUEST_ENABLE -> if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                Toast.makeText(applicationContext, "블루투스 활성화", Toast.LENGTH_LONG).show()
                mTvBluetoothStatus!!.text = "활성화"
            } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                Toast.makeText(applicationContext, "취소", Toast.LENGTH_LONG).show()
                mTvBluetoothStatus!!.text = "비활성화"
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun listPairedDevices() {
        if (mBluetoothAdapter!!.isEnabled) {
            mPairedDevices = mBluetoothAdapter!!.bondedDevices
            if ((mPairedDevices as MutableSet<BluetoothDevice>?)?.size ?: 0 > 0) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("장치 선택")
                mListPairedDevices = ArrayList()
                for (device in (mPairedDevices as MutableSet<BluetoothDevice>?)!!) {
                    (mListPairedDevices as ArrayList<String>).add(device.name)
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                val items =
                    (mListPairedDevices as ArrayList<String>).toTypedArray<CharSequence>()
                (mListPairedDevices as ArrayList<String>).toTypedArray<CharSequence>()
                builder.setItems(items
                ) { _, item -> connectSelectedDevice(items[item].toString()) }
                val alert: AlertDialog = builder.create()
                alert.show()
            } else {
                Toast.makeText(applicationContext, "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(applicationContext, "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun connectSelectedDevice(selectedDeviceName: String) {
        for (tempDevice in mPairedDevices!!) {
            if (selectedDeviceName == tempDevice.name) {
                mBluetoothDevice = tempDevice
                break
            }
        }
        try {
            mBluetoothSocket =
                mBluetoothDevice!!.createRfcommSocketToServiceRecord(BT_UUID)
            mBluetoothSocket?.connect()

            mThreadConnectedBluetooth =
                ConnectedBluetoothThread(mBluetoothSocket!!)
            mThreadConnectedBluetooth!!.start()
            mBluetoothHandler!!.obtainMessage(BT_CONNECTING_STATUS, 1, -1)
                .sendToTarget()
            DataManager.getInstance().isStarted = true
        } catch (e: IOException) {
            Toast.makeText(applicationContext, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                .show()
        }
    }

    inner class ConnectedBluetoothThread(private val mmSocket: BluetoothSocket) :
        Thread() {
        private val mmInStream: DataInputStream?
        private val mmOutStream: OutputStream?

        override fun run() {
            var bytes: Int
            while (true) {
                try {
                    bytes = mmInStream!!.available()
                    if (bytes != 0) {
                        SystemClock.sleep(1000)
                        val buffer = ByteArray(1024)
                        bytes = mmInStream.available()
                        bytes = mmInStream.read(buffer, 0, bytes)
                        mBluetoothHandler!!.obtainMessage(
                            BT_MESSAGE_READ,
                            bytes,
                            -1,
                            buffer
                        ).sendToTarget()
                    }
                } catch (e: IOException) {
                    break
                }
            }
        }

        fun write(str: String) {
            val bytes = str.toByteArray()
            try {
                mmOutStream!!.write(bytes)
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
        }

        init {
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = mmSocket.inputStream
                tmpOut = mmSocket.outputStream
            } catch (e: IOException) {
                Toast.makeText(applicationContext, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG)
                    .show()
            }
            mmInStream = DataInputStream(tmpIn)
            mmOutStream = tmpOut
        }
    }

    companion object {
        const val BT_REQUEST_ENABLE = 1
        const val BT_MESSAGE_READ = 2
        const val BT_CONNECTING_STATUS = 3
        val BT_UUID: UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }
}