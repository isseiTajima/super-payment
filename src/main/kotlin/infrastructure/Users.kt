package jp.co.payment.infrastructure

import jp.co.payment.domain.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement

object Users : IntIdTable("users") {
    val companyName = varchar("company_name", 255)
    val name = varchar("name", 255)
    val email = varchar("email", 255)
    val password = varchar("password", 255)
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    fun toDomain(stmt: InsertStatement<Number>): User = User(
        userId = stmt[id].value,
        companyName = CompanyName(stmt[companyName]),
        userName = Username(stmt[name]),
        email = Email(stmt[email]),
        password = Password(stmt[password]),
    )

    fun toDomain(row: ResultRow): User = User(
        userId = row[id].value,
        companyName = CompanyName(row[companyName]),
        userName = Username(row[name]),
        email = Email(row[email]),
        password = Password(row[password]),
    )
}
