package com.example.clinicaseprice

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val imgLogo = findViewById<ImageView>(R.id.imgLogo)

        val animation = AnimationUtils.loadAnimation(
            this,
            R.anim.splash_anim
        )

        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {

                val intent = Intent(
                    this@MainActivity,
                    LoginActivity::class.java
                )

                startActivity(intent)

                // Evita volver al Splash con el botón Atrás
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })

        imgLogo.startAnimation(animation)
    }
}