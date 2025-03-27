package com.example.grocerychecklist.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.R
import com.example.grocerychecklist.ui.theme.LightGray
import com.example.grocerychecklist.ui.theme.PrimaryGreen
import com.example.grocerychecklist.viewmodel.auth.AuthMainEvent
import com.example.grocerychecklist.viewmodel.dashboard.DashboardMainEvent

@Composable
fun AuthMainScreen (
    onEvent: (AuthMainEvent) -> Unit,
) {
    Scaffold { innerPadding ->
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 64.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.grocery_illus),
                    contentDescription = "Image Logo",
                    modifier = Modifier
                        .size(280.dp)
                )
                Text(
                    "Keep track of",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = PrimaryGreen,
                )
                Text(
                    "what you buy",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = PrimaryGreen,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Stay organized and never miss an item with a smart, easy-to-use grocery list",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = { onEvent(AuthMainEvent.NavigateLogin) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) { Text("Login") }
                Button(
                    onClick = { onEvent(AuthMainEvent.NavigateRegister) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) { Text("Register") }
                Button(
                    onClick = { onEvent(AuthMainEvent.ContinueGuest) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightGray,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                ) { Text("Continue as Guest") }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AuthMainScreenPrev() {
    AuthMainScreen(
        onEvent = {}
    )
}