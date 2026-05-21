package vn.vibe.booking.presentation.app

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vn.vibe.booking.core.di.AppContainer
import vn.vibe.booking.presentation.auth.AuthViewModel
import vn.vibe.booking.presentation.auth.LoginScreen
import vn.vibe.booking.presentation.auth.RegisterScreen
import vn.vibe.booking.presentation.home.HomeScreen
import vn.vibe.booking.presentation.home.HomeViewModel
import vn.vibe.booking.presentation.theme.BookingTheme

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun BookingApp(
    appContainer: AppContainer,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    val navController = rememberNavController()
    val token by appContainer.observeAuthStateUseCase().collectAsStateWithLifecycle(initialValue = null)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(
            loginUseCase = appContainer.loginUseCase,
            registerUseCase = appContainer.registerUseCase,
            tokenRepository = appContainer.tokenRepository
        )
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            userRepository = appContainer.userRepository,
            tokenRepository = appContainer.tokenRepository,
            homeRepository = appContainer.homeRepository
        )
    )

    BookingTheme {
        Surface(modifier = modifier) {
            Scaffold { innerPadding ->
                Box(modifier = androidx.compose.ui.Modifier) {
                    NavHost(
                        navController = navController,
                        startDestination = if (token.isNullOrBlank()) Routes.LOGIN else Routes.HOME
                    ) {
                        composable(Routes.LOGIN) {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                                onLoginSuccess = {
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                },
                                contentPadding = innerPadding
                            )
                        }

                        composable(Routes.REGISTER) {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onBackToLogin = { navController.popBackStack() },
                                contentPadding = innerPadding
                            )
                        }

                        composable(Routes.HOME) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                token = token,
                                onLogout = {
                                    homeViewModel.logout {
                                        navController.navigate(Routes.LOGIN) {
                                            popUpTo(Routes.HOME) { inclusive = true }
                                        }
                                    }
                                },
                                contentPadding = innerPadding
                            )
                        }
                    }
                }
            }
        }
    }
}
