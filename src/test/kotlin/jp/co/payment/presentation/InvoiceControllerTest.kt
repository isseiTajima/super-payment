package jp.co.payment.presentation

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import jp.co.payment.*
import jp.co.payment.infrastructure.TestDatabaseFactory
import kotlinx.serialization.json.jsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class InvoiceControllerTest {

    @BeforeEach
    fun setUp() {
        // H2データベースの初期化
        TestDatabaseFactory.init()
    }

    @AfterEach
    fun tearDown() {
        // テスト後のクリーンアップ
        TestDatabaseFactory.cleanup()
        // Koinを停止
        KoinTestUtils.stopKoinAfterTest()
    }

    @Test
    @DisplayName("請求書作成が成功すること")
    fun post_createInvoice() = testApplication {
        // Given - ユーザーを作成する
        val createUserRequest = CreateUserRequest(
            username = "testuser",
            password = "password123",
            companyName = "テスト会社",
            email = "test@example.com"
        )

        // HTTPリクエストを介してユーザーを作成
        val userResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(CreateUserRequest.serializer(), createUserRequest))
        }

        // ユーザーIDを取得するためにレスポンスを解析
        val userResponseBody = userResponse.toJsonObject()
        val userId = userResponseBody.getIntOrThrow("id")

        val today = LocalDate.now()
        val dueDate = today.plusDays(30)

        val invoiceRequest = CreateInvoiceRequest(
            paymentAmount = "10000",
            userId = userId,
            issueDate = today,
            fee = "400",
            feeRate = "0.04",
            taxRate = "1.10",
            dueDate = dueDate
        )

        // When - HTTPリクエストを介して請求書を作成
        val invoiceResponse = client.post("/invoices") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(CreateInvoiceRequest.serializer(), invoiceRequest))
        }

        // Then
        assertThat(invoiceResponse.status).isEqualTo(HttpStatusCode.Created)

        // レスポンスボディを解析
        val responseBody = invoiceResponse.toJsonObject()

        // レスポンスフィールドを検証
        assertThat(responseBody.getInt("id")).isNotNull()
        assertThat(responseBody.getString("paymentAmount")).isEqualTo("10000.00")
        assertThat(responseBody.getString("fee")).isEqualTo("400.00")
        assertThat(responseBody.getString("feeRate")).isEqualTo("0.04")
        assertThat(responseBody.getString("taxAmount")).isEqualTo("40.00")
        assertThat(responseBody.getString("taxRate")).isEqualTo("1.10")
        assertThat(responseBody.getString("totalAmount")).isEqualTo("10440.00")
    }

    @Test
    @DisplayName("請求書取得が成功すること")
    fun get_getInvoice() = testApplication {
        // Given - ユーザーを作成する
        val createUserRequest = CreateUserRequest(
            username = "testuser",
            password = "password123",
            companyName = "テスト会社",
            email = "test@example.com"
        )

        // ユーザーを作成
        val userResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(CreateUserRequest.serializer(), createUserRequest))
        }

        // ユーザーIDを取得するためにレスポンスを解析
        val userResponseBody = userResponse.toJsonObject()
        val userId = userResponseBody.getIntOrThrow("id")

        val today = LocalDate.now()
        val dueDate = today.plusDays(30)

        // 請求書を作成
        val createRequest = CreateInvoiceRequest(
            paymentAmount = "10000",
            userId = userId,
            issueDate = today,
            fee = "400",
            feeRate = "0.04",
            taxRate = "1.10",
            dueDate = dueDate
        )

        // HTTPリクエストを介して請求書を作成
        client.post("/invoices") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(CreateInvoiceRequest.serializer(), createRequest))
        }

        // When - HTTPリクエストを介して請求書を取得
        val startDate = today.minusDays(1)
        val endDate = today.plusDays(31)

        val invoicesResponse = client.get("/invoices") {
            url {
                parameters.append("startDate", startDate.toString())
                parameters.append("endDate", endDate.toString())
            }
        }

        // Then
        assertThat(invoicesResponse.status).isEqualTo(HttpStatusCode.OK)

        // レスポンスボディを配列として解析
        val responseArray = invoicesResponse.toJsonArray()

        // レスポンスを検証
        assertThat(responseArray.size).isEqualTo(1)

        val invoice = responseArray[0].jsonObject
        assertThat(invoice.getInt("id")).isNotNull()
        assertThat(invoice.getString("paymentAmount")).isEqualTo("10000.00")
        assertThat(invoice.getString("feeRate")).isEqualTo("0.04")
        assertThat(invoice.getString("taxRate")).isEqualTo("1.10")
    }
}
