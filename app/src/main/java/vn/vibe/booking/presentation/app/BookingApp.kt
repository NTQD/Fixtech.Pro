package vn.vibe.booking.presentation.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import vn.vibe.booking.core.data.TokenManager
import vn.vibe.booking.presentation.auth.AuthViewModel
import vn.vibe.booking.presentation.auth.LoginScreen
import vn.vibe.booking.presentation.auth.RegisterScreen
import vn.vibe.booking.presentation.home.HomeScreen
import vn.vibe.booking.presentation.home.HomeViewModel
import vn.vibe.booking.presentation.theme.BookingTheme
import kotlinx.coroutines.delay

private object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun BookingApp(
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val token by tokenManager.token.collectAsStateWithLifecycle(initialValue = null)

    BookingTheme {
        Surface(modifier = modifier) {
            Scaffold { innerPadding ->
                Box(modifier = Modifier) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.SPLASH
                    ) {
                        composable(Routes.SPLASH) {
                            LaunchedEffect(token) {
                                delay(1000) // Mock splash delay
                                val dest = if (token.isNullOrBlank()) Routes.LOGIN else Routes.HOME
                                navController.navigate(dest) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            }
                            // Splash UI
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                androidx.compose.material3.Text(
                                    text = androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.app_name),
                                    style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }

                        composable(Routes.LOGIN) {
                            val authViewModel: AuthViewModel = hiltViewModel()
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
                            val authViewModel: AuthViewModel = hiltViewModel()
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.REGISTER) { inclusive = true }
                                    }
                                },
                                contentPadding = innerPadding
                            )
                        }

                        composable(Routes.HOME) {
                            val homeViewModel: HomeViewModel = hiltViewModel()
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
