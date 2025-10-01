package com.issuetrax.app.domain.entity

data class User(
    val id: Long,
    val login: String,
    val name: String?,
    val email: String?,
    val avatarUrl: String,
    val htmlUrl: String,
    val type: UserType = UserType.USER
)

enum class UserType {
    USER, ORGANIZATION, BOT
}