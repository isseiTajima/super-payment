package jp.co.payment.infrastructure

import jp.co.payment.domain.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.InsertStatement

object Invoices : IntIdTable("invoices") {
    val userId = integer("user_id").references(Users.id)
    val issueDate = date("issue_date")
    val paymentAmount = decimal("payment_amount", 15, 2)
    val fee = decimal("fee", 15, 2)
    val feeRate = decimal("fee_rate", 5, 2)
    val taxAmount = decimal("tax_amount", 15, 2)
    val taxRate = decimal("tax_rate", 5, 2)
    val totalAmount = decimal("total_amount", 15, 2)
    val dueDate = date("payment_due_date")
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")

    fun toDomain(stmt: InsertStatement<Number>): Invoice = Invoice(
        invoiceId = stmt[id].value,
        userId = stmt[userId],
        issueDate = IssueDate(stmt[issueDate]),
        paymentAmount = PaymentAmount(stmt[paymentAmount]),
        fee = Fee.calculate(
            PaymentAmount(stmt[paymentAmount]),
            FeeRate(stmt[feeRate])
        ),
        feeRate = FeeRate(stmt[feeRate]),
        taxAmount = TaxAmount.calculate(
            Fee.calculate(
                PaymentAmount(stmt[paymentAmount]),
                FeeRate(stmt[feeRate])
            ), TaxRate(stmt[taxRate])
        ),
        taxRate = TaxRate(stmt[taxRate]),
        totalAmount = TotalAmount(stmt[totalAmount]),
        dueDate = DueDate(stmt[dueDate])
    )

    fun toDomain(row: ResultRow): Invoice = Invoice(
        invoiceId = row[id].value,
        userId = row[userId],
        issueDate = IssueDate(row[issueDate]),
        paymentAmount = PaymentAmount(row[paymentAmount]),
        fee = Fee.calculate(
            PaymentAmount(row[paymentAmount]),
            FeeRate(row[feeRate])
        ),
        feeRate = FeeRate(row[feeRate]),
        taxAmount = TaxAmount.calculate(
            Fee.calculate(
                PaymentAmount(row[paymentAmount]),
                FeeRate(row[feeRate])
            ), TaxRate(row[taxRate])
        ),
        taxRate = TaxRate(row[taxRate]),
        totalAmount = TotalAmount(row[totalAmount]),
        dueDate = DueDate(row[dueDate])
    )
}
