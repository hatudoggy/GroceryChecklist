package com.example.grocerychecklist.ui.screen.checklist

import ItemTagComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocerychecklist.ui.component.ChecklistComponent
import com.example.grocerychecklist.ui.component.ChecklistComponentVariant
import com.example.grocerychecklist.ui.component.ChecklistItemComponent
import com.example.grocerychecklist.ui.component.ChecklistItemComponentVariant
import com.example.grocerychecklist.ui.component.RoundedTextField
import com.example.grocerychecklist.ui.component.TopBarComponent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ChecklistViewScreen() {
    Scaffold(
        modifier = Modifier.padding(vertical = 0.dp),
        contentWindowInsets = WindowInsets(0.dp),
        topBar = { TopBarComponent(title = "Checklist") },
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
            ) {
                Text("Main Groceries", fontSize = 18.sp)
                //Listed times below
                Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(8.dp),
                                spotColor = Color(0x40000000),
                                ambientColor = Color(0x20000000)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
                ) {
                // Left Column
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Chicken",
                        style = TextStyle(
                            fontSize = 20.sp, // Increased font size
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    )

                    ItemTagComponent(ItemCategory.POULTRY)
                }

                // Right Column
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₱ 20 each",
                        style = TextStyle(
                            fontSize = 14.sp, // Increased font size
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5A5A5)
                        ),
                        textAlign = TextAlign.Right
                    )

                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "₱ 200",
                        style = TextStyle(
                            fontSize = 24.sp, // Increased font size
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        textAlign = TextAlign.Right
                    )

                    Text(
                        text = "10 pcs",
                        style = TextStyle(
                            fontSize = 12.sp, // Increased font size
                            fontWeight = FontWeight.Light,
                            color = Color(0xFFA5A5A5)
                        ),
                        textAlign = TextAlign.Right
                    )
                }
            }
                //Listed times below
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x20000000)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Apple",
                            style = TextStyle(
                                fontSize = 20.sp, // Increased font size
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        )

                        ItemTagComponent(ItemCategory.FRUIT)
                    }

                    // Right Column
                    Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "₱ 15 each",
                            style = TextStyle(
                                fontSize = 14.sp, // Increased font size
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFA5A5A5)
                            ),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = "₱ 150",
                            style = TextStyle(
                                fontSize = 24.sp, // Increased font size
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            text = "10 pcs",
                            style = TextStyle(
                                fontSize = 12.sp, // Increased font size
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFA5A5A5)
                            ),
                            textAlign = TextAlign.Right
                        )
                    }
                }
                //Listed times below
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x20000000)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Hotdog",
                            style = TextStyle(
                                fontSize = 20.sp, // Increased font size
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        )

                        ItemTagComponent(ItemCategory.MEAT)
                    }

                    // Right Column
                    Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "₱ 25 each",
                            style = TextStyle(
                                fontSize = 14.sp, // Increased font size
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFA5A5A5)
                            ),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = "₱ 250",
                            style = TextStyle(
                                fontSize = 24.sp, // Increased font size
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            text = "10 pcs",
                            style = TextStyle(
                                fontSize = 12.sp, // Increased font size
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFA5A5A5)
                            ),
                            textAlign = TextAlign.Right
                        )
                    }
                }
                //Listed times below
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = Color(0x40000000),
                            ambientColor = Color(0x20000000)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Toilet Paper",
                            style = TextStyle(
                                fontSize = 20.sp, // Increased font size
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        )

                        ItemTagComponent(ItemCategory.SANITARY)
                    }

                    // Right Column
                    Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "₱ 10 / dozen",
                            style = TextStyle(
                                fontSize = 14.sp, // Increased font size
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFA5A5A5)
                            ),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = "₱ 100",
                            style = TextStyle(
                                fontSize = 24.sp, // Increased font size
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            text = "10 pcs",
                            style = TextStyle(
                                fontSize = 12.sp, // Increased font size
                                fontWeight = FontWeight.Light,
                                color = Color(0xFFA5A5A5)
                            ),
                            textAlign = TextAlign.Right
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChecklistViewScreenPreview() {
    ChecklistViewScreen()
}
