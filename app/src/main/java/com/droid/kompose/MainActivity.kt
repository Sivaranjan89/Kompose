package com.droid.kompose

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pin_view.observeEnteredOTP {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        button_view.onButtonClicked {
            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()
        }

        button_view.onImageClicked {
            Toast.makeText(this, "Image Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
