package com.issuetrax.app.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val login: String,
    val name: String?,
    val email: String?,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: UserType = UserType.USER
)

@Serializable
enum class UserType {
    USER, ORGANIZATION, BOT
}