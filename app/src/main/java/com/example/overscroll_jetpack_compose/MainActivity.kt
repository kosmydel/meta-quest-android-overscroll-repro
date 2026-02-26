package com.example.overscroll_jetpack_compose

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Simple full-screen RecyclerView (no XML)
        val recyclerView = RecyclerView(this).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = SimpleTextAdapter(items = List(15) { i -> "Item #${i + 1}" })

            // Enable overscroll explicitly
            overScrollMode = View.OVER_SCROLL_ALWAYS
        }

        // Optional: handle edge-to-edge padding (keeps list away from status/nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, sysBars.bottom)
            insets
        }

        setContentView(recyclerView)
    }
}