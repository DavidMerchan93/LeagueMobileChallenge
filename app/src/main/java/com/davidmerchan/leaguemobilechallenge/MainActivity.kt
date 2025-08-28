package com.davidmerchan.leaguemobilechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.davidmerchan.presentation.feature.login.LoginScreen
import com.davidmerchan.presentation.theme.LeagueMobileChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeagueMobileChallengeTheme {
                LoginScreen()
            }
        }
    }
}
