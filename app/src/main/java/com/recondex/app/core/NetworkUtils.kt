package com.recondex.app.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket

object NetworkUtils {

    val COMMON_PORTS = listOf(21, 22, 23, 25, 53, 80, 110, 139, 143, 443, 445, 587, 993, 995, 3306, 3389, 5900, 8080, 8443)

    val SERVICE_NAMES = mapOf(
        21 to "FTP", 22 to "SSH", 23 to "Telnet", 25 to "SMTP", 53 to "DNS",
        80 to "HTTP", 110 to "POP3", 139 to "NetBIOS", 143 to "IMAP", 443 to "HTTPS",
        445 to "SMB", 587 to "SMTP(TLS)", 993 to "IMAPS", 995 to "POP3S",
        3306 to "MySQL", 3389 to "RDP", 5900 to "VNC", 8080 to "HTTP-alt", 8443 to "HTTPS-alt"
    )

    fun serviceNameFor(port: Int): String = SERVICE_NAMES[port] ?: "desconocido"

    /** IP local del dispositivo en la interfaz WiFi/datos activa, o null si no se encuentra. */
    fun getLocalIPv4(): String? {
        return runCatching {
            NetworkInterface.getNetworkInterfaces().toList()
                .filter { !it.isLoopback && it.isUp }
                .flatMap { it.interfaceAddresses }
                .map { it.address }
                .firstOrNull { it.hostAddress?.contains(':') == false }
                ?.hostAddress
        }.getOrNull()
    }

    /** Dado "192.168.1.23" devuelve "192.168.1" (asumiendo subred /24, lo más común en WiFi doméstico). */
    fun subnetBaseOf(ip: String): String = ip.substringBeforeLast('.')

    suspend fun isPortOpen(host: String, port: Int, timeoutMs: Int = 400): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMs)
                true
            }
        }.getOrDefault(false)
    }

    /** Escanea una subred /24 probando conexión a un puerto "sonda" común en cada host; llama onFound por cada host vivo. */
    suspend fun discoverHosts(subnetBase: String, onFound: suspend (String) -> Unit) = coroutineScope {
        val semaphore = Semaphore(48)
        val probePorts = listOf(80, 443, 22, 445, 139)
        (1..254).map { lastOctet ->
            async(Dispatchers.IO) {
                semaphore.withPermit {
                    val host = "$subnetBase.$lastOctet"
                    val alive = probePorts.any { port -> isPortOpen(host, port, 250) }
                    if (alive) onFound(host)
                }
            }
        }.forEach { it.await() }
    }

    /** Escanea un rango de puertos sobre un host objetivo; llama onOpenPort por cada puerto abierto. */
    suspend fun scanPorts(host: String, ports: List<Int>, onOpenPort: suspend (Int) -> Unit) = coroutineScope {
        val semaphore = Semaphore(64)
        ports.map { port ->
            async(Dispatchers.IO) {
                semaphore.withPermit {
                    if (isPortOpen(host, port, 500)) onOpenPort(port)
                }
            }
        }.forEach { it.await() }
    }
}
