package com.example.yazlabnav

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.ArrayList
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        val mapeGecisButon: Button?= findViewById(R.id.denemee)
        val profileGecisButon: Button?= findViewById(R.id.profilButton)


        mapeGecisButon?.setOnClickListener {
            val intent=Intent(this, Harita::class.java)
            startActivity(intent)
        }
        profileGecisButon?.setOnClickListener {
            val intent=Intent(this, Profilim::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val geriTusMain = findViewById<ImageButton>(R.id.geritusu_menu)
        geriTusMain.setOnClickListener {
            geriButtonMain()
        }

    }
    private fun geriButtonMain() {
        val geriTusMain = Intent(this, GirisEkrani::class.java)
        startActivity(geriTusMain)
    }


}
