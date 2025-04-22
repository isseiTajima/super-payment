package jp.co.payment

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jp.co.payment.infrastructure.appModule
import jp.co.payment.presentation.invoiceRoutes
import jp.co.payment.presentation.userRoutes
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.koin
import org.koin.logger.slf4jLogger

/**
 * アプリケーションのエントリーポイント
 */
fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * データベース設定を取得する拡張プロパティ
 * application.confから設定を読み込む（実際の環境では環境変数やSSMなどから取得すること）
 */
val Application.dbConfig get() = environment.config.config("database")

/**
 * JWT設定を取得する拡張プロパティ
 * application.confから設定を読み込む（実際の環境では環境変数やSSMなどから取得すること）
 */
val Application.jwtConfig get() = environment.config.config("jwt")

/**
 * JWT設定を保持するデータクラス
 */
data class JwtConfig(
    val issuer: String,
    val audience: String,
    val secret: String
)

/**
 * アプリケーションのメイン設定
 * Ktorフレームワークによって自動的に呼び出される
 */
fun Application.module() {
    // データベース接続設定
    configureDatabaseConnection()

    // Koin DIフレームワークの設定
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    // JSONシリアライゼーションの設定
    install(ContentNegotiation) {
        json()
    }

    // JWT認証の設定
    configureJwt()

    // テスト環境の判定
    val dbUrl = dbConfig.property("url").getString()
    val isTest = dbUrl.contains("h2:mem:test") ||
            jwtConfig.property("issuer").getString().contains("test")

    // 認証の設定（テスト時は認証をスキップ）
    configureAuthentication(!isTest)

    // ルーティングの設定
    routing {
        userRoutes()
        invoiceRoutes()
    }
}

/**
 * データベース接続を設定する
 */
private fun Application.configureDatabaseConnection() {
    val dbConfig = this.dbConfig
    Database.connect(
        url = dbConfig.property("url").getString(),
        driver = dbConfig.property("driver").getString(),
        user = dbConfig.property("user").getString(),
        password = dbConfig.property("password").getString()
    )
}

/**
 * JWT認証の設定をKoinに登録する
 */
fun Application.configureJwt() {
    val config = environment.config.config("jwt")
    koin {
        modules(module {
            single {
                JwtConfig(
                    issuer = config.property("issuer").getString(),
                    audience = config.property("audience").getString(),
                    secret = config.property("secret").getString()
                )
            }
        })
    }
}

/**
 * 認証の設定
 * @param isAuth 認証を有効にするかどうか（テスト時はfalse）
 */
fun Application.configureAuthentication(isAuth: Boolean = true) {
    install(Authentication) {
        jwt("auth-jwt") {
            // テスト用にフラグによって認証をスキップする分岐
            if (!isAuth) {
                skipWhen { true }
            }

            // JWT設定値の取得
            val config = this@configureAuthentication.jwtConfig
            realm = config.property("realm").getString()
            val issuer = config.property("issuer").getString()
            val audience = config.property("audience").getString()
            val secret = config.property("secret").getString()

            // Authorization: Bearer <token> 形式のヘッダーからトークンを抽出する設定
            authSchemes("Bearer")

            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )

            validate { credential ->
                JWTPrincipal(credential.payload)
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("message" to "認証に失敗しました")
                )
            }
        }
    }
}
