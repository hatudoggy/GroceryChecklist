package com.example.grocerychecklist.ui.screen.item

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.Routes


fun NavGraphBuilder.itemDestination() {
    composable<Routes.ItemMain> {
        ItemMainScreen()
    }
}