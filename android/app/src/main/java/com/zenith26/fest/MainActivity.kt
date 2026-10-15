package com.zenith26.fest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.zenith26.fest.ui.EventScreen
import com.zenith26.fest.ui.theme.ZenithTheme
import com.zenith26.fest.viewmodel.EventViewModel

/**
 * MainActivity serving as the single-activity entry point for the Zenith '26 Fest mobile application.
 */
class MainActivity : ComponentActivity() {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZenithTheme {
                EventScreen(viewModel = viewModel)
            }
        }
    }
}
