package com.davidmerchan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.davidmerchan.presentation.feature.auth.AuthContract
import com.davidmerchan.presentation.feature.auth.AuthState
import com.davidmerchan.presentation.feature.auth.AuthViewModel
import com.davidmerchan.presentation.feature.auth.SplashScreen
import com.davidmerchan.presentation.feature.detail.DetailScreen
import com.davidmerchan.presentation.feature.home.HomeScreen
import com.davidmerchan.presentation.feature.login.LoginScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val state by authViewModel.uiState.collectAsState()

    LaunchedEffect(state.authState) {
        when (state.authState) {
            AuthState.AUTHENTICATED -> {
                navController.navigate(HomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            }

            AuthState.UNAUTHENTICATED -> {
                navController.navigate(LoginRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            }

            AuthState.CHECKING -> { /* Stay in splash */
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        composable<SplashRoute> {
            SplashScreen()
        }
        composable<LoginRoute> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onPostClick = { postId ->
                    navController.navigate(DetailRoute(postId))
                },
                onLogoutClick = {
                    authViewModel.handleEvent(AuthContract.Event.Logout)
                }
            )
        }

        composable<DetailRoute> { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<DetailRoute>()
            DetailScreen(
                userId = detailRoute.userId,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
