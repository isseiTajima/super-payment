package jp.co.payment.domain

data class User(
    val userId: UserId?,
    val companyName: CompanyName,
    val userName: Username,
    val email: Email,
    val password: Password,
)
typealias UserId = Int
