package vn.vibe.booking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.vibe.booking.data.remote.RepairServiceDto

@Entity(tableName = "repair_services")
data class RepairServiceEntity(
    @PrimaryKey
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

fun RepairServiceDto.toEntity() = RepairServiceEntity(
    id = id,
    categoryId = categoryId,
    name = name,
    shortDescription = shortDescription,
    description = description,
    basePrice = basePrice,
    estimatedMinutes = estimatedMinutes,
    warrantyDays = warrantyDays,
    active = active
)

fun RepairServiceEntity.toDto() = RepairServiceDto(
    id = id,
    categoryId = categoryId,
    name = name,
    shortDescription = shortDescription,
    description = description,
    basePrice = basePrice,
    estimatedMinutes = estimatedMinutes,
    warrantyDays = warrantyDays,
    active = active
)
