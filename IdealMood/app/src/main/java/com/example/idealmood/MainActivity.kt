package com.example.idealmood

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class MainActivity : AppCompatActivity() {
    private val textArray = arrayListOf<String>("솔루션", "내 감정", "감정 쓰레기통")

    // bluetooth variables
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val mDelimiter: Byte = 10

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var connectedDevices: Set<BluetoothDevice>
    private lateinit var targetBluetoothDevice: BluetoothDevice
    private lateinit var connectedSocket: BluetoothSocket

    private var btInputStream: InputStream? = null
    private var btOutputStream: OutputStream? = null
    private var blueToothThread: Thread? = null
    private var readBufferPositon = 0; //버퍼 내 수신 문자 저장 위치
    private lateinit var readBuffer: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        initPermission()
        initBluetooth()
    }

    private fun init() {
        // 탭 달기
        contents.adapter = MyFragStateAdapter(this)
        TabLayoutMediator(tabLayout, contents){ tab, position ->
            tab.text = textArray[position]
        }.attach()

        contents.isUserInputEnabled = false // 스트롤해서 탭 넘기는 기능 삭제
    }

    private fun initPermission() {
        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
        val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission1 != PackageManager.PERMISSION_GRANTED
            || permission2 != PackageManager.PERMISSION_GRANTED
            || permission3 != PackageManager.PERMISSION_GRANTED
            || permission4 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),
                642)
        } else {
            Log.d("DISCOVERING-PERMISSIONS", "Permissions Granted")
        }
    }

    private fun initBluetooth() {
        // 블루투스 어댑터 가져오기. null이면 블루투스가 안 되는 기기
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        connectedDevices = mutableSetOf()

        // 블루투스가 꺼져있다면 켜질 수 있도록 요청한다.
        if (!bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        } else {
            checkPairedDevices();
        }
    }

    private fun checkPairedDevices() {
        // check paired devices
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            if (deviceName!!.startsWith("INOTE"))
                connectToSelectedDevice(deviceHardwareAddress)

            // TODO: Find our device and auto-connect
//            toast("$deviceName: $deviceHardwareAddress")
        }

        requestActionFound();
    }

    private fun requestActionFound() {
        // 어떤 상황에서 연락을 받을건지 설정함
        val filter = IntentFilter()

        filter.addAction(BluetoothDevice.ACTION_FOUND)    //기기 검색됨
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED) //연결 확인
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED) //연결 끊김 확인
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED) //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)   //기기 검색 시작
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)  //기기 검색 종료

        registerReceiver(receiver, filter)

        var isStarted: Boolean = false
        if (bluetoothAdapter != null)
            isStarted = bluetoothAdapter.startDiscovery()

        toast("isStarted: $isStarted")
    }

    // 응답 할 때 호출되는 내용
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            toast("onReceive: $intent.action")

            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // 찾았을 때 호출 되는 경우.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address

//                    Log.d("deviceName: ", deviceName)
//                    Log.d("deviceHardwareAddress: ", deviceHardwareAddress)

//                    if (deviceName != null)
                        connectedDevices = connectedDevices.plus(device)
                    // TODO: add condition
