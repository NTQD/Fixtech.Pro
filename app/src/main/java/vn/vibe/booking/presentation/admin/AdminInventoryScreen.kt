package vn.vibe.booking.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.data.remote.InventoryItemDto
import vn.vibe.booking.presentation.home.HomeViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminInventoryScreen(
    viewModel: HomeViewModel,
    token: String?,
    onBack: () -> Unit
) {
    val inventoryState by viewModel.inventoryState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    
    // Sorting state
    var sortOption by remember { mutableStateOf(0) } // 0: Default (ID), 1: Price High-Low, 2: Price Low-High
    var showSortMenu by remember { mutableStateOf(false) }

    // Tabs state
    val categories = listOf("Tất cả", "CPU", "RAM", "Mainboard", "SSD/HDD", "VGA", "Màn hình", "Bàn phím", "Touchpad", "Pin Laptop", "Webcam/Camera", "Loa", "Phụ kiện")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Dialog state
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoryItemDto?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(token, searchQuery) {
        if (token != null) {
            viewModel.loadInventory(token, keyword = searchQuery.takeIf { it.isNotBlank() }, limit = 500)
        }
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    // Filtering logic
    val filteredItems = remember(inventoryState.items, selectedTabIndex, sortOption) {
        var list = inventoryState.items
        
        // Category filtering
        if (selectedTabIndex > 0) {
            val cat = categories[selectedTabIndex].lowercase()
            list = list.filter { item ->
                val name = item.name.lowercase()
                val isVga = name.contains("vga") || name.contains("card màn hình") || name.contains("gpu") || name.contains("graphics") || name.contains("card đồ họa") || name.contains("geforce") || name.contains("radeon") || name.contains("rtx")
                val isMain = name.contains("mainboard") || name.contains("bo mạch chủ") || name.contains("motherboard") || name.contains("bo mạch") || name.startsWith("main") || name.contains(" main ")
                val isRam = name.contains("ram") || name.contains("memory") || name.contains("ddr")
                val isStorage = name.contains("ssd") || name.contains("hdd") || name.contains("ổ cứng") || name.contains("hard drive") || name.contains("ổ đĩa")
                val isScreen = (name.contains("màn hình") || name.contains("screen") || name.contains("lcd") || name.contains("display") || name.contains("panel")) && !isVga
                val isKeyboard = name.contains("bàn phím") || name.contains("keyboard")
                val isTouchpad = name.contains("touchpad") || name.contains("trackpad") || name.contains("bàn di chuột")
                val isBattery = (name.contains("pin") || name.contains("battery")) && !name.contains("30pin") && !name.contains("40pin")
                val isCamera = name.contains("webcam") || name.contains("camera") || name.contains("ống kính")
                val isSpeaker = name.contains("loa") || name.contains("speaker") || name.contains("sound")
                val isCpu = (name.contains("intel") || name.contains("amd") || name.contains("ryzen") || name.contains("cpu") || name.contains("chip") || name.contains("pentium") || name.contains("threadripper") || name.contains("vi xử lý") || name.contains("processor")) &&
                            !isVga && !isMain && !isRam && !isStorage && !isScreen && !isKeyboard && !isTouchpad && !isBattery && !isCamera && !isSpeaker

                when (cat) {
                    "cpu" -> isCpu
                    "ram" -> isRam
                    "mainboard" -> isMain
                    "ssd/hdd" -> isStorage
                    "vga" -> isVga
                    "màn hình" -> isScreen
                    "bàn phím" -> isKeyboard
                    "touchpad" -> isTouchpad
                    "pin laptop" -> isBattery
                    "webcam/camera" -> isCamera
                    "loa" -> isSpeaker
                    "phụ kiện" -> !isCpu && !isRam && !isMain && !isStorage && !isVga && !isScreen && !isKeyboard && !isTouchpad && !isBattery && !isCamera && !isSpeaker
                    else -> true
                }
            }
        }

        // Sorting logic
        when (sortOption) {
            1 -> list.sortedByDescending { it.price }
            2 -> list.sortedBy { it.price }
            else -> list.sortedBy { it.id }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý kho (Linh kiện)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")
                    }
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sắp xếp")
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Mặc định (ID)") },
                            onClick = { sortOption = 0; showSortMenu = false },
                            leadingIcon = { if (sortOption == 0) Icon(Icons.Default.FilterList, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Giá: Cao đến Thấp") },
                            onClick = { sortOption = 1; showSortMenu = false },
                            leadingIcon = { if (sortOption == 1) Icon(Icons.Default.FilterList, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Giá: Thấp đến Cao") },
                            onClick = { sortOption = 2; showSortMenu = false },
                            leadingIcon = { if (sortOption == 2) Icon(Icons.Default.FilterList, null) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                selectedItem = null
                showAddEditDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm linh kiện")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Tìm kiếm linh kiện...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                divider = {},
                containerColor = MaterialTheme.colorScheme.background
            ) {
                categories.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (inventoryState.loading && inventoryState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (inventoryState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(inventoryState.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${categories[selectedTabIndex]} (${filteredItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(filteredItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = currencyFormatter.format(item.price),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.description ?: "Không có mô tả",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tồn kho: ${item.stock}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (item.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    Row {
                                        IconButton(onClick = { 
                                            selectedItem = item
                                            showAddEditDialog = true 
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { 
                                            selectedItem = item
                                            showDeleteDialog = true 
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddEditDialog) {
        InventoryAddEditDialog(
            item = selectedItem,
            onDismiss = { showAddEditDialog = false },
            onConfirm = { name, desc, price, stock ->
                if (token != null) {
                    if (selectedItem == null) {
                        viewModel.createInventoryItem(token, name, desc, price, stock) {
                            showAddEditDialog = false
                        }
                    } else {
                        viewModel.updateInventoryItem(token, selectedItem!!.id, name, desc, price, stock) {
                            showAddEditDialog = false
                        }
                    }
                }
            }
        )
    }

    if (showDeleteDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa linh kiện '${selectedItem!!.name}' không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (token != null) {
                            viewModel.deleteInventoryItem(token, selectedItem!!.id) {
                                showDeleteDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun InventoryAddEditDialog(
    item: InventoryItemDto?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var priceStr by remember { mutableStateOf(item?.price?.toInt()?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(item?.stock?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Thêm linh kiện mới" else "Sửa thông tin linh kiện") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên linh kiện *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Giá bán (đ) *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = stockStr, onValueChange = { stockStr = it }, label = { Text("Số lượng tồn kho *") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val stock = stockStr.toIntOrNull() ?: 0
                    if (name.isNotBlank() && price > 0) {
                        onConfirm(name, description.takeIf { it.isNotBlank() }, price, stock)
                    }
                },
                enabled = name.isNotBlank() && priceStr.isNotBlank() && stockStr.isNotBlank()
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

