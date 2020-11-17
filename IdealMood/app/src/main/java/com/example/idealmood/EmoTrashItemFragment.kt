package com.example.idealmood

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_emo_trash_item.*

/**
 * A simple [Fragment] subclass.
 */

//item_contents
//item_day_written
class EmoTrashItemFragment(val data:emoTrashData) : AppCompatDialogFragment() {



    lateinit var myView:View



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        myView = activity!!.layoutInflater.inflate(
            R.layout.fragment_emo_trash_item,
            LinearLayout(activity),
            false
        )



        //build dialog
        var builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)

        builder.setView(myView)


        return builder.create()

        /*
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })*/
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        item_contents.text = data.title
        item_day_written.text = data.date

        emoItemCloseBtn.setOnClickListener {
            this.dialog?.dismiss()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return myView
    }



}
