package com.maveri.figma.main.widget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maveri.figma.databinding.ActivityFullscreenImageBinding
import com.squareup.picasso.Picasso

class FullscreenImageActivity : AppCompatActivity(){

    private lateinit var binding: ActivityFullscreenImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenImageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Picasso.with(this)
            .load(intent.getStringExtra("imageUrl").toString())
            .into(binding.fullScreenImage)



    }
}