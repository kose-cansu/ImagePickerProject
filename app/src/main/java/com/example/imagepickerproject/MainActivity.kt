package com.example.imagepickerproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.imagepickerproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var imagePicker: ImagePicker
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePicker = ImagePicker(this) { uri ->
            binding.imageView.setImageURI(uri)
        }

        binding.setImageButton.setOnClickListener {
            imagePicker.show()
        }
    }
}