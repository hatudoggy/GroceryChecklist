package com.example.grocerychecklist.ui.screen.checklist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.grocerychecklist.ui.screen.Routes


fun NavGraphBuilder.checklistDestination() {
    composable<Routes.ChecklistMain> {
        ChecklistMainScreen()
    }
    composable<Routes.ChecklistDetail> {
        ChecklistDetailScreen()
    }
    composable<Routes.ChecklistView> {
        ChecklistViewScreen()
    }
    composable<Routes.ChecklistEdit> {
        ChecklistEditScreen()
    }
    composable<Routes.ChecklistStart> {
        ChecklistStartScreen()
    }
}