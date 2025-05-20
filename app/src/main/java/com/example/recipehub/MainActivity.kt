package com.example.recipehub

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipehub.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        binding.ivLogo.startAnimation(slideDown)
        binding.llName.startAnimation(slideUp)

        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(2000)
                    startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                    finish()
                } catch (e: InterruptedException) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        thread.start()

    }
}