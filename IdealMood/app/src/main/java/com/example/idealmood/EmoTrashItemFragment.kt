package com.example.idealmood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.fragment_emo_trash_item.*

/**
 * A simple [Fragment] subclass.
 */

//item_contents
//item_day_written
class EmoTrashItemFragment(val data:emoTrashData) : AppCompatDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emo_trash_item, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        item_contents.text = data.title
        item_day_written.text = data.date

        emoItemCloseBtn.setOnClickListener {
            this.dialog?.dismiss()
        }

    }
}
