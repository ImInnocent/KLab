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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_emo_trash.*
import kotlinx.android.synthetic.main.fragment_emo_trash.view.*
import kotlinx.android.synthetic.main.fragment_emo_trash_deleted.*
import kotlinx.android.synthetic.main.fragment_emo_trash_deleted.view.*
import kotlinx.android.synthetic.main.fragment_emo_trash_edit.*
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class EmoTrashDeletedFragment(val myDBHelper: MyDBHelper) : AppCompatDialogFragment() {

    lateinit var myView: View
    lateinit var myAdapter2:emoTrashAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        emoTrashDelBtn.setOnClickListener { // X버튼 눌렀을 때
            //Toast.makeText(context, "뒤로가기 버튼 누른거랑 같은 효과 추가", Toast.LENGTH_SHORT).show()
            this.dialog?.cancel()
        }

        //putData()
        emoTrashDelRcy.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        myAdapter2 = emoTrashAdapter(myDBHelper.ETArray_Del)

        emoTrashDelRcy.adapter = myAdapter2

        // 클릭해서 내용 보여주기
        myAdapter2.itemClickListener = object :emoTrashAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: emoTrashAdapter.MyemoTrashViewHolder,
                view: View,
                data: emoTrashData,
                position: Int
            ) {
                EmoTrashItemFragment(myAdapter2.items[position]).show(fragmentManager!!, "emotrashitem")
            }

        }
        
        // 스와이프해서 지우기
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false    // 위치 이동 기능은 활성화 X
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val size = myAdapter2.itemCount - 1     // 데이터의 역순으로 리사이클러 뷰에 표시되므로
                myDBHelper.ET_deleteData(myAdapter2.items[size - viewHolder.adapterPosition])
                myDBHelper.ET_getAllRecord()    // 데이터 갱신
                myAdapter2.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(emoTrashDelRcy)
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


    /*fun putData() {
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
    }*/

}