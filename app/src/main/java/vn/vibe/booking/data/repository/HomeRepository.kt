package vn.vibe.booking.data.repository

import org.json.JSONObject
import vn.vibe.booking.data.remote.BookingApi
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.data.remote.RepairServiceApi
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ReviewApi
import vn.vibe.booking.data.remote.ReviewDto
import vn.vibe.booking.data.remote.ServiceCategoryApi
import vn.vibe.booking.data.remote.ServiceCategoryDto
import vn.vibe.booking.data.remote.toBookingSummaryDto
import vn.vibe.booking.data.remote.toItemsPage
import vn.vibe.booking.data.remote.toRepairServiceDto
import vn.vibe.booking.data.remote.toReviewDto
import vn.vibe.booking.data.remote.toServiceCategoryDto
import vn.vibe.booking.domain.model.UiState

interface HomeRepository {
    suspend fun getCategories(token: String, keyword: String? = null, page: Int = 1, limit: Int = 20): UiState<List<ServiceCategoryDto>>
    suspend fun getServices(token: String?, keyword: String? = null, categoryId: Long? = null, page: Int = 1, limit: Int = 20): UiState<List<RepairServiceDto>>
    suspend fun getMyBookings(token: String, status: String? = null, page: Int = 1, limit: Int = 20): UiState<List<BookingSummaryDto>>
    suspend fun getServiceReviews(serviceId: Long, page: Int = 1, limit: Int = 20): UiState<List<ReviewDto>>
}

class HomeRepositoryImpl(
    private val serviceCategoryApi: ServiceCategoryApi,
    private val repairServiceApi: RepairServiceApi,
    private val bookingApi: BookingApi,
    private val reviewApi: ReviewApi
) : HomeRepository {

    override suspend fun getCategories(token: String, keyword: String?, page: Int, limit: Int): UiState<List<ServiceCategoryDto>> =
        runApi {
            val json = JSONObject(serviceCategoryApi.getAdminCategories(keyword, page, limit, token))
            val pageDto = json.toItemsPage()
            pageDto.items.map { it.toServiceCategoryDto() }
        }

    override suspend fun getServices(token: String?, keyword: String?, categoryId: Long?, page: Int, limit: Int): UiState<List<RepairServiceDto>> =
        runApi {
            val json = JSONObject(repairServiceApi.getServices(keyword, categoryId, page, limit))
            val pageDto = json.toItemsPage()
            pageDto.items.map { it.toRepairServiceDto() }
        }

    override suspend fun getMyBookings(token: String, status: String?, page: Int, limit: Int): UiState<List<BookingSummaryDto>> =
        runApi {
            val json = JSONObject(bookingApi.getMyBookings(status, page, limit, token))
            val pageDto = json.toItemsPage()
            pageDto.items.map { it.toBookingSummaryDto() }
        }

    override suspend fun getServiceReviews(serviceId: Long, page: Int, limit: Int): UiState<List<ReviewDto>> =
        runApi {
            val json = JSONObject(reviewApi.getServiceReviews(serviceId, page, limit))
            val pageDto = json.toItemsPage()
            pageDto.items.map { it.toReviewDto() }
        }
}

private inline fun <T> runApi(block: () -> T): UiState<T> = try {
    val data = block()
    if (data is List<*> && data.isEmpty()) UiState.Empty() else UiState.Success(data)
} catch (e: Exception) {
    UiState.Error(e.message ?: "Không thể tải dữ liệu")
}
