package com.example.grocerychecklist.ui.screen.util

import androidx.activity.OnBackPressedCallback

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.grocerychecklist.ui.screen.Navigator
import com.example.grocerychecklist.ui.screen.Routes

@Composable
fun CustomBackButtonHandler(
    navController: Navigator,
    targetRoute: Routes,
    enabled: Boolean = true
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current

    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                // Disable the default back behavior
                isEnabled = false
                // Navigate to the target route
                navController.navigateWithClearBackStack(targetRoute)
                // Re-enable the callback if needed
                isEnabled = true
            }
        }
    }

    // Add and remove the callback when the composable enters or leaves the composition
    DisposableEffect(key1 = backDispatcher) {
        backDispatcher?.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}