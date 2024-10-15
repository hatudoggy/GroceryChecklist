package com.example.grocerychecklist.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.grocerychecklist.data.ColorOption
import com.example.grocerychecklist.data.IconOption
import com.example.grocerychecklist.data.model.Checklist
import com.example.grocerychecklist.domain.utility.DateUtility
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainEvent
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainState

@Composable
fun DashboardMainScreen(
    state: DashboardMainState,
    onEvent: (DashboardMainEvent) -> Unit
) {
    Scaffold {
        innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardMainScreenPreview() {
    val currentDateTime = DateUtility.getCurrentDateTime()
    val mockState = DashboardMainState(
        checklists = listOf(
            Checklist(
                name = "Dog",
                description = "",
                icon = IconOption.Home,
                iconColor = ColorOption.White,
                createdAt = currentDateTime,
                updatedAt = currentDateTime,
                lastOpenedAt = currentDateTime,
                lastShopAt = currentDateTime
            )
        )
    )
    val mockOnEvent: (DashboardMainEvent) -> Unit = {}

    DashboardMainScreen(
        state = mockState,
        onEvent = mockOnEvent
    )
}