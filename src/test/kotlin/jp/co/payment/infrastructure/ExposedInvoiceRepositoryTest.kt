package jp.co.payment.infrastructure

import jp.co.payment.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

/**
 * ExposedInvoiceRepositoryのテストクラス
 * H2インメモリデータベースを使用してリポジトリの実装をテストします
 */
class ExposedInvoiceRepositoryTest {

    private lateinit var invoiceRepository: InvoiceRepository
    private lateinit var userRepository: UserRepository
    private var testUserId: Int = 0  // テストユーザーのIDを保持する変数

    @BeforeEach
    fun setUp() {
        TestDatabaseFactory.init()
        invoiceRepository = TestDatabaseFactory.createInvoiceRepository()
        userRepository = TestDatabaseFactory.createUserRepository()

        // テストユーザーを作成
        val testUser = User(
            userId = null,
            companyName = CompanyName("Test Company"),
            userName = Username(UUID.randomUUID().toString()),
            email = Email("test@example.com"),
            password = Password("password123")
        )

        // ユーザーを保存し、生成されたIDを取得
        val savedUser = userRepository.save(testUser)
        testUserId = savedUser.userId!!
    }

    /**
     * 各テスト実行後にデータベースをクリーンアップします
     */
    @AfterEach
    fun tearDown() {
        TestDatabaseFactory.cleanup()
    }

    @Test
    @DisplayName("請求書を保存し、正しく保存されることを確認する")
    fun save_invoice() {
        // Given: テスト用の請求書データを作成
        val invoice = createTestInvoice()

        // When: リポジトリを使用して請求書を保存
        val savedInvoice = invoiceRepository.save(invoice)

        // Then: 保存された請求書のIDがnullでないことを確認
        assertThat(savedInvoice.invoiceId).isNotNull
        // Then: 保存された請求書の各フィールドが元の請求書と一致することを確認
        assertThat(savedInvoice.userId).isEqualTo(invoice.userId)
        assertThat(savedInvoice.issueDate.value).isEqualTo(invoice.issueDate.value)
        assertThat(savedInvoice.paymentAmount.value.toInt()).isEqualTo(invoice.paymentAmount.value.toInt())
        assertThat(savedInvoice.fee.value).isEqualTo(invoice.fee.value)
        assertThat(savedInvoice.feeRate.value).isEqualTo(invoice.feeRate.value)
        assertThat(savedInvoice.taxAmount.value).isEqualTo(invoice.taxAmount.value)
        assertThat(savedInvoice.taxRate.value).isEqualTo(invoice.taxRate.value)
        assertThat(savedInvoice.totalAmount.value).isEqualTo(invoice.totalAmount.value)
        assertThat(savedInvoice.dueDate.value).isEqualTo(invoice.dueDate.value)
    }

    @Test
    @DisplayName("期日範囲内の請求書のみが正しく検索されることを確認する")
    fun findByDueDateBetween() {
        // Given: 異なる期日を持つ複数の請求書を作成して保存
        val today = LocalDate.now()
        val invoice1 = createTestInvoice(dueDate = today)
        val invoice2 = createTestInvoice(dueDate = today.plusDays(1))
        val invoice3 = createTestInvoice(dueDate = today.plusDays(5))
        val invoice4 = createTestInvoice(dueDate = today.plusDays(10))

        invoiceRepository.save(invoice1)
        invoiceRepository.save(invoice2)
        invoiceRepository.save(invoice3)
        invoiceRepository.save(invoice4)

        // When: 特定の期日範囲で請求書を検索
        val startDate = today.minusDays(2)
        val endDate = today.plusDays(7)
        val foundInvoices = invoiceRepository.findByDueDateBetween(startDate, endDate)

        // Then: 期日範囲内の請求書のみが取得されていることを確認
        assertThat(foundInvoices).hasSize(3)
        assertThat(foundInvoices.map { it.dueDate.value }).containsExactlyInAnyOrder(
            today,
            today.plusDays(1),
            today.plusDays(5)
        )
    }

    /**
     * テスト用の請求書オブジェクトを作成するヘルパーメソッド
     */
    private fun createTestInvoice(
        issueDate: LocalDate = LocalDate.now(),
        paymentAmount: BigDecimal = BigDecimal("10000"),
        feeRate: BigDecimal = BigDecimal("0.05"),
        taxRate: BigDecimal = BigDecimal("1.10"),
        dueDate: LocalDate = LocalDate.now().plusDays(30)
    ): Invoice {
        return Invoice.create(
            userId = testUserId,
            paymentAmount = PaymentAmount(paymentAmount),
            issueDate = IssueDate(issueDate),
            dueDate = DueDate(dueDate),
            feeRate = FeeRate(feeRate),
            taxRate = TaxRate(taxRate)
        )
    }
}
