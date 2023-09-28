package com.example.phase1

import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.phase1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        binding.fragmentContainerView.getFragment<ClickFragment>().setButtonFunction {
            val drawFragment = DrawFragment()
            val transaction = this.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerView, drawFragment, "draw_tag")
            transaction.addToBackStack(null)
            transaction.commit()
        }
        setContentView(binding.root)



    }
}