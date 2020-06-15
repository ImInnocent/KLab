package com.example.idealmood

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_solution.*

/**
 * A simple [Fragment] subclass.
 */
class SolutionFragment : Fragment() {

    val solutionArr = arrayOf<String>("명상", "심호흡")  // data 추가 예정
    val solList = arrayOf<AppCompatActivity>(MeditationSolution(), DeepBreathSolution())
    lateinit var madapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_solution, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                view.layoutManager = LinearLayoutManager(context)
                madapter = MyAdapter(solutionArr)
                adapter = madapter
            }
        }
        init()
        return view
    }

    private fun init() {
        madapter.itemClickListener = object :MyAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                val i = Intent(requireContext(), solList[position]::class.java)
                startActivity(i)
            }

        }
    }
}
