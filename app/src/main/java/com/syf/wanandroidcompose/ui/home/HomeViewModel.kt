package com.syf.wanandroidcompose.ui.home

import com.syf.wanandroidcompose.ui.common.BaseViewModel
import com.syf.wanandroidcompose.ui.intentAndState.HomeAction
import com.syf.wanandroidcompose.ui.intentAndState.HomeListState

class HomeViewModel : BaseViewModel<HomeAction, HomeListState>() {

    override fun onAction(
        action: HomeAction,
        currentState: HomeListState?
    ) {
        when (action) {
            is HomeAction.LoadTab ->loadTab()
            is HomeAction.ClickArticle -> toDetail()
            is HomeAction.ClickUser -> loadUserArticle()
            is HomeAction.LoadArticleData -> loadArticleData()
            is HomeAction.LoadPagerData -> loadPagerData()
            is HomeAction.LoadPublic -> loadPublic()
            is HomeAction.RefreshAllData -> refreshAllData()
        }
    }

    private fun loadTab() {
        emitState { HomeListState() }
        TODO("Not yet implemented")
    }

    private fun toDetail() {
        TODO("Not yet implemented")
    }

    private fun loadUserArticle() {
        TODO("Not yet implemented")
    }

    private fun loadArticleData() {
        TODO("Not yet implemented")
    }

    private fun loadPagerData() {
        TODO("Not yet implemented")
    }

    private fun loadPublic() {
        TODO("Not yet implemented")
    }

    private fun refreshAllData() {

        TODO("Not yet implemented")
    }


}