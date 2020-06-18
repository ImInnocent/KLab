package com.example.idealmood


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_emo_trash_edit.*
import java.io.FileOutputStream
import java.io.PrintStream
import java.text.SimpleDateFormat
import java.util.*


class EmoTrashEditFragment : AppCompatDialogFragment() {

    lateinit var myView:View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_emo_trash_edit, container, false)
        return myView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        myView = activity!!.layoutInflater.inflate(R.layout.fragment_emo_trash_edit, LinearLayout(activity), false)
        //myView = context!!.layoutInflater.inflate(R.layout.fragment_emo_trash_edit, null)


        // dialog 빌드
        return AlertDialog.Builder(activity!!)
            .setView(myView)
            .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
            .setPositiveButton("SUBMIT", DialogInterface.OnClickListener { dialog, which ->

                writeFile(emoTrashEdit.text.toString())
            })
            .create()



    }

    private fun writeFile(title:String) {
        val output = PrintStream(context?.openFileOutput("trashData.txt", Context.MODE_APPEND))

        val sdf = SimpleDateFormat("yy.MM.dd", Locale.KOREAN)

        output.println(title)
        output.println(
            sdf.format(
                Calendar.getInstance().time
            )
        )
        output.println("null")
        output.close()


        //감정 쓰레기통 화면 refresh기능 추가 필요


    }

}
