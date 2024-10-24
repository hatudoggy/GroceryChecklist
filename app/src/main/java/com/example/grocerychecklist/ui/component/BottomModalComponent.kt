package com.example.grocerychecklist.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModalComponent(
    sheetState: SheetState,
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit,
    contentTitle: String = "Select currency",
    isPreviewing: Boolean = false
) {
    val previewSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = if (isPreviewing) previewSheetState else sheetState
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                contentTitle, style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 28.sp,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BottomModalComponentPreview() {
    val openBottomSheet = rememberSaveable { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState()

    if (openBottomSheet.value) {
        BottomModalComponent(
            bottomSheetState,
            content = { MockBottomModalContentComponent() },
            isPreviewing = true
        )
    }
}

@Composable
fun MockBottomModalContentComponent() {
    LazyColumn (verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(10) {
            BottomModalButtonComponent(title = "Philippine Peso", subTitle = "P")
        }
    }
}