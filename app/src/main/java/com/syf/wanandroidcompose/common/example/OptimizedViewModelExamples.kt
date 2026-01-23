package com.syf.wanandroidcompose.common.example

import androidx.lifecycle.viewModelScope
import com.syf.wanandroidcompose.common.Action
import com.syf.wanandroidcompose.common.BaseViewModelOptimized
import com.syf.wanandroidcompose.common.Effect
import com.syf.wanandroidcompose.common.BaseViewModelWithEffectOptimized
import com.syf.wanandroidcompose.common.State
import com.syf.wanandroidcompose.network.RetrofitClient
import com.syf.wanandroidcompose.home.HomeApiService
import com.syf.wanandroidcompose.network.apiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 优化版 BaseViewModel 使用示例
 */

// ==================== 示例 1：基本使用 ====================

sealed class SearchAction : Action {
    data class SearchQuery(val query: String) : SearchAction()
    data object ClearSearch : SearchAction()
}

data class SearchState(
    val query: String = "",
    val results: List<String> = emptyList(),
    val isLoading: Boolean = false
) : State

class SearchViewModel : BaseViewModelOptimized<SearchAction, SearchState>() {

    override fun onAction(action: SearchAction, currentState: SearchState?) {
        when (action) {
            is SearchAction.SearchQuery -> searchWithDebounce(action.query)
            is SearchAction.ClearSearch -> clearSearch()
        }
    }

    /**
     * 示例：使用 launchAction 实现防抖搜索
     * 
     * 相同 key 的 Action 会取消之前的，避免重复请求
     */
    private fun searchWithDebounce(query: String) {
        // 使用固定 key，新搜索会取消旧的
        launchAction(key = "search") {
            // 防抖延迟
            delay(300)
            
            emitState(replayState?.copy(isLoading = true) ?: SearchState(isLoading = true))
            
            // 模拟网络请求
            delay(1000)
            
            emitState(
                SearchState(
                    query = query,
                    results = listOf("结果1: $query", "结果2: $query", "结果3: $query"),
                    isLoading = false
                )
            )
        }
    }

    private fun clearSearch() {
        // 取消正在进行的搜索
        cancelAction("search")
        
        emitState {
            SearchState()
        }
    }
}

// ==================== 示例 2：异常处理 ====================

sealed class NetworkAction : Action {
    data object LoadData : NetworkAction()
    data object Retry : NetworkAction()
}

data class NetworkState(
    val data: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : State

class NetworkViewModel : BaseViewModelOptimized<NetworkAction, NetworkState>() {

    private val apiService = RetrofitClient.create<HomeApiService>()

    override fun onAction(action: NetworkAction, currentState: NetworkState?) {
        when (action) {
            is NetworkAction.LoadData -> loadData()
            is NetworkAction.Retry -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            emitState(NetworkState(isLoading = true))
            
            apiRequest { apiService.getBanner() }
                .collect { result ->
                    result
                        .onSuccess { data ->
                            emitState(NetworkState(data = "成功加载 ${data.size} 条数据"))
                        }
                        .onError { _, message, _ ->
                            // 错误会被 handleException 捕获
                            emitState(NetworkState(error = message))
                        }
                }
        }
    }

    /**
     * 重写异常处理方法
     */
    override fun handleException(exception: Exception, action: NetworkAction) {
        // 自定义异常处理逻辑
        emitState {
            NetworkState(error = "发生错误: ${exception.message}")
        }
    }
}

// ==================== 示例 3：Effect 使用 ====================

sealed class LoginAction : Action {
    data class Login(val username: String, val password: String) : LoginAction()
    data object Logout : LoginAction()
}

data class LoginState(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val isLoading: Boolean = false
) : State

sealed class LoginEffect : Effect {
    data class ShowToast(val message: String) : LoginEffect()
    data object NavigateToHome : LoginEffect()
    data object NavigateToLogin : LoginEffect()
}

class LoginViewModel : BaseViewModelWithEffectOptimized<LoginAction, LoginState, LoginEffect>() {

    override fun onAction(action: LoginAction, currentState: LoginState?) {
        when (action) {
            is LoginAction.Login -> login(action.username, action.password)
            is LoginAction.Logout -> logout()
        }
    }

    private fun login(username: String, password: String) {
        launchAction {
            emitState(LoginState(isLoading = true))
            
            // 模拟网络请求
            delay(1000)
            
            if (username.isNotEmpty() && password.isNotEmpty()) {
                // 登录成功
                emitState(LoginState(isLoggedIn = true, username = username))
                emitEffect(LoginEffect.ShowToast("登录成功"))
                emitEffect(LoginEffect.NavigateToHome)
            } else {
                // 登录失败
                emitState(LoginState(isLoading = false))
                emitEffect(LoginEffect.ShowToast("用户名或密码不能为空"))
            }
        }
    }

    private fun logout() {
        emitState {
            LoginState()
        }
        emitEffect {
            LoginEffect.ShowToast("已退出登录")
        }
        emitEffect {
            LoginEffect.NavigateToLogin
        }
    }
}

// ==================== 示例 4：异步日志控制 ====================

class DebugViewModel : BaseViewModelOptimized<SearchAction, SearchState>() {

    // 关闭日志（即使在 Debug 模式）
    override val enableLogging: Boolean = false

    override fun onAction(action: SearchAction, currentState: SearchState?) {
        // 处理逻辑
    }
}

// ==================== 示例 5：自定义调度器 ====================

class BackgroundViewModel : BaseViewModelOptimized<SearchAction, SearchState>() {

    // 在 IO 线程处理 Action
    override val actionDispatcher = Dispatchers.IO

    override fun onAction(action: SearchAction, currentState: SearchState?) {
        // 此方法会在 IO 线程执行
    }
}