package com.davidmerchan.presentation

import androidx.compose.runtime.Composable
import com.davidmerchan.presentation.navigation.AppNavigation
import com.davidmerchan.presentation.theme.LeagueMobileChallengeTheme

@Composable
fun LeagueMobileChallengeApp() {
    LeagueMobileChallengeTheme {
        AppNavigation()
    }
}
