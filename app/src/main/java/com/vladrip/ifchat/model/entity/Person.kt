package com.vladrip.ifchat.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    indices = [Index(
        value = ["phoneNumber"],
        unique = true
    ), Index(
        value = ["tag"],
        unique = true
    )]
)
data class Person(
    @PrimaryKey val id: Long,
    val phoneNumber: String,
    val tag: String?,
    val firstName: String,
    val lastName: String?,
    val bio: String?,
    val onlineAt: LocalDateTime
) {
    fun getFullName(): String {
        return "$firstName $lastName"
    }
}