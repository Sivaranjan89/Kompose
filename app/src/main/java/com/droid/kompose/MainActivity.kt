package com.droid.kompose

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.droidlib.kompose.toast.UIToast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pin_view.observeEnteredOTP {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        button_view.onButtonClicked {
            UIToast.Build(this, "Button Clicked").backgroundColor(Color.LTGRAY)
                .textColor(Color.BLACK)
                .textSize(15F)
                .makeBold(true)
                .roundCornered(50F)
                .renderStroke(1F, Color.BLACK)
                .renderIcon(R.mipmap.ic_launcher_round, 30F)
                .duration(Toast.LENGTH_LONG)
                .build()
        }

        button_view.onImageClicked {
            Toast.makeText(this, "Image Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
