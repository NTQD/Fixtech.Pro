package vn.vibe.booking.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.layout.PaddingValues

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    contentPadding: PaddingValues
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var localError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(state.message) {
        if (!state.message.isNullOrBlank()) {
            onLoginSuccess(); viewModel.clearLoginTransientState()
        }
    }

    val bg = Brush.linearGradient(colors = listOf(Color(0xFF050816), Color(0xFF111827), Color(0xFF190F2D)))
    val accent = Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4), Color(0xFFEC4899)))

    Box(modifier = Modifier.fillMaxSize().background(bg).padding(contentPadding).padding(20.dp), contentAlignment = Alignment.Center) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Đăng nhập", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Text("Đăng nhập để tiếp tục", color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Số điện thoại") })
                OutlinedTextField(value = password, onValueChange = { password = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Mật khẩu") }, visualTransformation = PasswordVisualTransformation())
                if (localError != null) Text(localError!!, color = Color(0xFFFCA5A5))
                if (state.error != null) Text(state.error!!, color = Color(0xFFFCA5A5))
                Button(onClick = { if (phone.isBlank() || password.isBlank()) localError = "Vui lòng nhập đầy đủ thông tin" else { localError = null; viewModel.login(phone.trim(), password.trim(), onSuccess = {}) } }, modifier = Modifier.fillMaxWidth(), enabled = !state.isLoading, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))) { Text("Đăng nhập", color = Color.White) }
                if (state.isLoading) CircularProgressIndicator()
                Button(onClick = onNavigateToRegister, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827))) { Text("Chưa có tài khoản? Đăng ký", color = Color.White) }
            }
        }
    }
}
