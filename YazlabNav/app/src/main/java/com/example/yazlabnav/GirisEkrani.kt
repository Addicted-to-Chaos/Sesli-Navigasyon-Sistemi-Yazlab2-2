package com.example.yazlabnav

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout



class GirisEkrani : AppCompatActivity() {

    private lateinit var girisButton: Button
    private lateinit var isimText: TextView
    private lateinit var isimInput: EditText
    private lateinit var textInputLayout: TextInputLayout

    val textToSpeech:TextToSpeech? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_giris_ekrani)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPref=getSharedPreferences("myPref", MODE_PRIVATE)
        val editor=sharedPref.edit()

        girisButton=findViewById(R.id.girisbutton)
        isimText=findViewById(R.id.isimText)
        isimInput=findViewById(R.id.textInputİsim)
        textInputLayout=findViewById(R.id.textInputLayoutIsim)

        if(sharedPref.contains("name") && sharedPref.getString("name",null)?.length!=0){
            isimInput.isEnabled=false
            isimInput.visibility = View.INVISIBLE
            textInputLayout.visibility=View.INVISIBLE

            isimText.isEnabled=true
            isimText.visibility = View.VISIBLE;
            val name: String? = sharedPref.getString("name",null)
            isimText.text="Hoşgeldin ${name}"
            girisButton.isEnabled=true
        }
        else{
            isimInput.isEnabled=true
            isimInput.visibility = View.VISIBLE;

            isimText.isEnabled=false
            isimText.visibility = View.INVISIBLE;


            textToSpeech?.speak("Hoşgeldin "+ sharedPref.getString("name",null), TextToSpeech.QUEUE_FLUSH, null, null)
            girisButton.isEnabled=true
        }
        girisButton.setOnClickListener {
            editor.apply{
                putString("name", isimInput.text.toString())
                apply()
            }
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}