package jp.co.payment.presentation

import jp.co.payment.application.CreateInvoiceUseCase
import jp.co.payment.application.GetInvoicesUseCase
import jp.co.payment.domain.*
import jp.co.payment.infrastructure.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 請求書関連のHTTPリクエストを処理するコントローラー。
 * 請求書の作成と取得のためのエンドポイントを提供します。
 */
class InvoiceController(
    private val createInvoiceUseCase: CreateInvoiceUseCase,
    private val getInvoicesUseCase: GetInvoicesUseCase
) {
    /**
     * システムに新しい請求書を作成します。
     */
    fun createInvoice(request: CreateInvoiceRequest): CreateInvoiceResponse {
        return createInvoiceUseCase.execute(request.toDomain()).let {
            CreateInvoiceResponse.fromDomain(it)
        }
    }

    /**
     * 指定された日付範囲内の請求書を取得します。
     */
    fun getInvoices(startDate: LocalDate, endDate: LocalDate): List<InvoiceResponse> {
        return getInvoicesUseCase.execute(startDate, endDate).map {
            InvoiceResponse.fromDomain(it)
        }
    }
}

/**
 * 請求書作成リクエスト用オブジェクト。
 */
@Serializable
data class CreateInvoiceRequest(
    val paymentAmount: String,
    val userId: Int,
    @Serializable(with = LocalDateSerializer::class)
    val issueDate: LocalDate,
    val fee: String,
    val feeRate: String,
    val taxRate: String,
    @Serializable(with = LocalDateSerializer::class)
    val dueDate: LocalDate
) {
    /**
     * リクエストをドメインのInvoiceオブジェクトに変換します。
     * ドメイン変換時に手数料、税額、合計金額の計算を行います。
     */
    fun toDomain(): Invoice = PaymentAmount.fromString(paymentAmount).run {
        Fee.calculate(
            paymentAmount = this,
            feeRate = FeeRate(BigDecimal(feeRate))
        ).let { fee ->
            TaxAmount.calculate(
                fee = fee,
                taxRate = TaxRate(BigDecimal(taxRate))
            ).let { tax ->
                Invoice(
                    invoiceId = null,
                    userId = userId,
                    issueDate = IssueDate(issueDate),
                    paymentAmount = this,
                    fee = fee,
                    feeRate = FeeRate(BigDecimal(feeRate)),
                    taxAmount = tax,
                    taxRate = TaxRate(BigDecimal(taxRate)),
                    totalAmount = TotalAmount.of(
                        this, fee, tax
                    ),
                    dueDate = DueDate(dueDate)
                )
            }
        }
    }
}


/**
 * 請求書作成レスポンス用オブジェクト。
 */
@Serializable
data class CreateInvoiceResponse(
    val id: Int?,
    val issueDate: String,
    val paymentAmount: String,
    val fee: String,
    val feeRate: String,
    val taxAmount: String,
    val taxRate: String,
    val totalAmount: String,
    val dueDate: String
) {
    companion object {
        /**
         * ドメインのInvoiceオブジェクトからレスポンスを作成します。
         */
        fun fromDomain(invoice: Invoice): CreateInvoiceResponse =
            CreateInvoiceResponse(
                id = invoice.invoiceId,
                issueDate = invoice.issueDate.value.toString(),
                paymentAmount = invoice.paymentAmount.value.toString(),
                fee = invoice.fee.value.toString(),
                feeRate = invoice.feeRate.value.toString(),
                taxAmount = invoice.taxAmount.value.toString(),
                taxRate = invoice.taxRate.value.toString(),
                totalAmount = invoice.totalAmount.value.toString(),
                dueDate = invoice.dueDate.value.toString()
            )
    }
}

/**
 * 請求書レスポンス用オブジェクト。
 */
@Serializable
data class InvoiceResponse(
    val id: Int?,
    val issueDate: String,
    val paymentAmount: String,
    val fee: String,
    val feeRate: String,
    val taxAmount: String,
    val taxRate: String,
    val totalAmount: String,
    val dueDate: String
) {
    companion object {
        /**
         * ドメインのInvoiceオブジェクトからレスポンスを作成します。
         */
        fun fromDomain(invoice: Invoice): InvoiceResponse =
            InvoiceResponse(
                id = invoice.invoiceId,
                issueDate = invoice.issueDate.value.toString(),
                paymentAmount = invoice.paymentAmount.value.toString(),
                fee = invoice.fee.value.toString(),
                feeRate = invoice.feeRate.value.toString(),
                taxAmount = invoice.taxAmount.value.toString(),
                taxRate = invoice.taxRate.value.setScale(2, java.math.RoundingMode.HALF_UP).toString(),
                totalAmount = invoice.totalAmount.value.toString(),
                dueDate = invoice.dueDate.value.toString()
            )
    }
}
