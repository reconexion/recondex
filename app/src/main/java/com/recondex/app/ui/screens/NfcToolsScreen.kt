package com.recondex.app.ui.screens

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.recondex.app.core.NfcUtils
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.core.TOOLS
import com.recondex.app.ui.components.CompleteToolFooter
import com.recondex.app.ui.components.DuoButton
import com.recondex.app.ui.components.ExplainerCard
import com.recondex.app.ui.components.ToolScaffold
import com.recondex.app.ui.theme.DuoBlue

@Composable
fun NfcToolsScreen(progress: ProgressStore) {
    val tool = remember { TOOLS.first { it.id == "nfc" } }
    val context = LocalContext.current
    val activity = context as? Activity
    val adapter = remember { NfcAdapter.getDefaultAdapter(context) }

    var mode by remember { mutableStateOf("leer") } // "leer" o "escribir"
    var writeText by remember { mutableStateOf("") }
    val readLines = remember { mutableStateOf(listOf<String>()) }
    var status by remember { mutableStateOf("Acerca un tag NFC para leer...") }

    DisposableEffect(mode, writeText) {
        if (activity != null && adapter != null) {
            val callback = NfcAdapter.ReaderCallback { tag: Tag ->
                if (mode == "leer") {
                    handleRead(tag) { lines ->
                        readLines.value = lines
                        status = "Tag leído ✔"
                        SoundFx.success()
                    }
                } else {
                    handleWrite(tag, writeText) { ok ->
                        status = if (ok) "Escritura exitosa ✔" else "No se pudo escribir (¿tag de solo lectura?)"
                        if (ok) SoundFx.success() else SoundFx.error()
                    }
                }
            }
            adapter.enableReaderMode(
                activity, callback,
                NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NFC_BARCODE,
                null
            )
        }
        onDispose { runCatching { adapter?.disableReaderMode(activity) } }
    }

    ToolScaffold(title = "Herramientas NFC") {
        LazyColumn {
            item { ExplainerCard(tool.whatItDoes, tool.example, tool.accent) }
            item {
                if (adapter == null) {
                    Text(
                        "Este dispositivo no tiene NFC disponible.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                } else {
                    Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DuoButton(
                            text = "Modo leer",
                            color = if (mode == "leer") tool.accent else DuoBlue.copy(alpha = 0.35f),
                            modifier = Modifier.weight(1f),
                            onClick = { mode = "leer"; status = "Acerca un tag NFC para leer..." }
                        )
                        DuoButton(
                            text = "Modo escribir",
                            color = if (mode == "escribir") tool.accent else DuoBlue.copy(alpha = 0.35f),
                            modifier = Modifier.weight(1f),
                            onClick = { mode = "escribir"; status = "Escribe el texto y acerca el tag" }
                        )
                    }
                }
            }
            if (mode == "escribir") {
                item {
                    OutlinedTextField(
                        value = writeText,
                        onValueChange = { writeText = it },
                        label = { Text("Texto a escribir en el tag") },
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    )
                }
            }
            item { Text(status, modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.titleMedium) }
            item { Divider(modifier = Modifier.padding(vertical = 10.dp)) }
            items(readLines.value) { line -> Text(line, modifier = Modifier.padding(vertical = 2.dp)) }
            item { CompleteToolFooter(progress, toolId = "nfc") }
        }
    }
}

private fun handleRead(tag: Tag, onResult: (List<String>) -> Unit) {
    val ndef = Ndef.get(tag)
    if (ndef == null) {
        onResult(listOf("Este tag no soporta NDEF."))
        return
    }
    runCatching {
        ndef.connect()
        val message: NdefMessage? = ndef.cachedNdefMessage ?: ndef.ndefMessage
        ndef.close()
        if (message == null) onResult(listOf("Tag vacío (sin mensaje NDEF)."))
        else onResult(NfcUtils.describeMessage(message))
    }.onFailure { onResult(listOf("Error leyendo tag: ${it.message}")) }
}

private fun handleWrite(tag: Tag, text: String, onResult: (Boolean) -> Unit) {
    val ndef = Ndef.get(tag)
    if (ndef == null) {
        onResult(false)
        return
    }
    val message = NdefMessage(arrayOf(NfcUtils.createTextRecord(text)))
    val ok = runCatching {
        ndef.connect()
        if (!ndef.isWritable) return@runCatching false
        ndef.writeNdefMessage(message)
        ndef.close()
        true
    }.getOrDefault(false)
    onResult(ok)
}
