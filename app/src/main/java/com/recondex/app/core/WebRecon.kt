package com.recondex.app.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object WebRecon {

    private val SECURITY_HEADERS = listOf(
        "Strict-Transport-Security", "Content-Security-Policy", "X-Frame-Options",
        "X-Content-Type-Options", "Referrer-Policy", "Permissions-Policy"
    )

    private val COMMON_PATHS = listOf(
        "robots.txt", ".env", ".git/HEAD", "admin", "wp-admin", "backup.zip",
        ".well-known/security.txt", "server-status", "phpinfo.php", "config.php.bak"
    )

    data class HeaderCheck(val header: String, val present: Boolean, val value: String?)
    data class PathCheck(val path: String, val statusCode: Int, val exposed: Boolean)

    private fun normalizeUrl(input: String): String {
        var u = input.trim()
        if (!u.startsWith("http://") && !u.startsWith("https://")) u = "https://$u"
        return u.trimEnd('/')
    }

    suspend fun checkSecurityHeaders(targetUrl: String): List<HeaderCheck> = withContext(Dispatchers.IO) {
        val conn = (URL(normalizeUrl(targetUrl)).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
            instanceFollowRedirects = true
        }
        runCatching {
            conn.connect()
            SECURITY_HEADERS.map { header ->
                val value = conn.getHeaderField(header)
                HeaderCheck(header, value != null, value)
            }
        }.getOrElse { SECURITY_HEADERS.map { HeaderCheck(it, false, null) } }
            .also { runCatching { conn.disconnect() } }
    }

    suspend fun checkCommonPaths(targetUrl: String, onEach: suspend (PathCheck) -> Unit) = coroutineScope {
        val base = normalizeUrl(targetUrl)
        COMMON_PATHS.map { path ->
            async(Dispatchers.IO) {
                val code = runCatching {
                    (URL("$base/$path").openConnection() as HttpURLConnection).run {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 5000
                        instanceFollowRedirects = false
                        connect()
                        responseCode.also { disconnect() }
                    }
                }.getOrDefault(-1)
                onEach(PathCheck(path, code, code == 200))
            }
        }.forEach { it.await() }
    }

    /**
     * Prueba MUY básica de posible SQLi: compara el código/tamaño de respuesta de una URL
     * con parámetro normal vs. con un payload clásico. Solo es un indicio heurístico,
     * no un hallazgo confirmado — pensado para servidores de prueba propios.
     */
    suspend fun probeBasicSqli(urlWithParam: String): String = withContext(Dispatchers.IO) {
        if (!urlWithParam.contains("=")) return@withContext "La URL debe tener al menos un parámetro, ej: https://miapp.test/item?id=1"
        val baseline = fetch(urlWithParam)
        val payloadUrl = urlWithParam + "' OR '1'='1"
        val probed = fetch(payloadUrl)
        val errorUrl = urlWithParam + "'"
        val errorProbe = fetch(errorUrl)

        when {
            baseline == null -> "No se pudo conectar al objetivo."
            errorProbe != null && errorProbe.first >= 500 -> "⚠️ Posible SQLi: la comilla simple (') provocó un error 5xx del servidor (código ${errorProbe.first})."
            probed != null && baseline.second != probed.second && probed.first == 200 -> "⚠️ Posible SQLi: el tamaño de respuesta cambió con el payload OR 1=1 (${baseline.second} → ${probed.second} bytes)."
            else -> "Sin indicios claros de SQLi con esta prueba básica (no concluyente — considera sqlmap para un análisis real)."
        }
    }

    private fun fetch(urlStr: String): Pair<Int, Int>? = runCatching {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
        }
        conn.connect()
        val code = conn.responseCode
        val size = runCatching { conn.inputStream.readBytes().size }.getOrDefault(0)
        conn.disconnect()
        code to size
    }.getOrNull()
}
