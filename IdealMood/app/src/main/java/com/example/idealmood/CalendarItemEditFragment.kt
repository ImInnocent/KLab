package com.example.idealmood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_calendar_item_edit.*



class CalendarItemEditFragment : AppCompatDialogFragment() {
    // TODO: Rename and change types of parameters

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        worst.setOnClickListener {
            //Toast.makeText(activity, "최악", Toast.LENGTH_SHORT).show()
            /*val result = 1
            setResult("requestKey", bundleOf("bundleKey" to result))*/
            this.dialog?.cancel()
        }
         bad.setOnClickListener {
             this.dialog?.cancel()
         }
         soso.setOnClickListener {
            this.dialog?.cancel()
         }
         good.setOnClickListener {
              this.dialog?.cancel()
         }
         best.setOnClickListener {
             this.dialog?.cancel()
         }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_item_edit, container, false)
    }


}
