package com.example.idealmood

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_emotion.*
import kotlinx.android.synthetic.main.fragment_emotion.view.*

/**
 * A simple [Fragment] subclass.
 */
class EmotionFragment : Fragment() {

    var onoffFlag = true
    //var cancelFlag = true

    companion object {
        val COLOR_EMOJI_ON = Color.rgb(255, 255, 255)
        val COLOR_EMOJI_OFF = Color.rgb(90, 90, 90)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emotion, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        init()
        super.onActivityCreated(savedInstanceState)
    }

    private fun init() {
        blueConnectBtn.setOnClickListener {
            activity?.let{
//                val intent = Intent (it, DeviceScanActivity::class.java)
                val intent = Intent (it, BluetoothConnectActivity::class.java)
                it.startActivity(intent)
            }
        }

        // 이미지 변경해주기
        setEmojiImg(DataManager.getInstance().isStarted)

        emotionImg.setOnClickListener {
//            if bluetooth가 connected되었는지를 알려주는 변수가 true일 때  // 전원 켜져있을 때
            if (DataManager.getInstance().isStarted) {
                // ****** 전원 끄는 코드 작성 *******
                DataManager.getInstance().disconnect()

                setEmojiImg(false)
            }
            else {
                setEmojiImg(true)
                DataManager.getInstance().connect()

                // 블루투스 연결 액티비티 실행
                // 디바이스 없으니까 이미지만
//                activity?.let {
//                    val intent = Intent(it, BluetoothConnectActivity::class.java)
//                    it.startActivity(intent)
//                }
            }
        }
    }

    private fun setEmojiImg(isOn: Boolean) {
        if (isOn) {
            // 원래색
            emotionImg.setColorFilter(COLOR_EMOJI_ON, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
        else {
            // 회색으로 바꿈
            emotionImg.setColorFilter(COLOR_EMOJI_OFF, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    /* 전원 on/off 하기 전 다시 물어보는 다이얼로근데 굳이 할 필요 없어보임.
    private fun showOn2Off() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@EmotionFragment.requireContext(), R.style.Theme_AppCompat_Light_Dialog))
        builder.setTitle("Confirm")
        builder.setMessage(getString(R.string.emotion_off_measure))

        builder.setPositiveButton("OK") { _, _ ->
            //cancelFlag = false
            Toast.makeText(this.requireContext(), "(측정을 종료함)", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("CANCEL") { _, _ ->
            //cancelFlag = false
            onoffFlag = false
        }
        builder.show()
    }

    private fun showOff2On() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this@EmotionFragment.requireContext(), R.style.Theme_AppCompat_Light_Dialog))
        builder.setTitle("Confirm")
        builder.setMessage(getString(R.string.emotion_on_measure))

        builder.setPositiveButton("OK") { _, _ ->
            Toast.makeText(this.requireContext(), "(측정 다시 시작)", Toast.LENGTH_SHORT).show()
            //onoffSwitch.isChecked = true
        }
        builder.setNegativeButton("CANCEL") { _, _ ->
            onoffFlag = false
        }

        builder.show()
    }*/

}
