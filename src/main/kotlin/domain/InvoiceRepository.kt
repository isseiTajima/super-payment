package jp.co.payment.domain

import java.time.LocalDate

interface InvoiceRepository {
    fun save(invoice: Invoice): Invoice
    fun findByDueDateBetween(startDate: LocalDate, endDate: LocalDate): List<Invoice>
}