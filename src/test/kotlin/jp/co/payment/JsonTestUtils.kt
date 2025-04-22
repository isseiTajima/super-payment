package jp.co.payment

import io.ktor.client.statement.*
import kotlinx.serialization.json.*

/**
 * テスト用のJSON解析ユーティリティ
 */

// ignoreUnknownKeysをtrueに設定したJSONパーサー
val testJson = Json { ignoreUnknownKeys = true }

/**
 * HTTPレスポンスボディをJsonObjectに解析する
 */
suspend fun HttpResponse.toJsonObject(): JsonObject {
    return testJson.parseToJsonElement(this.bodyAsText()).jsonObject
}

/**
 * HTTPレスポンスボディをJsonArrayに解析する
 */
suspend fun HttpResponse.toJsonArray(): JsonArray {
    return testJson.parseToJsonElement(this.bodyAsText()).jsonArray
}

/**
 * JsonObjectから文字列値を取得する
 */
fun JsonObject.getString(key: String): String? {
    return this[key]?.jsonPrimitive?.content
}

/**
 * JsonObjectから整数値を取得する
 */
fun JsonObject.getInt(key: String): Int? {
    return this[key]?.jsonPrimitive?.intOrNull
}

/**
 * JsonObjectからnull以外の整数値を取得する
 */
fun JsonObject.getIntOrThrow(key: String): Int {
    return this[key]?.jsonPrimitive?.int ?: throw IllegalStateException("キー $key が見つからないか、整数ではありません")
}
