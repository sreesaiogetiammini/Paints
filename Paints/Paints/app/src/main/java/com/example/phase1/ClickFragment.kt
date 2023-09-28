package com.example.phase1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.phase1.databinding.FragmentClickBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClickFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClickFragment : Fragment() {


    private var buttonFunction : () -> Unit = {}

    fun setButtonFunction(newFunc: () -> Unit) {
        buttonFunction = newFunc
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentClickBinding.inflate(inflater, container, false)
        val viewModel : PaintViewModel by activityViewModels()
        binding.CreatePaintBtn.setOnClickListener {
            buttonFunction()
        }
        return binding.root
    }


}