package jp.co.payment.infrastructure

import jp.co.payment.domain.Invoice
import jp.co.payment.domain.InvoiceRepository
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Exposed を使用したInvoiceRepositoryの実装。
 * 請求書エンティティのデータベース操作を提供します。
 */
class ExposedInvoiceRepository : InvoiceRepository {
    /**
     * 請求書を永続化します。
     */
    override fun save(invoice: Invoice): Invoice {
        return transaction {
            Invoices.insert {
                it[userId] = invoice.userId
                it[issueDate] = invoice.issueDate.value
                it[paymentAmount] = invoice.paymentAmount.value
                it[fee] = invoice.fee.value
                it[feeRate] = invoice.feeRate.value
                it[taxAmount] = invoice.taxAmount.value
                it[taxRate] = invoice.taxRate.value
                it[totalAmount] = invoice.totalAmount.value
                it[dueDate] = invoice.dueDate.value
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
            }.let {
                Invoices.toDomain(it)
            }
        }
    }

    /**
     * 期日で請求書を検索し、請求書を返します。
     */
    override fun findByDueDateBetween(startDate: LocalDate, endDate: LocalDate): List<Invoice> {
        return transaction {
            Invoices.select {
                (Invoices.dueDate greaterEq startDate) and (Invoices.dueDate lessEq endDate)
            }.map { Invoices.toDomain(it) }
        }
    }
}
