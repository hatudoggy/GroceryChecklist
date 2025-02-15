package com.example.grocerychecklist.ui.screen.item

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.GroceryChecklistApp.Companion.appModule
import com.example.grocerychecklist.ui.screen.Routes
import com.example.grocerychecklist.viewmodel.item.ItemMainViewModel
import com.example.grocerychecklist.viewmodel.viewModelFactory


fun NavGraphBuilder.itemDestination() {

    composable<Routes.ItemMain> {
        val itemMainViewModel = viewModel<ItemMainViewModel>(
            factory = viewModelFactory {
                ItemMainViewModel(appModule.itemRepository)
            }
        )
        val state by itemMainViewModel.state.collectAsState()
        ItemMainScreen(
            state = state,
            onEvent = itemMainViewModel::onEvent,
            viewModel = itemMainViewModel
        )
    }
}