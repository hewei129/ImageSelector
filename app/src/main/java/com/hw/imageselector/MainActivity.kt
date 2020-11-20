package com.hw.imageselector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.hw.selpic_lib.SelectPictureController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SelectPictureController.OnPictureSelectedListener {
    val selectPictureController  by lazy { SelectPictureController(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectPictureController.mOnPictureSelectedListener = this
        tv_sel.setOnClickListener {
            selectPictureController.showPopWindows(true)
        }
    }

    override fun onPictureSelected(url: String?, filePath: String?) {

    }

    override fun onPictureSelected(filePath: String?) {
        Toast.makeText(this, "filePath:$filePath", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectPictureController.onActivityResult(requestCode, resultCode, data)
    }

}