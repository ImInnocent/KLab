package com.example.idealmood

import android.app.AlertDialog
import android.content.Intent
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
import kotlinx.android.synthetic.main.fragment_emotion.view.onoffSwitch

/**
 * A simple [Fragment] subclass.
 */
class EmotionFragment : Fragment() {

    var onoffFlag = true
    //var cancelFlag = true

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
        onoffSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(onoffFlag) {
                if (isChecked)
                    showOff2On()
                else
                    showOn2Off()
            }
            else
                onoffFlag = true
            /*if(cancelFlag) { -> 디바이스 취소버튼 누를때로 버튼 상태 바뀌는 것 방지 -> 나중에 다시 해봐야할듯
                onoffFlag = false
                onoffSwitch.isChecked = !onoffSwitch.isChecked
            }
            onoffFlag = true*/
        }

        blueConnectBtn.setOnClickListener {
            activity?.let{
                val intent = Intent (it, DeviceScanActivity::class.java)
                it.startActivity(intent)
            }
        }
    }

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
            onoffSwitch.isChecked = true
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
            onoffSwitch.isChecked = false
        }

        builder.show()
    }

}
