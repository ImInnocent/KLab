package com.example.idealmood

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_emo_trash.*
import kotlinx.android.synthetic.main.fragment_emo_trash.view.*
import kotlinx.android.synthetic.main.fragment_emo_trash_edit.*
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
        array.add(emoTrashData("너무 억울하다..", "20.1.3", "내용1"))
        array.add(emoTrashData("화가 난다", "20.3.7", "내용2"))
        array.add(emoTrashData("이해가 안된다", "20.5.17", "내용3"))

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

            Toast.makeText(context,"감정 일기 추가", Toast.LENGTH_SHORT).show()
            //if(!EmoTrashEditFragment().isAdded)
            EmoTrashEditFragment().show( fragmentManager!!, "dialog")



        }

        deleteBtn.setOnClickListener {


            Toast.makeText(context, "감정 쓰레기통 삭제 화면", Toast.LENGTH_SHORT).show()
        }
    }


     fun putData() {
    }

    public fun addArray(title:String){
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        EmoTrashFragment().array.add(emoTrashData(title,sdf.format(
            Calendar.getInstance().time), "null" ))
    }

}
