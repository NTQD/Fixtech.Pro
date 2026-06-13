package vn.vibe.booking.data.remote

import org.json.JSONArray
import org.json.JSONObject

data class ApiListResponseDto<T>(
    val items: List<T>,
    val page: Int,
    val limit: Int,
    val total: Int
)

data class ApiResultDto<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class ServiceCategoryDto(
    val id: Long,
    val name: String,
    val description: String?,
    val active: Boolean
)

data class RepairServiceDto(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val shortDescription: String?,
    val description: String?,
    val basePrice: Long,
    val estimatedMinutes: Int,
    val warrantyDays: Int,
    val active: Boolean
)

data class BookingSummaryDto(
    val id: Long,
    val bookingCode: String,
    val status: String,
    val preferredDate: String?,
    val preferredTimeSlot: String?,
    val scheduledAt: String?,
    val totalEstimatedPrice: Long,
    val createdAt: String?
)


data class BookingDetailDto(
    val id: Long,
    val bookingCode: String,
    val customerName: String,
    val customerPhone: String,
    val deviceType: String,
    val issueDescription: String,
    val status: String,
    val totalEstimatedPrice: Long,
    val items: List<BookingItemDto>,
    val statusHistory: List<BookingHistoryDto>
)

data class BookingItemDto(
    val serviceId: Long,
    val serviceName: String,
    val quantity: Int,
    val estimatedPrice: Long
)

data class BookingHistoryDto(
    val fromStatus: String?,
    val toStatus: String,
    val note: String?,
    val createdAt: String?
)

data class ReviewDto(
    val id: Long,
    val bookingCode: String,
    val rating: Int,
    val comment: String?
)

data class AdminUserDto(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
    val role: String,
    val active: Boolean
)

private fun JSONObject.optStringOrNull(name: String): String? = optString(name).takeIf { it.isNotBlank() }

fun JSONObject.toServiceCategoryDto() = ServiceCategoryDto(
    id = optLong("id"),
    name = optString("name"),
    description = optStringOrNull("description"),
    active = optBoolean("active", true)
)

fun JSONObject.toRepairServiceDto() = RepairServiceDto(
    id = optLong("id"),
    categoryId = optLong("categoryId"),
    name = optString("name"),
    shortDescription = optStringOrNull("shortDescription"),
    description = optStringOrNull("description"),
    basePrice = optLong("basePrice"),
    estimatedMinutes = optInt("estimatedMinutes"),
    warrantyDays = optInt("warrantyDays"),
    active = optBoolean("active", true)
)

fun JSONObject.toBookingSummaryDto() = BookingSummaryDto(
    id = optLong("id"),
    bookingCode = optString("bookingCode"),
    status = optString("status"),
    preferredDate = optStringOrNull("preferredDate"),
    preferredTimeSlot = optStringOrNull("preferredTimeSlot"),
    scheduledAt = optStringOrNull("scheduledAt"),
    totalEstimatedPrice = optLong("totalEstimatedPrice"),
    createdAt = optStringOrNull("createdAt")
)

fun JSONObject.toBookingItemDto() = BookingItemDto(
    serviceId = optLong("serviceId"),
    serviceName = optString("serviceName"),
    quantity = optInt("quantity"),
    estimatedPrice = optLong("estimatedPrice")
)

fun JSONObject.toBookingHistoryDto() = BookingHistoryDto(
    fromStatus = optStringOrNull("fromStatus"),
    toStatus = optString("toStatus"),
    note = optStringOrNull("note"),
    createdAt = optStringOrNull("createdAt")
)

fun JSONObject.toBookingDetailDto(): BookingDetailDto {
    val bookingObj = this.optJSONObject("booking") ?: this
    return BookingDetailDto(
        id = bookingObj.optLong("id"),
        bookingCode = bookingObj.optString("bookingCode"),
        customerName = bookingObj.optString("customerName"),
        customerPhone = bookingObj.optString("customerPhone"),
        deviceType = bookingObj.optString("deviceType"),
        issueDescription = bookingObj.optString("issueDescription"),
        status = bookingObj.optString("status"),
        totalEstimatedPrice = bookingObj.optLong("totalEstimatedPrice"),
        items = this.optJSONArray("items")?.let { arr -> List(arr.length()) { i -> arr.getJSONObject(i).toBookingItemDto() } }.orEmpty(),
        statusHistory = this.optJSONArray("statusHistory")?.let { arr -> List(arr.length()) { i -> arr.getJSONObject(i).toBookingHistoryDto() } }.orEmpty()
    )
}

fun JSONObject.toReviewDto() = ReviewDto(
    id = optLong("id"),
    bookingCode = optString("bookingCode"),
    rating = optInt("rating"),
    comment = optStringOrNull("comment")
)

fun JSONObject.toAdminUserDto() = AdminUserDto(
    id = optLong("id"),
    name = optString("name"),
    phone = optStringOrNull("phone"),
    email = optStringOrNull("email"),
    role = optString("role").ifBlank { optString("role") },
    active = optBoolean("active", true)
)

fun JSONObject.itemsArray(name: String): List<JSONObject> {
    val array: JSONArray = optJSONObject(name)?.optJSONArray("items") ?: optJSONArray(name) ?: return emptyList()
    return List(array.length()) { index -> array.getJSONObject(index) }
}

fun JSONObject.toItemsPage(): ApiListResponseDto<JSONObject> {
    val data = optJSONObject("data") ?: optJSONObject("result") ?: this
    val items = data.optJSONArray("items")?.let { arr -> List(arr.length()) { i -> arr.getJSONObject(i) } }.orEmpty()
    return ApiListResponseDto(
        items = items,
        page = data.optInt("page", optInt("page", 1)),
        limit = data.optInt("limit", optInt("limit", 20)),
        total = data.optInt("total", optInt("total", items.size))
    )
}
