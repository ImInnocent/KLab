package com.example.idealmood

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_drawer_emergency_setting.*
import kotlinx.android.synthetic.main.fragment_drawer_setting.*

class DrawerEmergencySetting : AppCompatDialogFragment() {
    lateinit var myView:View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        myView = activity!!.layoutInflater.inflate(
            R.layout.fragment_drawer_emergency_setting,
            LinearLayout(activity),
            false
        )

        // build dialog
        var builder:AlertDialog.Builder = AlertDialog.Builder(activity!!)

        builder.setView(myView)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

        return builder.create()

//        return super.onCreateDialog(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //닫기 버튼 클릭 -> 다이얼로그 종료
        emergencySettingCloseBtn.setOnClickListener {
            this.dialog?.dismiss()
            //this.dialog?.cancel()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return myView
    }
}