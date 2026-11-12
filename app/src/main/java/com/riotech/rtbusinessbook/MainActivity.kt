package com.riotech.rtbusinessbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.riotech.rtbusinessbook.ui.MainViewModel
import com.riotech.rtbusinessbook.ui.RtApp
import com.riotech.rtbusinessbook.ui.RtTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RtTheme {
                val vm: MainViewModel = viewModel()
                RtApp(vm)
            }
        }
    }
}
