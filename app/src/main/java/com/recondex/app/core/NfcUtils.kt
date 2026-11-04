package com.recondex.app.core

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import java.nio.charset.Charset

object NfcUtils {

    /** Decodifica los records de un NdefMessage a líneas legibles (texto / URI / otros). */
    fun describeMessage(message: NdefMessage): List<String> {
        return message.records.map { record ->
            when {
                record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT) ->
                    "📝 Texto: ${decodeTextRecord(record)}"
                record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_URI) ->
                    "🔗 URI: ${record.toUri()}"
                record.tnf == NdefRecord.TNF_MIME_MEDIA ->
                    "📎 MIME (${String(record.type, Charset.forName("US-ASCII"))}): ${String(record.payload, Charsets.UTF_8)}"
                else -> "❔ TNF=${record.tnf} tipo=${String(record.type, Charsets.UTF_8)} bytes=${record.payload.size}"
            }
        }
    }

    private fun decodeTextRecord(record: NdefRecord): String {
        val payload = record.payload
        val textEncoding = if ((payload[0].toInt() and 0x80) == 0) Charsets.UTF_8 else Charsets.UTF_16
        val languageCodeLength = payload[0].toInt() and 0x3F
        return String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)
    }

    /** Construye un NdefRecord de texto en inglés/es, formato RTD_TEXT estándar. */
    fun createTextRecord(text: String, languageCode: String = "es"): NdefRecord {
        val langBytes = languageCode.toByteArray(Charsets.US_ASCII)
        val textBytes = text.toByteArray(Charsets.UTF_8)
        val payload = ByteArray(1 + langBytes.size + textBytes.size)
        payload[0] = langBytes.size.toByte()
        System.arraycopy(langBytes, 0, payload, 1, langBytes.size)
        System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }
}
