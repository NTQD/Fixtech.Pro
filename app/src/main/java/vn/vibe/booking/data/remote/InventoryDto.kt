package vn.vibe.booking.data.remote

import org.json.JSONObject

data class InventoryItemDto(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val imageUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)

fun JSONObject.toInventoryItemDto() = InventoryItemDto(
    id = optLong("id"),
    name = optString("name", ""),
    description = if (has("description") && !isNull("description")) optString("description") else null,
    price = optDouble("price", 0.0),
    stock = optInt("stock", 0),
    imageUrl = if (has("imageUrl") && !isNull("imageUrl")) optString("imageUrl") else null,
    createdAt = if (has("createdAt") && !isNull("createdAt")) optString("createdAt") else null,
    updatedAt = if (has("updatedAt") && !isNull("updatedAt")) optString("updatedAt") else null
)
