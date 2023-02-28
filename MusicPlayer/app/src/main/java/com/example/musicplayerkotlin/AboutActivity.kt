package com.example.musicplayerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.musicplayerkotlin.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "RZ Music Player | About Us"

        binding.aboutus.text = "Developed By : Raj Zadafiya \n\n Contact : zadafiyaraj4749@gmail.com " +
                "\n\n If you have an problem in app then i will help you."

    }
}