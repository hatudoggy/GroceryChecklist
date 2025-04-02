package com.example.grocerychecklist.ui.component

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ToastComponent(
    message: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = message) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        onDismiss()
    }
}

@Preview(showBackground = true)
@Composable
fun ToastComponentPreview() {

}