package vn.vibe.booking.data.repository

import javax.inject.Inject

import org.json.JSONObject
import vn.vibe.booking.data.local.dao.CategoryDao
import vn.vibe.booking.data.local.dao.ServiceDao
import vn.vibe.booking.data.local.entity.toDto
import vn.vibe.booking.data.local.entity.toEntity
import vn.vibe.booking.data.remote.AdminApi
import vn.vibe.booking.data.remote.AdminUserDto
import vn.vibe.booking.data.remote.BookingApi
import vn.vibe.booking.data.remote.BookingItemRequest
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.data.remote.RepairServiceApi
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ReviewApi
import vn.vibe.booking.data.remote.ReviewDto
import vn.vibe.booking.data.remote.ServiceCategoryDto
import vn.vibe.booking.data.remote.toAdminUserDto
import vn.vibe.booking.data.remote.toBookingSummaryDto
import vn.vibe.booking.data.remote.toItemsPage
import vn.vibe.booking.data.remote.toRepairServiceDto
import vn.vibe.booking.data.remote.toReviewDto
import vn.vibe.booking.data.remote.toServiceCategoryDto
import vn.vibe.booking.data.remote.toBookingDetailDto
import vn.vibe.booking.domain.model.UiState

interface HomeRepository {
    suspend fun getCategories(token: String, keyword: String? = null, page: Int = 1, limit: Int = 20): UiState<List<ServiceCategoryDto>>
    suspend fun createCategory(token: String, name: String, description: String?, active: Boolean): UiState<ServiceCategoryDto>
    suspend fun updateCategory(token: String, id: Long, name: String, description: String?, active: Boolean?): UiState<ServiceCategoryDto>
    suspend fun deleteCategory(token: String, id: Long): UiState<Int>
    suspend fun getServices(token: String?, keyword: String? = null, categoryId: Long? = null, page: Int = 1, limit: Int = 20): UiState<List<RepairServiceDto>>
    suspend fun createService(token: String, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean): UiState<RepairServiceDto>
    suspend fun updateService(token: String, id: Long, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean): UiState<RepairServiceDto>
    suspend fun deleteService(token: String, id: Long): UiState<Int>
    suspend fun createBooking(
        token: String,
        customerName: String,
        customerPhone: String,
        customerEmail: String?,
        deviceType: String,
        deviceBrand: String,
        deviceModel: String,
        issueDescription: String,
        preferredDate: String,
        preferredTimeSlot: String,
        address: String,
        note: String?,
        items: List<BookingItemRequest>
    ): UiState<String>
    suspend fun getMyBookings(token: String, status: String? = null, page: Int = 1, limit: Int = 20): UiState<List<BookingSummaryDto>>
    suspend fun getBookingDetail(token: String, id: Long): UiState<vn.vibe.booking.data.remote.BookingDetailDto>
    suspend fun getAdminBookings(token: String, keyword: String? = null, status: String? = null, customerId: Int? = null, technicianId: Int? = null, page: Int = 1, limit: Int = 20): UiState<List<BookingSummaryDto>>
    suspend fun confirmBooking(token: String, id: Long, note: String): UiState<String>
    suspend fun assignTechnician(token: String, id: Long, technicianId: Long, note: String): UiState<String>
    suspend fun updateBookingStatus(token: String, id: Long, status: String, note: String, scheduledAt: String? = null): UiState<String>
    suspend fun createBookingReview(token: String, bookingId: Long, rating: Int, comment: String): UiState<ReviewDto>
    suspend fun getServiceReviews(serviceId: Long, page: Int = 1, limit: Int = 20): UiState<List<ReviewDto>>
    suspend fun getUsers(token: String, keyword: String? = null, role: String? = null, active: Boolean? = null, page: Int = 1, limit: Int = 20): UiState<List<AdminUserDto>>
    suspend fun createUser(token: String, name: String, phone: String?, email: String?, role: String?, active: Boolean?): UiState<AdminUserDto>
    suspend fun updateUser(token: String, id: Long, name: String?, phone: String?, email: String?, role: String?, active: Boolean?): UiState<AdminUserDto>
    suspend fun deleteUser(token: String, id: Long): UiState<Int>
    suspend fun setUserActive(token: String, id: Long, active: Boolean): UiState<String>
    suspend fun addBookingNote(token: String, id: Long, note: String): UiState<String>
}

class HomeRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi,
    private val serviceCategoryApi: vn.vibe.booking.data.remote.ServiceCategoryApi,
    private val repairServiceApi: RepairServiceApi,
    private val bookingApi: BookingApi,
    private val reviewApi: ReviewApi,
    private val categoryDao: CategoryDao,
    private val serviceDao: ServiceDao
) : HomeRepository {

    override suspend fun getCategories(token: String, keyword: String?, page: Int, limit: Int): UiState<List<ServiceCategoryDto>> = try {
        try {
            val remote = JSONObject(serviceCategoryApi.getAdminCategories(keyword, page, limit, token)).toItemsPage().items.map { it.toServiceCategoryDto() }
            if (keyword.isNullOrEmpty() && page == 1) {
                categoryDao.clearCategories()
                categoryDao.insertCategories(remote.map { it.toEntity() })
            }
            UiState.Success(remote)
        } catch (e: Exception) {
            val local = categoryDao.getAllCategories()
            if (local.isNotEmpty() && keyword.isNullOrEmpty() && page == 1) {
                UiState.Success(local.map { it.toDto() })
            } else {
                UiState.Error(e.message ?: "Network error and no cache")
            }
        }
    } catch (e: Exception) {
        UiState.Error(e.message ?: "Lỗi hệ thống")
    }
    override suspend fun createCategory(token: String, name: String, description: String?, active: Boolean): UiState<ServiceCategoryDto> = runApi { JSONObject(serviceCategoryApi.createCategory(name, description, active, token)).toServiceCategoryDto() }
    override suspend fun updateCategory(token: String, id: Long, name: String, description: String?, active: Boolean?): UiState<ServiceCategoryDto> = runApi { JSONObject(serviceCategoryApi.updateCategory(id, name, description, active ?: true, token)).toServiceCategoryDto() }
    override suspend fun deleteCategory(token: String, id: Long): UiState<Int> = runApi { JSONObject(serviceCategoryApi.deleteCategory(id, token)).optInt("data", 1) }
    override suspend fun getServices(token: String?, keyword: String?, categoryId: Long?, page: Int, limit: Int): UiState<List<RepairServiceDto>> = try {
        try {
            val remote = JSONObject(repairServiceApi.getServices(keyword, categoryId, page, limit)).toItemsPage().items.map { it.toRepairServiceDto() }
            if (keyword.isNullOrEmpty() && categoryId == null && page == 1) {
                serviceDao.clearServices()
                serviceDao.insertServices(remote.map { it.toEntity() })
            }
            UiState.Success(remote)
        } catch (e: Exception) {
            val local = serviceDao.getAllServices()
            if (local.isNotEmpty() && keyword.isNullOrEmpty() && categoryId == null && page == 1) {
                UiState.Success(local.map { it.toDto() })
            } else {
                UiState.Error(e.message ?: "Network error and no cache")
            }
        }
    } catch (e: Exception) {
        UiState.Error(e.message ?: "Lỗi hệ thống")
    }
    override suspend fun createService(token: String, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean): UiState<RepairServiceDto> = runApi {
        JSONObject(repairServiceApi.createService(categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active, token)).toRepairServiceDto()
    }
    override suspend fun updateService(token: String, id: Long, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean): UiState<RepairServiceDto> = runApi {
        JSONObject(repairServiceApi.updateService(id, categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active, token)).toRepairServiceDto()
    }
    override suspend fun deleteService(token: String, id: Long): UiState<Int> = runApi { JSONObject(repairServiceApi.deleteService(id, token)).optInt("data", 1) }
    override suspend fun createBooking(
        token: String,
        customerName: String,
        customerPhone: String,
        customerEmail: String?,
        deviceType: String,
        deviceBrand: String,
        deviceModel: String,
        issueDescription: String,
        preferredDate: String,
        preferredTimeSlot: String,
        address: String,
        note: String?,
        items: List<BookingItemRequest>
    ): UiState<String> = runApi {
        bookingApi.createBooking(
            customerName = customerName,
            customerPhone = customerPhone,
            customerEmail = customerEmail,
            deviceType = deviceType,
            deviceBrand = deviceBrand,
            deviceModel = deviceModel,
            issueDescription = issueDescription,
            preferredDate = preferredDate,
            preferredTimeSlot = preferredTimeSlot,
            address = address,
            note = note,
            items = items,
            token = token
        )
    }
    override suspend fun getMyBookings(token: String, status: String?, page: Int, limit: Int): UiState<List<BookingSummaryDto>> = runApi { JSONObject(bookingApi.getMyBookings(status, page, limit, token)).toItemsPage().items.map { it.toBookingSummaryDto() } }
    override suspend fun getBookingDetail(token: String, id: Long): UiState<vn.vibe.booking.data.remote.BookingDetailDto> = runApi { JSONObject(bookingApi.getBookingDetail(id, token)).let { it.optJSONObject("result") ?: it.optJSONObject("data") ?: it }.toBookingDetailDto() }
    override suspend fun getAdminBookings(token: String, keyword: String?, status: String?, customerId: Int?, technicianId: Int?, page: Int, limit: Int): UiState<List<BookingSummaryDto>> = runApi { JSONObject(bookingApi.getAdminBookings(keyword, status, customerId, technicianId, page, limit, token)).toItemsPage().items.map { it.toBookingSummaryDto() } }
    override suspend fun confirmBooking(token: String, id: Long, note: String): UiState<String> = runApi { bookingApi.confirmBooking(id, note, token) }
    override suspend fun assignTechnician(token: String, id: Long, technicianId: Long, note: String): UiState<String> = runApi { bookingApi.assignTechnician(id, technicianId, note, token) }
    override suspend fun updateBookingStatus(token: String, id: Long, status: String, note: String, scheduledAt: String?): UiState<String> = runApi { bookingApi.updateStatus(id, status, note, scheduledAt, token) }
    override suspend fun createBookingReview(token: String, bookingId: Long, rating: Int, comment: String): UiState<ReviewDto> = runApi { JSONObject(reviewApi.createReview(bookingId, rating, comment, token)).toReviewDto() }
    override suspend fun getServiceReviews(serviceId: Long, page: Int, limit: Int): UiState<List<ReviewDto>> = runApi { JSONObject(reviewApi.getServiceReviews(serviceId, page, limit)).toItemsPage().items.map { it.toReviewDto() } }
    override suspend fun getUsers(token: String, keyword: String?, role: String?, active: Boolean?, page: Int, limit: Int): UiState<List<AdminUserDto>> = runApi { JSONObject(adminApi.getUsers(keyword, role, active, page, limit, token)).toItemsPage().items.map { it.toAdminUserDto() } }
    override suspend fun createUser(token: String, name: String, phone: String?, email: String?, role: String?, active: Boolean?): UiState<AdminUserDto> = runApi { JSONObject(adminApi.createUser(name, phone, email, role, active, token)).toAdminUserDto() }
    override suspend fun updateUser(token: String, id: Long, name: String?, phone: String?, email: String?, role: String?, active: Boolean?): UiState<AdminUserDto> = runApi { JSONObject(adminApi.updateUser(id, name, phone, email, role, active, token)).toAdminUserDto() }
    override suspend fun deleteUser(token: String, id: Long): UiState<Int> = runApi { JSONObject(adminApi.deleteUser(id, token)).optInt("data", 1) }
    override suspend fun setUserActive(token: String, id: Long, active: Boolean): UiState<String> = runApi { adminApi.setUserActive(id, active, token) }
    override suspend fun addBookingNote(token: String, id: Long, note: String): UiState<String> = runApi { bookingApi.addNote(id, note, token) }
}

private inline fun <T> runApi(block: () -> T): UiState<T> = try { UiState.Success(block()) } catch (e: Exception) { UiState.Error(e.message ?: "Không thể tải dữ liệu") }
