package jp.co.payment.application

import jp.co.payment.domain.Invoice
import jp.co.payment.domain.InvoiceRepository
import java.time.LocalDate

/**
 * 請求書を取得するユースケース
 */
class GetInvoicesUseCase(private val invoiceRepository: InvoiceRepository) {
    fun execute(startDate: LocalDate, endDate: LocalDate): List<Invoice> {
        return invoiceRepository.findByDueDateBetween(startDate, endDate)
    }
}