package com.recondex.app.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.SoundFx
import com.recondex.app.ui.components.CompleteToolFooter
import com.recondex.app.ui.components.ToolScaffold

private data class BleDevice(val name: String, val address: String, val rssi: Int)

@Composable
fun BleScannerScreen(progress: ProgressStore) {
    val context = LocalContext.current
    val adapter = remember {
        (context.getSystemService(android.content.Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }

    var scanning by remember { mutableStateOf(false) }
    val devices = remember { mutableStateOf(listOf<BleDevice>()) }
    var gattInfo by remember { mutableStateOf<String?>(null) }
    var activeGatt by remember { mutableStateOf<BluetoothGatt?>(null) }

    val neededPermissions = remember {
        if (Build.VERSION.SDK_INT >= 31) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    var hasPermissions by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        hasPermissions = results.values.all { it }
    }

    val scanCallback = remember {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val name = result.device.name ?: "Dispositivo sin nombre"
                val entry = BleDevice(name, result.device.address, result.rssi)
                val current = devices.value
                devices.value = if (current.any { it.address == entry.address }) {
                    current.map { if (it.address == entry.address) entry else it }
                } else {
                    current + entry
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activeGatt?.close()
            runCatching { adapter?.bluetoothLeScanner?.stopScan(scanCallback) }
        }
    }

    ToolScaffold(title = "Escáner BLE") {
        LazyColumn {
            item {
                Text("Escanea dispositivos Bluetooth Low Energy cercanos y explora sus servicios GATT.", style = MaterialTheme.typography.bodyMedium)
            }
            item {
                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        if (!hasPermissions) {
                            permissionLauncher.launch(neededPermissions)
                            return@Button
                        }
                        if (scanning) {
                            runCatching { adapter?.bluetoothLeScanner?.stopScan(scanCallback) }
                            scanning = false
                        } else {
                            devices.value = emptyList()
                            runCatching { adapter?.bluetoothLeScanner?.startScan(scanCallback) }
                            scanning = true
                            SoundFx.tap()
                        }
                    }
                ) { Text(if (scanning) "Detener escaneo" else "Iniciar escaneo BLE") }
            }
            item { Divider(modifier = Modifier.padding(vertical = 10.dp)) }
            items(devices.value) { device ->
                Text(
                    "📡 ${device.name}  ·  ${device.address}  ·  RSSI ${device.rssi} dBm",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (!hasPermissions) return@clickable
                            SoundFx.tap()
                            gattInfo = "Conectando..."
                            val bluetoothDevice = adapter?.getRemoteDevice(device.address)
                            activeGatt = bluetoothDevice?.connectGatt(context, false, object : BluetoothGattCallback() {
                                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                                        gatt.discoverServices()
                                    }
                                }
                                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                                    gattInfo = gatt.services.joinToString("\n") { service ->
                                        "Service ${service.uuid}\n" + service.characteristics.joinToString("\n") { "  · char ${it.uuid}" }
                                    }
                                    SoundFx.success()
                                }
                            })
                        }
                        .padding(vertical = 8.dp)
                )
                Divider()
            }
            gattInfo?.let { info ->
                item {
                    Text("Servicios GATT:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 10.dp))
                    Text(info, style = MaterialTheme.typography.bodyMedium)
                }
            }
            item { CompleteToolFooter(progress, toolId = "ble", xp = 15) }
        }
    }
}
