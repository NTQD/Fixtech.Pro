package vn.vibe.booking.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    contentPadding: PaddingValues
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    var phone by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(state.message) {
        if (!state.message.isNullOrBlank()) {
            onLoginSuccess()
            viewModel.clearLoginTransientState()
        }
    }

    val background = Brush.linearGradient(
        colors = listOf(Color(0xFF050816), Color(0xFF0F172A), Color(0xFF1E1B4B))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(contentPadding)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)),
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Chào mừng trở lại", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Đăng nhập để quản lý lịch sửa, theo dõi booking và xem thông tin tài khoản.", color = Color(0xFF94A3B8))

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        localError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Số điện thoại") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        localError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mật khẩu") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                ErrorText(localError ?: state.error)

                Button(
                    onClick = {
                        if (phone.text.isBlank() || password.text.isBlank()) {
                            localError = "Vui lòng nhập đầy đủ số điện thoại và mật khẩu"
                        } else {
                            localError = null
                            viewModel.login(phone.text.trim(), password.text.trim(), onSuccess = {})
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Đăng nhập")
                    }
                }

                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Chưa có tài khoản? Đăng ký ngay", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ErrorText(message: String?) {
    if (!message.isNullOrBlank()) {
        Text(message, color = Color(0xFFFCA5A5), style = MaterialTheme.typography.bodyMedium)
    }
}
