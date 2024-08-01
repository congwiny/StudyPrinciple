package com.congwiny.principle.jetpack.viewmodel

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.congwiny.principle.R
import kotlin.random.Random

class CustomActivity : AppCompatActivity() {

    companion object {
        const val TAG = "CustomActivity"
    }

    private lateinit var contentTv: TextView
    private lateinit var button: Button
    private val customViewModel by viewModels<CustomViewModel>()
    private var isPortrait = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_custom)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        contentTv = findViewById(R.id.content_tv)
        contentTv.text = "当前数值：${customViewModel.data.value}"

        button = findViewById(R.id.button)
        button.setOnClickListener {
            customViewModel.data.value = Random.nextInt()
            contentTv.text = "当前数值：${customViewModel.data.value}"
        }
        val orientation = resources.configuration.orientation

        isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT
        val switchScreen = findViewById<Button>(R.id.switch_screen)
        switchScreen.setOnClickListener {
            if (isPortrait) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            }
            isPortrait = !isPortrait
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 处理横屏切换
            Log.e(TAG, "横屏")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 处理竖屏切换
            Log.e(TAG, "竖屏")
        }
    }
}