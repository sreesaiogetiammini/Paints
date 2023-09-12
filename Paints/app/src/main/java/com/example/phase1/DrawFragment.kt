package com.example.phase1

import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.phase1.databinding.FragmentDrawBinding
import org.xmlpull.v1.XmlPullParser


@RequiresApi(Build.VERSION_CODES.O)
class DrawFragment : Fragment() {

    private lateinit var binding: FragmentDrawBinding
    private val viewModel: PaintViewModel by activityViewModels()
    var fabVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDrawBinding.inflate(inflater)

        // Set up the onTouchEvent for your custom view
        binding.paintView.setOnTouchListener { _, event ->
            binding.paintView.onTouchEvent(event)
            true // Consume the touch event
        }

        // Observe drawing paths changes and update ViewModel

        viewModel.getDrawingPaths().observe(viewLifecycleOwner) { drawingPaths ->
            binding.paintView.clearPaths()
            for ((path, brush) in drawingPaths) {
                binding.paintView.setDrawingPath(path, brush)
            }
        }
        binding.penSelector.setOnClickListener {
            Toast.makeText(getActivity(), "Pen Clicked", Toast.LENGTH_SHORT).show();
            if (binding.seekLayout.visibility === View.VISIBLE) {
                binding.seekLayout.visibility = View.GONE
            }
            else {
                binding.seekLayout.visibility = View.VISIBLE
            }
        }


        binding.colorSelector.setOnClickListener {
            Toast.makeText(getActivity(), "Colors Clicked", Toast.LENGTH_SHORT).show();
            if (binding.colorLayout.visibility === View.VISIBLE) {
                binding.colorLayout.visibility = View.GONE
            }
            else {
                binding.colorLayout.visibility = View.VISIBLE
            }
        }



        binding.color1.setOnClickListener{
            binding.paintView.changeColor(Color.BLACK)
        }
        binding.color2.setOnClickListener{
            binding.paintView.changeColor(Color.BLUE)
        }
        binding.color3.setOnClickListener{
            binding.paintView.changeColor(Color.MAGENTA)
        }
        binding.color4.setOnClickListener{
            binding.paintView.changeColor(Color.WHITE)
        }

        binding.color5.setOnClickListener{
            binding.paintView.changeColor(Color.CYAN)
        }



        binding.erasorSelector.setOnClickListener {
            binding.paintView.changeStrokeWidth(50f)
        }

        binding.textSelector.setOnClickListener {
            Toast.makeText(getActivity(), "Text Clicked", Toast.LENGTH_SHORT).show();
            if (binding.seekLayout.visibility === View.VISIBLE) {
                binding.seekLayout.visibility = View.GONE
            }
            else {
                binding.seekLayout.visibility = View.VISIBLE
            }
        }



        binding.idFABNew.setOnClickListener{
            binding.paintView.clearPaths()

        }

        binding.idFABClearAll.setOnClickListener{
            binding.paintView.clearPaths()
        }



        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update the TextView with the current progress value
                binding.textWatcher.text = progress.toString()
                binding.paintView.changeStrokeWidth(progress.toFloat())

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.idFABAdd.setOnClickListener{
            ClickPlusBtn()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setDrawingPaths(binding.paintView.getDrawingPaths())
        //viewModel.setPaintBrushColor(binding.paintView.getColorDrawingPaths())
    }


    private fun ClickPlusBtn(){
        if (!fabVisible) {

            // if its false we are displaying home fab
            // and settings fab by changing their
            // visibility to visible.
            binding.idFABShare.show()
            binding.idFABNew.show()
            binding.idFABClearAll.show()

            // on below line we are setting
            // their visibility to visible.
            binding.idFABShare.visibility = View.VISIBLE
            binding.idFABNew.visibility = View.VISIBLE
            binding.idFABClearAll.visibility = View.VISIBLE

            // on below line we are checking image icon of add fab
           // binding.idFABAdd.setImageDrawable(resources.getDrawable(R.drawable.ic_close))

            // on below line we are changing
            // fab visible to true
            fabVisible = true
        } else {

            // if the condition is true then we
            // are hiding home and settings fab
            binding.idFABShare.hide()
            binding.idFABNew.hide()
            binding.idFABClearAll.hide()

            // on below line we are changing the
            // visibility of home and settings fab
            binding.idFABShare.visibility = View.GONE
            binding.idFABNew.visibility = View.GONE
            binding.idFABClearAll.visibility = View.GONE

            // on below line we are changing image source for add fab
            //addFAB.setImageDrawable(resources.getDrawable(R.drawable.ic_add))

            // on below line we are changing
            // fab visible to false.
            fabVisible = false
        }
    }




}


