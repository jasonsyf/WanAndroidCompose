package com.syf.wanandroidcompose.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.syf.wanandroidcompose.theme.WanAndroidComposeTheme

@Composable
fun ProfileView(
    viewModel: ProfileViewModel = viewModel(), 
    rootNavController: NavController,
    themeModeText: String = "系统",
    contrastText: String = "标准",
    fontText: String = "系统",
    languageText: String = "系统",
    onToggleThemeMode: () -> Unit = {},
    onToggleThemeContrast: () -> Unit = {},
    onToggleFontStyle: () -> Unit = {},
    onToggleLanguage: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle(initialValue = ProfileState())

    LaunchedEffect(state.navigateToLoginRegister) {
        if (state.navigateToLoginRegister) {
            rootNavController.navigate("loginRegister")
            viewModel.sendAction(ProfileAction.LoginRegisterNavigated)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User Header - Clean & Elegant
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable {
                    if (!state.isLogin) {
                        viewModel.sendAction(ProfileAction.ClickLoginRegister)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        if (state.isLogin) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "头像",
                    modifier = Modifier.size(48.dp),
                    tint = if (state.isLogin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(modifier = Modifier.padding(start = 20.dp).weight(1f)) {
                Text(
                    text = if (state.isLogin) state.username else "未登录",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (state.isLogin) "欢迎回来，继续探索知识吧" else "点击登录 / 注册",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Groups
        SettingsGroup {
            ProfileMenuItem(
                text = "我的收藏",
                icon = Icons.Default.Favorite,
                iconTint = MaterialTheme.colorScheme.error,
                iconBackground = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                onClick = { viewModel.sendAction(ProfileAction.ClickMyCollection) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "应用设置",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 8.dp)
        )

        SettingsGroup {
            ProfileMenuItem(
                text = "语言设置",
                valueText = languageText,
                icon = Icons.Default.Settings,
                iconTint = MaterialTheme.colorScheme.tertiary,
                iconBackground = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                onClick = onToggleLanguage
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), modifier = Modifier.padding(start = 68.dp))
            ProfileMenuItem(
                text = "主题模式",
                valueText = themeModeText,
                icon = Icons.Default.Settings,
                iconTint = MaterialTheme.colorScheme.secondary,
                iconBackground = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                onClick = onToggleThemeMode
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), modifier = Modifier.padding(start = 68.dp))
            ProfileMenuItem(
                text = "主题对比度",
                valueText = contrastText,
                icon = Icons.Default.Settings,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBackground = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                onClick = onToggleThemeContrast
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f), modifier = Modifier.padding(start = 68.dp))
            ProfileMenuItem(
                text = "字体样式",
                valueText = fontText,
                icon = Icons.Default.Settings,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                iconBackground = MaterialTheme.colorScheme.surfaceVariant,
                onClick = onToggleFontStyle
            )
        }

        if (state.isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SettingsGroup(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun ProfileMenuItem(
    text: String, 
    valueText: String? = null, 
    icon: ImageVector, 
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconBackground: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
        }
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (valueText != null) {
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    WanAndroidComposeTheme {
        ProfileView(rootNavController = rememberNavController())
    }
}