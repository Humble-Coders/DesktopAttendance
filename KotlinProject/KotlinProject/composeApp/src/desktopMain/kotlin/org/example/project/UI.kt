package org.example.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image

// Main Application UI
@Composable
fun JewelryApp(viewModel: ProductsViewModel) {
    // State for navigation
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val imageLoader = JewelryAppInitializer.getImageLoader()

    // Material Theme customization for jewelry store
    MaterialTheme(
        colors = lightColors(
            primary = Color(0xFFC9AD6E),        // Gold
            primaryVariant = Color(0xFFB8973D),  // Darker gold
            secondary = Color(0xFF7D7D7D),      // Silver
            background = Color(0xFFF9F9F9),     // Light background
            surface = Color.White
        )
    ) {
        // Use a Row layout to create a permanent drawer effect
        Row {
            // Sidebar navigation - fixed width, always visible
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp)  // Increase drawer width
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color(0xFFEEEEEE)))
            ) {
                // App logo header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // App logo placeholder
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Build,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Jewelry Inventory",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Navigation items
                NavigationItem(
                    icon = Icons.Default.AccountBox,
                    title = "Dashboard",
                    selected = currentScreen == Screen.DASHBOARD,
                    onClick = { currentScreen = Screen.DASHBOARD }
                )

                NavigationItem(
                    icon = Icons.Default.AddCircle,
                    title = "Add Product",
                    selected = currentScreen == Screen.ADD_PRODUCT,
                    onClick = {
                        viewModel.createNewProduct()
                        currentScreen = Screen.ADD_PRODUCT
                    }
                )

                NavigationItem(
                    icon = Icons.Default.Settings,
                    title = "Settings",
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = { currentScreen = Screen.SETTINGS }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Version info
                Text(
                    "Version 1.0",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            // Main content area
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top bar
                TopAppBar(
                    title = { Text("Jewelry Inventory Management") },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White,
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                viewModel.loadProducts()
                                snackbarHostState.showSnackbar("Data refreshed")
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                )

                // Content with SnackbarHost
                Box(modifier = Modifier.fillMaxSize()) {
                    // Screen content based on navigation
                    when (currentScreen) {
                        Screen.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            imageLoader = imageLoader,
                            onAddProduct = {
                                viewModel.createNewProduct()
                                currentScreen = Screen.ADD_PRODUCT
                            },
                            onEditProduct = { productId ->
                                viewModel.selectProduct(productId)
                                currentScreen = Screen.EDIT_PRODUCT
                            }
                        )

                        Screen.ADD_PRODUCT -> AddEditProductScreen(
                            viewModel = viewModel,
                            onSave = {
                                currentScreen = Screen.DASHBOARD
                            },
                            onCancel = {
                                viewModel.clearCurrentProduct()
                                currentScreen = Screen.DASHBOARD
                            }
                        )

                        Screen.EDIT_PRODUCT -> AddEditProductScreen(
                            viewModel = viewModel,
                            onSave = {
                                currentScreen = Screen.DASHBOARD
                            },
                            onCancel = {
                                viewModel.clearCurrentProduct()
                                currentScreen = Screen.DASHBOARD
                            },
                            isEditing = true
                        )

                        Screen.SETTINGS -> SettingsScreen()
                    }

                    // Error message display
                    viewModel.error.value?.let { errorMessage ->
                        if (errorMessage.isNotEmpty()) {
                            LaunchedEffect(errorMessage) {
                                snackbarHostState.showSnackbar(
                                    message = errorMessage,
                                    actionLabel = "Dismiss"
                                )
                            }
                        }
                    }

                    // Loading indicator
                    if (viewModel.loading.value) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // Snackbar host
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

// Navigation item composable
@Composable
fun NavigationItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color(0xFFEFEFEF) else Color.Transparent
    val textColor = if (selected) MaterialTheme.colors.primary else Color.Black

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Dashboard Screen
@Composable
fun DashboardScreen(
    viewModel: ProductsViewModel,
    imageLoader: ImageLoader,
    onAddProduct: () -> Unit,
    onEditProduct: (String) -> Unit
) {
    val products by remember { viewModel.products }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val productToDelete = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with title and add button
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Products",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onAddProduct,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add New Product")
            }
        }

        // Search bar
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Implement search */ },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            placeholder = { Text("Search products...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )

        // Products table
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
        ) {
            // Table header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Use Box containers with consistent width for each column
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(0.15f)) {
                    Text(
                        "Image",
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(0.2f)) {
                    Text(
                        "Name",
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(0.15f)) {
                    Text(
                        "Category",
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(0.15f)) {
                    Text(
                        "Material",
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(0.1f)) {
                    Text(
                        "Price",
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(0.1f)) {
                    Text(
                        "Available",
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(modifier = Modifier.weight(0.15f)) {
                    Text(
                        "Actions",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider()

            // Table content
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No products found. Add a new product to get started.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn {
                    items(products) { product ->
                        ProductRow(
                            product = product,
                            viewModel = viewModel,
                            imageLoader = imageLoader,
                            onEdit = { onEditProduct(product.id) },
                            onDelete = {
                                productToDelete.value = product.id
                                showDeleteDialog.value = true
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this product? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete.value?.let { viewModel.deleteProduct(it) }
                        showDeleteDialog.value = false
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProductRow(
    product: Product,
    viewModel: ProductsViewModel,
    imageLoader: ImageLoader,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var productImage by remember { mutableStateOf<ImageBitmap?>(null) }

    // Load the first image if available
    LaunchedEffect(product.images) {
        if (product.images.isNotEmpty()) {
            val imageUrl = product.images.first()
            coroutineScope.launch {
                val imageBytes = imageLoader.loadImage(imageUrl)
                imageBytes?.let {
                    val image = withContext(Dispatchers.IO) {
                        Image.makeFromEncoded(it).asImageBitmap()
                    }
                    productImage = image
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product image
        Box(
            modifier = Modifier
                .weight(0.15f)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            if (productImage != null) {
                Image(
                    bitmap = productImage!!,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        // Product details
        TableCell(text = product.name, weight = 0.2f)
        TableCell(text = viewModel.getCategoryName(product.categoryId), weight = 0.15f)
        TableCell(text = viewModel.getMaterialName(product.materialId), weight = 0.15f)
        TableCell(text = "$${product.price}", weight = 0.1f)

        // Availability status
        Box(
            modifier = Modifier.weight(0.1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 30.dp)
                    .background(
                        if (product.available) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (product.available) "Yes" else "No",
                    color = if (product.available) Color(0xFF388E3C) else Color(0xFFD32F2F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.weight(0.15f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFE3F2FD), CircleShape)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF1976D2)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFFEBEE), CircleShape)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
fun TableCell(text: String, weight: Float) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(weight),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

// Add/Edit Product Screen
@Composable
fun AddEditProductScreen(
    viewModel: ProductsViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isEditing: Boolean = false
) {
    val product = viewModel.currentProduct.value ?: Product()
    val categories by remember { viewModel.categories }
    val materials by remember { viewModel.materials }

    // Form state
    var name by remember { mutableStateOf(product.name) }
    var description by remember { mutableStateOf(product.description) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var categoryId by remember { mutableStateOf(product.categoryId) }
    var materialId by remember { mutableStateOf(product.materialId) }
    var materialType by remember { mutableStateOf(product.materialType) }
    var gender by remember { mutableStateOf(product.gender) }
    var weight by remember { mutableStateOf(product.weight) }
    var available by remember { mutableStateOf(product.available) }
    var featured by remember { mutableStateOf(product.featured) }

    // Validation state
    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var materialError by remember { mutableStateOf(false) }

    // Expanded dropdown states
    var categoryExpanded by remember { mutableStateOf(false) }
    var materialExpanded by remember { mutableStateOf(false) }
    var materialTypeExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            if (isEditing) "Edit Product" else "Add New Product",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = it.isEmpty()
                    },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError,
                    singleLine = true
                )
                if (nameError) {
                    Text(
                        "Product name is required",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                // Price
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = try {
                            it.toDouble() <= 0
                        } catch (e: NumberFormatException) {
                            true
                        }
                    },
                    label = { Text("Price (Rs)") },  // Include the currency in the label
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = priceError,
                    singleLine = true
                )
                if (priceError) {
                    Text(
                        "Please enter a valid price",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Category dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categories.find { it.id == categoryId }?.name ?: "",
                        onValueChange = { },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { categoryExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Category")
                            }
                        },
                        readOnly = true,
                        isError = categoryError
                    )

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(onClick = {
                                categoryId = category.id
                                categoryExpanded = false
                                categoryError = false
                            }) {
                                Text(category.name)
                            }
                        }
                    }
                }
                if (categoryError) {
                    Text(
                        "Category is required",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Material dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = materials.find { it.id == materialId }?.name ?: "",
                        onValueChange = { },
                        label = { Text("Material") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { materialExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Material")
                            }
                        },
                        readOnly = true,
                        isError = materialError
                    )

                    DropdownMenu(
                        expanded = materialExpanded,
                        onDismissRequest = { materialExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        materials.forEach { material ->
                            DropdownMenuItem(onClick = {
                                materialId = material.id
                                materialType = "" // Reset material type when material changes
                                materialExpanded = false
                                materialError = false
                            }) {
                                Text(material.name)
                            }
                        }
                    }
                }
                if (materialError) {
                    Text(
                        "Material is required",
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                // Material Type dropdown (only show if material is selected)
                if (materialId.isNotEmpty()) {
                    val selectedMaterial = materials.find { it.id == materialId }
                    val materialTypes = selectedMaterial?.types ?: emptyList()

                    if (materialTypes.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = materialType,
                                onValueChange = { },
                                label = { Text("Material Type") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { materialTypeExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Material Type")
                                    }
                                },
                                readOnly = true
                            )

                            DropdownMenu(
                                expanded = materialTypeExpanded,
                                onDismissRequest = { materialTypeExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                materialTypes.forEach { type ->
                                    DropdownMenuItem(onClick = {
                                        materialType = type
                                        materialTypeExpanded = false
                                    }) {
                                        Text(type)
                                    }
                                }
                            }
                        }
                    }
                }

                // Gender dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { },
                        label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { genderExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Gender")
                            }
                        },
                        readOnly = true
                    )

                    DropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        listOf("Men", "Women", "Unisex").forEach { genderOption ->
                            DropdownMenuItem(onClick = {
                                gender = genderOption
                                genderExpanded = false
                            }) {
                                Text(genderOption)
                            }
                        }
                    }
                }

                // Weight
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (e.g. 8.5g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Availability switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Available", modifier = Modifier.weight(1f))
                    Switch(
                        checked = available,
                        onCheckedChange = { available = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                }

                // Featured switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Featured", modifier = Modifier.weight(1f))
                    Switch(
                        checked = featured,
                        onCheckedChange = { featured = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary)
                    )
                }

                // Image URLs - in a real app, you'd have image upload functionality
                // For now, we'll keep the existing images

                Spacer(modifier = Modifier.height(16.dp))

                // Form buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Validate form
                            nameError = name.isEmpty()
                            priceError = try {
                                price.toDouble() <= 0
                            } catch (e: NumberFormatException) {
                                true
                            }
                            categoryError = categoryId.isEmpty()
                            materialError = materialId.isEmpty()

                            if (!nameError && !priceError && !categoryError && !materialError) {
                                // Create updated product
                                val updatedProduct = Product(
                                    id = product.id,
                                    name = name,
                                    description = description,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    categoryId = categoryId,
                                    materialId = materialId,
                                    materialType = materialType,
                                    gender = gender,
                                    weight = weight,
                                    available = available,
                                    featured = featured,
                                    images = product.images,
                                    createdAt = product.createdAt
                                )

                                if (isEditing) {
                                    viewModel.updateProduct(updatedProduct)
                                } else {
                                    viewModel.addProduct(updatedProduct)
                                }

                                onSave()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                    ) {
                        Text(if (isEditing) "Save Changes" else "Add Product", color = Color.White)
                    }
                }
            }
        }
    }
}

// Settings Screen (placeholder)
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Settings functionality will be implemented in future updates.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

// Screen enum for navigation
enum class Screen {
    DASHBOARD,
    ADD_PRODUCT,
    EDIT_PRODUCT,
    SETTINGS
}

// Main function
