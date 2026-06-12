package vn.vibe.booking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.vibe.booking.data.remote.ServiceCategoryDto

@Entity(tableName = "service_categories")
data class ServiceCategoryEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    val active: Boolean
)

fun ServiceCategoryDto.toEntity() = ServiceCategoryEntity(
    id = id,
    name = name,
    description = description,
    active = active
)

fun ServiceCategoryEntity.toDto() = ServiceCategoryDto(
    id = id,
    name = name,
    description = description,
    active = active
)
