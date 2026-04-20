package com.syf.wanandroidcompose.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory), onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = LoginState())
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(key1 = state.errorMsg) {
        state.errorMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(key1 = state.loginSuccess) {
        if (state.loginSuccess) {
            snackbarHostState.showSnackbar("登录成功")
            onBack()
            viewModel.sendAction(LoginAction.Navigated)
        }
    }

    LaunchedEffect(key1 = state.registerSuccess) {
        if (state.registerSuccess) {
            snackbarHostState.showSnackbar("注册成功")
            viewModel.sendAction(LoginAction.Navigated)
            // 注册成功后可以自动登录或者跳转到登录页面
            // 这里我们假设注册成功后停留在当前页面，等待用户手动登录
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "登录",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = state.usernameInput,
                onValueChange = { viewModel.sendAction(LoginAction.InputUsername(it)) },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.passwordInput,
                onValueChange = { viewModel.sendAction(LoginAction.InputPassword(it)) },
                label = { Text("密码") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.sendAction(LoginAction.ClickLogin) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("登录")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.sendAction(LoginAction.ClickRegister) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("注册")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    WanAndroidComposeTheme {
        LoginScreen(onBack = {})
    }
}