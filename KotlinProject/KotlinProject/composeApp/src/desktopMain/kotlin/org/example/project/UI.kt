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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.toComposeImageBitmap
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
                            // Add this new property
                            onViewProductDetails = { productId ->
                                viewModel.selectProduct(productId)
                                currentScreen = Screen.PRODUCT_DETAIL
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

                        // Add the new product detail screen case
                        Screen.PRODUCT_DETAIL -> ProductDetailScreen(
                            viewModel = viewModel,
                            imageLoader = imageLoader,
                            onEdit = {
                                // No need to select product again as it's already selected
                                currentScreen = Screen.EDIT_PRODUCT
                            },
                            onBack = {
                                currentScreen = Screen.DASHBOARD
                            }
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
    onViewProductDetails: (String) -> Unit  // Add this parameter

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                    Text("Image", fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.CenterStart) {
                    Text("Name", fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                    Text("Category", fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                    Text("Material", fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
                    Text("Price", fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
                    Text("Available", fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                    Text("Actions", fontWeight = FontWeight.Bold)
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
                            onDelete = {
                                productToDelete.value = product.id
                                showDeleteDialog.value = true
                            },
                            // Add this new parameter
                            onClick = { onViewProductDetails(product.id) }
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



// Update the ProductRow function by replacing TableCell with consistent Box approach
@Composable
fun ProductRow(
    product: Product,
    viewModel: ProductsViewModel,
    imageLoader: ImageLoader,
    onDelete: () -> Unit,
    onClick: () -> Unit
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
                        Image.makeFromEncoded(it).toComposeImageBitmap()
                    }
                    productImage = image
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick=onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product image
        Box(
            modifier = Modifier.weight(0.15f),
            contentAlignment = Alignment.CenterStart
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

        // Name
        Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.CenterStart) {
            Text(
                text = product.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Category
        Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
            Text(
                text = viewModel.getCategoryName(product.categoryId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Material
        Box(modifier = Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
            Text(
                text = viewModel.getMaterialName(product.materialId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Price
        Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.CenterStart) {
            Text(
                text = "$${product.price}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

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
        Box(
            modifier = Modifier.weight(0.15f),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {


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
}



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
    val imageLoader = JewelryAppInitializer.getImageLoader()

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
    var images by remember { mutableStateOf(product.images) }

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

    LaunchedEffect(product) {
        if (isEditing) {
            println("Editing product: ${product.id} - ${product.name}")
        }
    }

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
                            print(e)
                            true
                        }
                    },
                    label = { Text("Price (Rs)") },
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

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Product Image Manager Component
                ProductImageManager(
                    existingImages = images,
                    onImagesChanged = { updatedImages ->
                        images = updatedImages
                    },
                    imageLoader = imageLoader,
                    productId = product.id // Pass product ID for better organization in storage
                )

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
                                print(e)
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
                                    images = images,
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




// Make sure this update is correctly made in the ProductsViewModel
// to ensure the product is properly loaded:

// in ProductsViewModel class:


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

// First, update the Screen enum by adding a PRODUCT_DETAIL entry:


// Add this new composable for the product detail view
@Composable
fun ProductDetailScreen(
    viewModel: ProductsViewModel,
    imageLoader: ImageLoader,
    onEdit: () -> Unit,
    onBack: () -> Unit
) {
    val product = viewModel.currentProduct.value
    val coroutineScope = rememberCoroutineScope()
    var productImages by remember { mutableStateOf<List<Pair<String, ImageBitmap?>>>(emptyList()) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    // Load all product images
    LaunchedEffect(product) {
        product?.let { p ->
            if (p.images.isNotEmpty()) {
                // Initialize the list with null images first
                productImages = p.images.map { it to null }

                // Load each image asynchronously
                p.images.forEachIndexed { index, imageUrl ->
                    coroutineScope.launch {
                        val imageBytes = imageLoader.loadImage(imageUrl)
                        imageBytes?.let {
                            val image = withContext(Dispatchers.IO) {
                                Image.makeFromEncoded(it).toComposeImageBitmap()
                            }
                            // Update just this image in the list
                            val updatedList = productImages.toMutableList()
                            if (index < updatedList.size) {
                                updatedList[index] = imageUrl to image
                                productImages = updatedList
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Product Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colors.primary)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit Product")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display product info or placeholder if not available
        product?.let { p ->
            // Product Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Product images with image gallery
                    if (p.images.isNotEmpty()) {
                        // Main selected image with navigation arrows
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .background(Color(0xFFF5F5F5))
                                .border(1.dp, Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            val currentImage = if (productImages.isNotEmpty() &&
                                selectedImageIndex < productImages.size) {
                                productImages[selectedImageIndex].second
                            } else null

                            if (currentImage != null) {
                                Image(
                                    bitmap = currentImage,
                                    contentDescription = "Product Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                CircularProgressIndicator()
                            }

                            // Add navigation arrows (only if there are multiple images)
                            if (productImages.size > 1) {
                                // Left arrow
                                IconButton(
                                    onClick = {
                                        selectedImageIndex = if (selectedImageIndex > 0)
                                            selectedImageIndex - 1
                                        else
                                            productImages.size - 1
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .size(48.dp)
                                        .background(
                                            color = Color(0x80000000),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = "Previous image",
                                        tint = Color.White
                                    )
                                }

                                // Right arrow
                                IconButton(
                                    onClick = {
                                        selectedImageIndex = if (selectedImageIndex < productImages.size - 1)
                                            selectedImageIndex + 1
                                        else
                                            0
                                    },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(48.dp)
                                        .background(
                                            color = Color(0x80000000),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = "Next image",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Image pagination indicators
                        if (productImages.size > 1) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (i in productImages.indices) {
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .size(10.dp)
                                            .background(
                                                color = if (i == selectedImageIndex)
                                                    MaterialTheme.colors.primary
                                                else
                                                    Color.LightGray,
                                                shape = CircleShape
                                            )
                                            .clickable { selectedImageIndex = i }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Thumbnail row
                        if (productImages.size > 1) {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(productImages.size) { index ->
                                    val (_, image) = productImages[index]
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFF5F5F5))
                                            .border(
                                                width = if (index == selectedImageIndex) 2.dp else 1.dp,
                                                color = if (index == selectedImageIndex)
                                                    MaterialTheme.colors.primary else Color(0xFFEEEEEE),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clickable { selectedImageIndex = index },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (image != null) {
                                            Image(
                                                bitmap = image,
                                                contentDescription = "Thumbnail ${index + 1}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    } else {
                        // No images placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color(0xFFF5F5F5))
                                .border(1.dp, Color(0xFFEEEEEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No images available", color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Product name and price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "₹${p.price}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status indicators (Available, Featured)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Available indicator
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    if (p.available) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (p.available) "In Stock" else "Out of Stock",
                                color = if (p.available) Color(0xFF388E3C) else Color(0xFFD32F2F),
                                fontSize = 14.sp
                            )
                        }

                        // Featured indicator
                        if (p.featured) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(
                                        Color(0xFFFFF8E1),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Featured",
                                    color = Color(0xFFFFA000),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    // Product details section
                    Text(
                        text = "Product Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Product attributes in a grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailRow("Category", viewModel.getCategoryName(p.categoryId))
                        DetailRow("Material", viewModel.getMaterialName(p.materialId))
                        if (p.materialType.isNotEmpty()) {
                            DetailRow("Material Type", p.materialType)
                        }
                        if (p.gender.isNotEmpty()) {
                            DetailRow("Gender", p.gender)
                        }
                        if (p.weight.isNotEmpty()) {
                            DetailRow("Weight", p.weight)
                        }
                        DetailRow("Product ID", p.id)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description section
                    Text(
                        text = "Description",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = p.description.ifEmpty { "No description available" },
                        color = if (p.description.isEmpty()) Color.Gray else Color.Unspecified
                    )
                }
            }
        } ?: run {
            // Placeholder if product is null
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Product not found or still loading...",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )
        Text(
            text = value,
            color = Color.DarkGray
        )
    }
}

// You'll need to add the BrokenImage icon to your imports
// Or you can import BackupRounded or other suitable icon

// Also, you'll need to add ArrowBack to your imports:
// import androidx.compose.material.icons.filled.ArrowBack

// Screen enum for navigation
enum class Screen {
    DASHBOARD,
    ADD_PRODUCT,
    EDIT_PRODUCT,
    PRODUCT_DETAIL,
    SETTINGS
}


// Main function
