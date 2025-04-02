package com.example.grocerychecklist.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocerychecklist.data.service.AccountService
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes
import kotlinx.coroutines.launch

class AuthMainViewModel(
    private val navigator: Navigator,
    private val accountService: AccountService
): ViewModel() {

    private fun onCreateGuest(){
        viewModelScope.launch {
            accountService.createAnonymousAccount()
            navigator.navigate(Routes.DashboardMain)
        }
    }

    fun onEvent(event: AuthMainEvent) {
        when(event) {
            AuthMainEvent.NavigateLogin -> { navigator.navigate(Routes.AuthLogin) }
            AuthMainEvent.NavigateRegister -> { navigator.navigate(Routes.AuthRegister) }
            AuthMainEvent.ContinueGuest -> { onCreateGuest() }
        }
    }
}