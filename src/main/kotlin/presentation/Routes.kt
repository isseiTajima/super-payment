package jp.co.payment.presentation

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate

fun Route.userRoutes() {
    // DI（Koin）からUserControllerを取得
    val userController: UserController by inject()

    route("/users") {
        // ユーザー登録
        post {
            val request = call.receive<CreateUserRequest>()
            val response = userController.createUser(request)
            call.respond(HttpStatusCode.Created, response)
        }

        // ユーザー認証
        post("/auth") {
            val request = call.receive<AuthRequest>()
            val response = userController.auth(request)
            call.respond(response)
        }
    }
}

fun Route.invoiceRoutes(enableAuth: Boolean = true) {
    // DI（Koin）からInvoiceControllerを取得
    val invoiceController: InvoiceController by inject()
    // 認証の有無を決定するラッパー関数
    val authWrapper: Route.() -> Unit = {
        // 請求書登録API
        post("/invoices") {
            val request = call.receive<CreateInvoiceRequest>()
            val response = invoiceController.createInvoice(request)
            call.respond(HttpStatusCode.Created, response)
        }

        // 請求書一覧API
        get("/invoices") {
            val startDateParam = call.request.queryParameters["startDate"] 
                ?: throw IllegalArgumentException("startDate is required")
            val endDateParam = call.request.queryParameters["endDate"] 
                ?: throw IllegalArgumentException("endDate is required")

            val startDate = LocalDate.parse(startDateParam)
            val endDate = LocalDate.parse(endDateParam)

            val response = invoiceController.getInvoices(startDate, endDate)
            call.respond(response)
        }
    }
    // 認証の有無に応じてルートを設定
    when (enableAuth) {
        true -> authenticate("auth-jwt", strategy = AuthenticationStrategy.Required) { authWrapper() }
        false -> authWrapper()
    }
}
