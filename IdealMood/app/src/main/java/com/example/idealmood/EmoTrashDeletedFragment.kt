package com.example.idealmood

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_emo_trash.*
import kotlinx.android.synthetic.main.fragment_emo_trash_deleted.*
import kotlinx.android.synthetic.main.fragment_emo_trash_edit.*
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class EmoTrashDeletedFragment : AppCompatDialogFragment() {

    lateinit var myView: View
    var array = ArrayList<emoTrashData>()
    lateinit var adapter:emoTrashAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        emoTrashDelBtn.setOnClickListener {
            Toast.makeText(context, "뒤로가기 버튼 누른거랑 같은 효과 추가", Toast.LENGTH_SHORT).show()
        }


        putData()
        emoTrashDelrcy.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        /*array.add(emoTrashData("일기1", "20.6.14", "null"))
        array.add(emoTrashData("일기2", "20.6.15", "null"))
        array.add(emoTrashData("일기3", "20.6.16", "null"))*/
        adapter = emoTrashAdapter(array)
        /*adapter.itemClickListener = object:emoTrashAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: emoTrashAdapter.MyemoTrashViewHolder,
                view: View,
                data: emoTrashData,
                position: Int
            ) {
                Toast.makeText(context, "내용까지 보여주는 팝업 뷰 생성 필요", Toast.LENGTH_SHORT).show()
            }

        }*/
        emoTrashDelrcy.adapter = adapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_emo_trash_edit, container, false)



        return myView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        myView = activity!!.layoutInflater.inflate(
            R.layout.fragment_emo_trash_deleted,
            LinearLayout(activity),
            false
        )



        // dialog 빌드
        return AlertDialog.Builder(activity!!)
            .setView(myView)
            .create()



    }


    fun putData() {
        val file = File(context!!.filesDir, "trashData.txt")
        if(file.exists()){
            val scan = Scanner(context?.openFileInput("trashData.txt"))
            readFileScan(scan)

        }else{
            context!!.openFileOutput("trashData.txt", Context.MODE_APPEND)
            val scan = Scanner(context?.openFileInput("trashData.txt"))
            readFileScan(scan)
        }


    }

    fun readFileScan(scan : Scanner) {
        while(scan.hasNextLine()){

            val title:String = scan.nextLine()
            val date:String = scan.nextLine()
            val content:String = scan.nextLine()
            array.add(emoTrashData(title, date, content))

        }
        scan.close()
    }

}