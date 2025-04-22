package jp.co.payment.application

import jp.co.payment.domain.Invoice
import jp.co.payment.domain.InvoiceRepository

/**
 * 新しい請求書を作成するユースケース。
 */
class CreateInvoiceUseCase(private val invoiceRepository: InvoiceRepository) {
    /**
     * 新しい請求書を作成します。
     */
    fun execute(invoice: Invoice): Invoice {
        return invoiceRepository.save(invoice)
    }
}
