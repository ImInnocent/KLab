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
import androidx.recyclerview.widget.ItemTouchHelper
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

    // var array = ArrayList<emoTrashData>()
    lateinit var myDBHelper: MyDBHelper
    lateinit var myAdapter:emoTrashAdapter



    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //init() -> view를 인자로 받아야 하므로 createView 이후로 옮기겠음.
        //putData()
        //임시 Data -> filescan으로 수정 예정
        //emoTrashData title -> emoTrashData contents 의 short ver 분리되어있다고 생각했는데 이것도 수정 필요.
        putData()
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emo_trash, container, false)
        init(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        myDBHelper.ET_getAllRecord()    // 아이템 갱신 알림
        myAdapter.items = myDBHelper.ETArray
        myAdapter.notifyDataSetChanged()
    }

    private fun init(view :View) {
        //val main_activity  = activity as MainActivity
        myDBHelper = MyDBHelper.getInstance()!!   // DBHelpter 객체 획득

        // 리사이클러뷰에 레이아웃 매니저 연결하고, 어댑터 선언해서 연결하고, 클릭리스너 설정하기
        view.emoTrashRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        myAdapter = emoTrashAdapter(myDBHelper.ETArray)
        myAdapter.itemClickListener = object:emoTrashAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: emoTrashAdapter.MyemoTrashViewHolder,
                view: View,
                data: emoTrashData,
                position: Int
            ) {
                //Toast.makeText(context, "내용까지 보여주는 팝업 뷰 생성 필요", Toast.LENGTH_SHORT).show()
                // DB에 저장해둔 내용(ETArray)의 해당 포지션 출력
                EmoTrashItemFragment(myAdapter.items[position]).show(fragmentManager!!, "emotrashitem")
            }

        }
        view.emoTrashRecyclerView.adapter = myAdapter

        view.addBtn.setOnClickListener {
            //Toast.makeText(context,"감정 일기 추가", Toast.LENGTH_SHORT).show()
            EmoTrashEditFragment(myDBHelper, myAdapter).show( fragmentManager!!, "editdialog")
        }

        view.deleteBtn.setOnClickListener {
            //Toast.makeText(context, "감정 쓰레기통 삭제 화면", Toast.LENGTH_SHORT).show()
            EmoTrashDeletedFragment(myDBHelper).show(fragmentManager!!, "deletedialog")
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
                val size = myAdapter.itemCount - 1     // 데이터의 역순으로 리사이클러 뷰에 표시되므로
                myDBHelper.ET_updateData(myAdapter.items[size - viewHolder.adapterPosition])
                myDBHelper.ET_getAllRecord()    // 데이터 갱신
                myAdapter.notifyDataSetChanged()
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(view.emoTrashRecyclerView)
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
