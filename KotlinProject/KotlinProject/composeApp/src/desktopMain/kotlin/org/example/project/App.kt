package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Data class for jewelry items
data class JewelryItem(
    val name: String,
    val sku: String,
    val category: String,
    val quantity: Int,
    val price: Int,
    val image: String
)

@Composable
fun App() {
    // Sample data
    val jewelryItems = remember {
        mutableStateListOf(
            JewelryItem("Gold Necklace", "SKU12345", "Necklace", 50, 100, "gold_necklace.jpg"),
            JewelryItem("Silver Earrings", "SKU67890", "Earrings", 150, 50, "silver_earrings.jpg"),
            JewelryItem("Silver Bracelet", "SKU54321", "Bracelet", 30, 200, "silver_bracelet.jpg"),
            JewelryItem("Silver Bracelet", "SKU98765", "Necklace", 20, 500, "silver_earrings.jpg"),
            JewelryItem("Gold Necklace", "SKU11223", "Bracelet", 75, 150, "gold_necklace.jpg")
        )
    }

    MaterialTheme(
        colors = lightColors(
            primary = Color(0xFFC9AD6E),
            primaryVariant = Color(0xFFB8973D),
            secondary = Color(0xFFA58D4E)
        )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar
            SideBar(modifier = Modifier.width(280.dp).fillMaxHeight())

            // Main content
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Search and Add button row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search Products...") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFC9AD6E)),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Add New Product", color = Color.White)
                    }
                }

                // Table header
                JewelryTableHeader()

                // Table content
                LazyColumn {
                    items(jewelryItems) { item ->
                        JewelryTableRow(item)
                    }
                }
            }
        }
    }
}

@Composable
fun SideBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.background(Color(0xFFC9AD6E)),
        horizontalAlignment = Alignment.Start
    ) {
        // App logo and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // You can replace this with your actual logo
            // Using a placeholder icon since we don't have the actual resource
            Box(
                modifier = Modifier.size(32.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("📦", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Inventory App",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Menu items
        SidebarMenuItem("Dashboard", true)
        SidebarMenuItem("Inventory", false)
        SidebarMenuItem("Sales", false)
        SidebarMenuItem("Settings", false)
    }
}

@Composable
fun SidebarMenuItem(title: String, isSelected: Boolean) {
    val backgroundColor = if (isSelected) Color(0xFFB8973D) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun JewelryTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Name",
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            "SKU",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text(
            "Category",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text(
            "Quantity",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text(
            "Price",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Text(
            "Images",
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun JewelryTableRow(item: JewelryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(Color.White)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            item.name,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
        Text(
            item.sku,
            modifier = Modifier.weight(1f)
        )
        Text(
            item.category,
            modifier = Modifier.weight(1f)
        )
        Text(
            item.quantity.toString(),
            modifier = Modifier.weight(1f)
        )
        Text(
            "$${item.price}",
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for the jewelry image
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("🏞️", fontSize = 16.sp)
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.padding(end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {},
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Gray
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Edit")
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFC9AD6E),
                    contentColor = Color.White
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Delete")
            }
        }
    }
}