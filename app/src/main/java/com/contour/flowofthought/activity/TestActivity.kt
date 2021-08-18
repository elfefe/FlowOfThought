package com.contour.flowofthought.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.contour.flowofthought.custom.FotEditText
import com.contour.flowofthought.custom.FotTextField.fotTextField

class TestActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    fotTextField()
                }
            }
        )
    }
}