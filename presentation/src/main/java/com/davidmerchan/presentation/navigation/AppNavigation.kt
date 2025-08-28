package com.davidmerchan.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.davidmerchan.presentation.feature.detail.DetailScreen
import com.davidmerchan.presentation.feature.home.HomeScreen
import com.davidmerchan.presentation.feature.login.LoginScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LoginRoute
    ) {
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
