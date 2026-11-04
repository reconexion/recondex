package com.recondex.app.core

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

object HashLab {

    // Diccionario educativo pequeño — contraseñas más comunes a nivel mundial.
    val COMMON_PASSWORDS = listOf(
        "123456", "password", "123456789", "12345678", "12345", "qwerty", "abc123",
        "111111", "123123", "admin", "letmein", "welcome", "monkey", "login",
        "princess", "qwerty123", "solo", "1234", "iloveyou", "football", "123321",
        "000000", "dragon", "master", "superman", "trustno1", "1q2w3e4r", "sunshine",
        "shadow", "michael", "jennifer", "123abc", "passw0rd", "hello", "freedom",
        "whatever", "qazwsx", "admin123", "root", "toor", "guest", "test",
    )

    fun identifyHashType(hash: String): String {
        val clean = hash.trim()
        return when (clean.length) {
            32 -> "MD5 (probable)"
            40 -> "SHA-1 (probable)"
            64 -> "SHA-256 (probable)"
            128 -> "SHA-512 (probable)"
            else -> "Longitud ${clean.length}: tipo no reconocido"
        }
    }

    private fun digestHex(algorithm: String, text: String): String {
        val bytes = MessageDigest.getInstance(algorithm).digest(text.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /** Intenta crackear por diccionario probando MD5/SHA-1/SHA-256/SHA-512 de cada palabra. */
    suspend fun crackHash(hash: String, wordlist: List<String> = COMMON_PASSWORDS): Pair<String, String>? = withContext(Dispatchers.Default) {
        val target = hash.trim().lowercase()
        val algorithms = listOf("MD5", "SHA-1", "SHA-256", "SHA-512")
        for (word in wordlist) {
            for (algo in algorithms) {
                if (digestHex(algo, word) == target) return@withContext word to algo
            }
        }
        null
    }

    data class AuthAttemptResult(val username: String, val password: String, val success: Boolean)

    /** Fuerza bruta simple de HTTP Basic Auth contra un endpoint propio de prueba. */
    suspend fun bruteForceBasicAuth(
        url: String,
        usernames: List<String>,
        passwords: List<String>,
        onAttempt: suspend (AuthAttemptResult) -> Unit
    ) = coroutineScope {
        val combos = usernames.flatMap { u -> passwords.map { p -> u to p } }
        combos.map { (user, pass) ->
            async(Dispatchers.IO) {
                val success = runCatching {
                    val creds = Base64.encodeToString("$user:$pass".toByteArray(), Base64.NO_WRAP)
                    val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        setRequestProperty("Authorization", "Basic $creds")
                        connectTimeout = 5000
                        readTimeout = 5000
                    }
                    conn.connect()
                    val code = conn.responseCode
                    conn.disconnect()
                    code in 200..299
                }.getOrDefault(false)
                onAttempt(AuthAttemptResult(user, pass, success))
            }
        }.forEach { it.await() }
    }
}