//                    if (deviceName.length >= 3 && deviceName.substring(0, 3) == "Key") {
//                        connectedDevices.plus(device)
//                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // 탐색 모드(약 11초)가 끝났을 때 호출되는 경우
                    // 탐색된 디바이스중에 찾아서 연결
                    startBluetoothDeviceConnection()
                }
            }
        }
    }

    private fun startBluetoothDeviceConnection() {
        if (connectedDevices.isEmpty()) {
            toast("There is no device")
        }

        // 연결 화면을 출력함
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.select_device_title)

        // 페어링 된 블루투스 장치의 이름 목록 작성
        val listItems: MutableList<String> = ArrayList()
        for (btDevice in connectedDevices) {
            if (btDevice.name != null)
                listItems.add(btDevice.name)
            else
                listItems.add(btDevice.address)
        }

        listItems.add("Cancel") // 취소 항목 추가


        val items = listItems.toTypedArray<CharSequence>()

        builder.setItems(items) { dialog, which ->
            dialog as Dialog
            if (which == connectedDevices.size) {
                // 연결할 장치를 선택하지 않고 '취소'를 누른 경우
            } else {
                // 기기 이름을 선택한 경우 선택한 기기 이름과 같은 블루투스 객체를 찾아서 연결을 시도한다
                for (bTdevice in connectedDevices) {
                    if (bTdevice.name == items[which].toString() || bTdevice.address == items[which].toString()) {
                        Log.d("Bluetooth Connect", bTdevice.address)
                        connectBluetoothDevice(bTdevice)
                        Log.d("Bluetooth Connect", "ConnectBluetoothDevice")
                        break
                    }
                }
            }
        }
        builder.setCancelable(false) // 뒤로 가기 버튼 사용 금지

        val alert: AlertDialog = builder.create()
        alert.show()
        Log.d("Bluetooth Connect", "alert start")
    }

    private fun connectBluetoothDevice(device: BluetoothDevice) {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val expectedName = ""

        //pairing되어 있는 기기의 목록을 가져와서 연결하고자 하는 기기가 이전 기기 목록에 있는지 확인
        var alreadyBondedFlag = false
        if (pairedDevices != null) {
            if (pairedDevices.isNotEmpty()) {
                for (bonded_device in pairedDevices) {
                    if (expectedName == bonded_device.name) {
                        alreadyBondedFlag = true
                        break
                    }
                }
            }
        }

        //pairing process
        //만약 pairing기록이 있으면 바로 연결을 수행하며, 없으면 createBond()함수를 통해서 pairing을 수행한다.
        if (!alreadyBondedFlag) {
            try {
                //pairing수행
                device.createBond()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            connectToSelectedDevice(device.address)
        }
    }

    //블루투스 기기에 연결하는 과정이 시간이 걸리기 때문에 그냥 함수로 수행을 하면 GUI에 영향을 미친다
    //따라서 연결 과정을 thread로 수행하고 thread의 수행 결과를 받아 다음 과정으로 넘어간다.
    private fun connectToSelectedDevice(selectedDeviceAddress: String) {
        //handler는 thread에서 던지는 메세지를 보고 다음 동작을 수행시킨다.
        val handler: Handler = @SuppressLint("HandlerLeak")

        object : Handler() {
            override fun handleMessage(msg: Message) {
                // 연결 완료
                if (msg.what === 1) {
                    try {
                        //연결이 완료되면 소켓에서 outstream과 inputstream을 얻는다. 블루투스를 통해
                        //데이터를 주고 받는 통로가 된다.
                        btOutputStream = connectedSocket.getOutputStream()
                        btInputStream = connectedSocket.getInputStream()
                        // 데이터 수신 준비
                        beginListenForData()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    //연결 실패
                    Toast.makeText(this@MainActivity, "Please check the device", Toast.LENGTH_SHORT).show()

                    try {
                        connectedSocket.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        //연결과정을 수행할 thread 생성
        val thread = Thread(Runnable {
            //선택된 기기의 이름을 갖는 bluetooth device의 object
            val connectedDevice = getDeviceFromBondedList(selectedDeviceAddress)
            // TODO: 이거 뭔지 모르겠는데 UUID 바꿔야 함
            val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
            try {
                // 소켓 생성
                connectedSocket = connectedDevice!!.createRfcommSocketToServiceRecord(uuid)
                // RFCOMM 채널을 통한 연결, socket에 connect하는데 시간이 걸린다. 따라서 ui에 영향을 주지 않기 위해서는
                // Thread로 연결 과정을 수행해야 한다.
                connectedSocket.connect()
                handler.sendEmptyMessage(1)
            } catch (e: java.lang.Exception) {
                // 블루투스 연결 중 오류 발생
                handler.sendEmptyMessage(-1)
            }
        })

        //연결 thread를 수행한다
        thread.start()
    }

    //기기에 저장되어 있는 해당 이름을 갖는 블루투스 디바이스의 bluetoothdevice 객채를 출력하는 함수
    //bluetoothdevice객채는 기기의 맥주소뿐만 아니라 다양한 정보를 저장하고 있다.
    private fun getDeviceFromBondedList(address: String): BluetoothDevice? {
        var selectedDevice: BluetoothDevice? = null

        //pair 목록에서 해당 주소을 갖는 기기 검색, 찾으면 해당 device 출력
        for (device in bluetoothAdapter.bondedDevices) {
            if (address == device.address) {
                selectedDevice = device
                break
            }
        }

        return selectedDevice
    }

    private fun beginListenForData() {
        val handler = Handler()
        readBuffer = ByteArray(1024) //  수신 버퍼
        readBufferPositon = 0 //   버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        blueToothThread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted) {
                try {
                    val bytesAvailable: Int = btInputStream?.available() ?: break

                    if (bytesAvailable <= 0)
                        continue

                    //데이터가 수신된 경우
                    val packetBytes = ByteArray(bytesAvailable)
                    btInputStream?.read(packetBytes)

                    for (i in 0 until bytesAvailable) {
                        val b = packetBytes[i]

                        if (b != mDelimiter) {
                            readBuffer[readBufferPositon++] = b
                            continue
                        }

                        val encodedBytes = ByteArray(readBufferPositon)
                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.size)
                        val data = String(encodedBytes, Charset.forName("US-ASCII"))
                        readBufferPositon = 0

                        handler.post {
                            //수신된 데이터는 data 변수에 string으로 저장!! 이 데이터를 이용하면 된다.
                            val cArr = data.toCharArray() // char 배열로 변환
                            if (cArr[0] == 'a') {
                                if (cArr[1] == '1') {

                                    //a1이라는 데이터가 수신되었을 때
                                }
                                if (cArr[1] == '2') {

                                    //a2라는 데이터가 수신 되었을 때
                                }
                            }
                        }
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })

        //데이터 수신 thread 시작
        blueToothThread!!.start()
    }

    fun SendResetSignal() {
        val msg = "bs00000"
        try {
            btOutputStream?.write(msg.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

//    private inner class AcceptThread : Thread() {
//        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
//        }
//
//        override fun run() {
//            // Keep listening until exception occurs or a socket is returned.
//            var shouldLoop = true
//            while (shouldLoop) {
//                val socket: BluetoothSocket? = try {
//                    mmServerSocket?.accept()
//                } catch (e: IOException) {
//                    Log.e(TAG, "Socket's accept() method failed", e)
//                    shouldLoop = false
//                    null
//                }
//                socket?.also {
//                    manageMyConnectedSocket(it)
//                    mmServerSocket?.close()
//                    shouldLoop = false
//                }
//            }
//        }
//
//        // Closes the connect socket and causes the thread to finish.
//        fun cancel() {
//            try {
//                mmServerSocket?.close()
//            } catch (e: IOException) {
//                Log.e(TAG, "Could not close the connect socket", e)
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 블루투스 켜는 요청에 대한 처리
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (bluetoothAdapter.isEnabled) {
                    toast("블루투스가 연결됨!")
                    checkPairedDevices();
                } else {
                    toast("블루투스가 연결되지 않음")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("블루투스가 요청 취소됨")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }
}
