package com.example.grocerychecklist.ui.screen

import androidx.navigation.NavController

class Navigator {

    private var navController: NavController? = null

    fun setController(navController: NavController) {
        this.navController = navController
    }

    fun navigate(route: Routes) {
        navController?.navigate(
            route
        )
    }

    fun popBackStack() {
        navController?.popBackStack()
    }

    fun clear() {
        navController = null
    }
}