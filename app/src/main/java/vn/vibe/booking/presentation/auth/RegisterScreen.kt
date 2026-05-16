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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit,
    contentPadding: PaddingValues
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(state.message) {
        if (!state.message.isNullOrBlank()) {
            onBackToLogin()
            viewModel.clearRegisterTransientState()
        }
    }

    val bg = Brush.linearGradient(colors = listOf(Color(0xFF050816), Color(0xFF111827), Color(0xFF190F2D)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(contentPadding)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Đăng ký", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Tạo tài khoản mới", color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Họ và tên", color = Color.White) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Số điện thoại", color = Color.White) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Mật khẩu", color = Color.White) },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (localError != null) Text(localError!!, color = Color(0xFFFCA5A5))
                if (state.error != null) Text(state.error!!, color = Color(0xFFFCA5A5))
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || password.isBlank()) {
                            localError = "Vui lòng nhập đầy đủ thông tin"
                        } else {
                            localError = null
                            viewModel.register(name.trim(), phone.trim(), password.trim(), onSuccess = {})
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
                ) {
                    Text("Đăng ký", color = Color.White)
                }
                if (state.isLoading) CircularProgressIndicator()
                Button(
                    onClick = onBackToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827))
                ) {
                    Text("Đã có tài khoản? Đăng nhập", color = Color.White)
                }
            }
        }
    }
}
