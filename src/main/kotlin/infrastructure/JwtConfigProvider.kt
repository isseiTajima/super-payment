package jp.co.payment.infrastructure

import io.ktor.server.application.*

class JwtConfigProvider(application: Application) {
    private val config = application.environment.config.config("jwt")

    val issuer: String = config.property("issuer").getString()
    val audience: String = config.property("audience").getString()
    val secret: String = config.property("secret").getString()
}