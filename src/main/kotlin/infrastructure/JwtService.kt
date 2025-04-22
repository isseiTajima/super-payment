package jp.co.payment.infrastructure

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import jp.co.payment.JwtConfig
import jp.co.payment.domain.User
import java.util.*

class JwtService(private val jwtConfig: JwtConfig) {
    private val validityInMs = 3_600_000 // 1時間（ミリ秒）

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(jwtConfig.issuer)
            .withAudience(jwtConfig.audience)
            .withClaim("userId", user.userId)
            .withClaim("username", user.userName.value)
            .withClaim("email", user.email.value)
            .withExpiresAt(getExpiration())
            .sign(Algorithm.HMAC256(jwtConfig.secret))
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}
