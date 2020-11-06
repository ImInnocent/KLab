package com.example.idealmood

import android.content.Intent
import android.net.Uri
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

    val solutionArr = arrayOf<Int>(
            R.string.solution_meditation_title,
            R.string.solution_deep_breath_title,
            R.string.solution_exercise_title,
            R.string.solution_dance_title,
            R.string.solution_asmr_title,
            R.string.solution_rain_title,
            R.string.solution_ocean_title,
            R.string.solution_firewall_title,
            R.string.solution_classic_title)  // data 추가 예정
    val solList = arrayOf<AppCompatActivity>(MeditationSolution(), DeepBreathSolution(), AppCompatActivity(),
                                            AppCompatActivity(), AppCompatActivity(), RainySoundSolution(), WaveSoundSolution(),
                                            FirewallSoundSolution(), AppCompatActivity())
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
                madapter = MyAdapter(solutionTextIdsToStrings())
                adapter = madapter
            }
        }
        init()
        return view
    }

    private fun solutionTextIdsToStrings(): Array<String> {
        return Array<String>(solutionArr.size) { getString(solutionArr[it]) }
    }

    private fun init() {
        madapter.itemClickListener = object :MyAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: MyAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                if (position == 2) {    // 하드코딩인가? 그냥 uri를 배열에다 넣고 꺼내쓸까?
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=exercise"))
                    startActivity(i)
                }
                else if (position == 3) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=kpop+dance+tutorial"))
                    startActivity(i)
                }
                else if (position == 4) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=asmr"))
                    startActivity(i)
                }
                else if (position == 8) {
                    var i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=classical+music"))
                    startActivity(i)
                }
                else {
                    val i = Intent(requireContext(), solList[position]::class.java)
                    startActivity(i)
                }
            }

        }
    }
}
