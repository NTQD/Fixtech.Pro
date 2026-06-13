package vn.vibe.booking.data.remote

import org.json.JSONObject

data class DashboardOverviewDto(
    val totalRevenue: Double,
    val totalBookings: Int,
    val totalUsers: Int,
    val totalInventoryItems: Int
)

data class RevenueDetailDto(
    val bookingId: Long,
    val bookingCode: String,
    val amount: Double,
    val rating: Int?,
    val customerName: String?,
    val completedAt: String?
)

fun JSONObject.toDashboardOverviewDto() = DashboardOverviewDto(
    totalRevenue = optDouble("totalRevenue", 0.0),
    totalBookings = optInt("totalBookings", 0),
    totalUsers = optInt("totalUsers", 0),
    totalInventoryItems = optInt("totalInventoryItems", 0)
)

fun JSONObject.toRevenueDetailDto() = RevenueDetailDto(
    bookingId = optLong("bookingId"),
    bookingCode = optString("bookingCode", ""),
    amount = optDouble("amount", 0.0),
    rating = if (has("rating") && !isNull("rating")) optInt("rating") else null,
    customerName = if (has("customerName") && !isNull("customerName")) optString("customerName") else null,
    completedAt = if (has("completedAt") && !isNull("completedAt")) optString("completedAt") else null
)
