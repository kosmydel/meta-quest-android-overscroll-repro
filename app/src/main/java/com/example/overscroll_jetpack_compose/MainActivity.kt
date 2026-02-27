package com.example.overscroll_jetpack_compose

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val lastClickedLabel = TextView(this).apply {
            text = "Last clicked item ID: —"
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(48, 32, 48, 32)
            setBackgroundColor(0xFF1E1E1E.toInt())
            setTextColor(0xFFFFFFFF.toInt())
        }

        val recyclerView = ThumbstickAwareRecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = SimpleTextAdapter(
                items = List(15) { i -> "Item #${i + 1}" },
                onItemClick = { position ->
                    lastClickedLabel.text = "Last clicked item ID: $position"
                },
            )
            overScrollMode = View.OVER_SCROLL_ALWAYS
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(lastClickedLabel, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ))
            addView(recyclerView, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f,
            ))
        }

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }

        setContentView(root)
    }
}