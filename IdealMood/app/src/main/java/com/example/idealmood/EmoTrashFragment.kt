package com.example.idealmood

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_emo_trash.*
import kotlinx.android.synthetic.main.fragment_emo_trash.view.*
import kotlinx.android.synthetic.main.fragment_emo_trash_edit.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class EmoTrashFragment : Fragment() {

    var array = ArrayList<emoTrashData>()
    lateinit var adapter:emoTrashAdapter



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        init()

        //putData()
        //임시 Data -> filescan으로 수정 예정
        //emoTrashData title -> emoTrashData contents 의 short ver 분리되어있다고 생각했는데 이것도 수정 필요.


        putData()
        emoTrashRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = emoTrashAdapter(array)
        adapter.itemClickListener = object:emoTrashAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: emoTrashAdapter.MyemoTrashViewHolder,
                view: View,
                data: emoTrashData,
                position: Int
            ) {
                Toast.makeText(context, "내용까지 보여주는 팝업 뷰 생성 필요", Toast.LENGTH_SHORT).show()
            }

        }
        emoTrashRecyclerView?.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emo_trash, container, false)



        return view
    }

    private fun init() {

        val main_activity  = activity as MainActivity



        addBtn.setOnClickListener {

            //Toast.makeText(context,"감정 일기 추가", Toast.LENGTH_SHORT).show()
            EmoTrashEditFragment().show( fragmentManager!!, "editdialog")



        }

        deleteBtn.setOnClickListener {


            //Toast.makeText(context, "감정 쓰레기통 삭제 화면", Toast.LENGTH_SHORT).show()
            EmoTrashDeletedFragment().show(fragmentManager!!, "deletedialog")
        }
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
