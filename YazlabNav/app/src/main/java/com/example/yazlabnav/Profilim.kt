package com.example.yazlabnav

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yazlabnav.R
import java.util.Locale

class Profilim : AppCompatActivity() {

    val adText: TextView?= findViewById(R.id.adText)
    val adGuncellemeButton: Button?= findViewById(R.id.adGuncelle)
    var textToSpeech:TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profilim)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref=getSharedPreferences("myPref", MODE_PRIVATE)
        val editor=sharedPref.edit()

            adText?.text=sharedPref.getString("name",null)

        adGuncellemeButton?.setOnClickListener {
            editor.apply{
                putString("name", adText?.text.toString())
                apply()
            }
            textToSpeech?.speak("Kullanıcı adı başarıyla güncellendi.", TextToSpeech.QUEUE_FLUSH, null, null)
            Toast.makeText(this, "Kullanıcı adı başarıyla güncellendi.", Toast.LENGTH_SHORT).show()

        }

    }

    fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Dil değiştirmek isterseniz alttaki tr yi başka dil koduyla değiştirin //Kaan
            val result = textToSpeech?.setLanguage(Locale("tr"))

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Toast.makeText(this, "Bu dil desteklenmiyor.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "TextToSpeech başlatılamadı.", Toast.LENGTH_SHORT).show()
        }
    }


}