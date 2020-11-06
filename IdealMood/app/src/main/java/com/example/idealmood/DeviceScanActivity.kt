package com.example.idealmood

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ListActivity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.content_device_scan.*
import org.jetbrains.anko.toast

/**
 * Activity for scanning and displaying available BLE devices.
 */
class DeviceScanActivity: AppCompatActivity() {
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    private val REQUEST_ENABLE_BT = 1000
    private val SCAN_PERIOD = 10000L

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    private var mScanning: Boolean = false
    private var arrayDevices = ArrayList<BluetoothDevice>()
    private val handler = Handler()
    private lateinit var recyclerViewAdapter: RvAdapter

    // 실질적으로 검색 결과를 처리하는 액티비티
    private val scanCallback = object: ScanCallback() {
        // 검색 실패
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            toast( "BLE SCAN Failed: $errorCode")
        }

        // 검색 결과
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                if (it.device.name != null) {
                    toast(it.device.name)
                }
                if (!arrayDevices.contains(it.device)) {

                    arrayDevices.add(it.device)
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }

        // 검색 결과가 한꺼번에 도착했을 때
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

            results?.let {
                var needUpdate: Boolean = false

                for (result in it) {
                    if (!arrayDevices.contains(result.device)) {
                        arrayDevices.add(result.device)
                        needUpdate = true
                    }
                }

                if (needUpdate) {
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    // 스캔 버튼을 눌렀을 때 호출되는 것
    private fun scanLeDevice(enable: Boolean) {
        when(enable) {
            true -> {
                if (mScanning) {
                    return
                }

                handler.postDelayed({
                    // end scanning
                    mScanning = false

                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)

                    toast("검색 종료")
                    searchButton.text = "Search"
                }, SCAN_PERIOD)

                // start scanning and if found, postDelayed called
                mScanning = true

                arrayDevices.clear()
                arrayDevices.addAll(BluetoothAdapter.getDefaultAdapter().bondedDevices)

                bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)

                toast("검색 시작")
                searchButton.text = "Searching..."
            }
            else -> {
                mScanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)

                toast("검색 중단")
                searchButton.text = "Search"
            }
        }
    }

    private fun selectDevice(address: String) {
        // need fix
//        DataManager.getInstance().isStarted = true
//        DataManager.getInstance().heartBeat = 100
//        finish()

        val intent = Intent(this, DeviceControlActivity::class.java)
        intent.putExtra("address", address)

        if (mScanning) scanLeDevice(false)

        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_device_scan)

        initBT()

        searchButton.setOnClickListener {
            when (mScanning) {
                true -> scanLeDevice(false)
                false -> scanLeDevice(true)
            }
        }

        recyclerViewAdapter = RvAdapter(this@DeviceScanActivity, arrayDevices) {
            toast(it.address)
            selectDevice(it.address)
        }

        val deviceList: RecyclerView = findViewById(R.id.deviceList)

        deviceList.adapter = recyclerViewAdapter

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
        deviceList.layoutManager = lm
        deviceList.setHasFixedSize(true)
    }

    private fun initBT() {
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
//            toast("DISCOVERING-PERMISSIONS: Permissions Granted")
        }
    }

    override fun onResume() {
        super.onResume()

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            toast("BLE 지원이 안되는 디바이스")
            finish()
        }

        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enabledBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enabledBtIntent, REQUEST_ENABLE_BT)
        }

        scanLeDevice(true)
    }
}