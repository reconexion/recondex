package com.recondex.app.core

import android.content.Context
import android.content.pm.PackageManager
import java.security.MessageDigest

data class AppSummary(val label: String, val packageName: String, val isSystemApp: Boolean)

data class AppDetail(
    val label: String,
    val packageName: String,
    val versionName: String?,
    val minSdk: Int,
    val targetSdk: Int,
    val debuggable: Boolean,
    val permissions: List<String>,
    val dangerousPermissions: List<String>,
    val exportedActivities: List<String>,
    val exportedServices: List<String>,
    val exportedReceivers: List<String>,
    val signatureSha256: String?,
)

object AppAnalyzer {

    private val DANGEROUS_HINTS = listOf(
        "CAMERA", "LOCATION", "CONTACTS", "SMS", "CALL_LOG", "RECORD_AUDIO",
        "READ_PHONE_STATE", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE",
        "BODY_SENSORS", "READ_CALENDAR", "WRITE_CALENDAR", "ACCESS_BACKGROUND_LOCATION"
    )

    fun listApps(context: Context): List<AppSummary> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return packages.map { appInfo ->
            AppSummary(
                label = pm.getApplicationLabel(appInfo).toString(),
                packageName = appInfo.packageName,
                isSystemApp = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            )
        }.sortedBy { it.label.lowercase() }
    }

    fun getAppDetail(context: Context, packageName: String): AppDetail? {
        val pm = context.packageManager
        return runCatching {
            @Suppress("DEPRECATION")
            val flags = PackageManager.GET_PERMISSIONS or PackageManager.GET_ACTIVITIES or
                PackageManager.GET_SERVICES or PackageManager.GET_RECEIVERS or
                if (android.os.Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES

            val info = pm.getPackageInfo(packageName, flags)
            val appInfo = info.applicationInfo

            val requestedPerms = info.requestedPermissions?.toList() ?: emptyList()
            val dangerous = requestedPerms.filter { perm -> DANGEROUS_HINTS.any { perm.contains(it) } }

            val exportedActivities = info.activities?.filter { it.exported }?.map { it.name } ?: emptyList()
            val exportedServices = info.services?.filter { it.exported }?.map { it.name } ?: emptyList()
            val exportedReceivers = info.receivers?.filter { it.exported }?.map { it.name } ?: emptyList()

            val sigSha256 = extractSigningSha256(info)

            AppDetail(
                label = appInfo?.let { pm.getApplicationLabel(it).toString() } ?: packageName,
                packageName = packageName,
                versionName = info.versionName,
                minSdk = appInfo?.minSdkVersion ?: 0,
                targetSdk = appInfo?.targetSdkVersion ?: 0,
                debuggable = appInfo?.let { (it.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0 } ?: false,
                permissions = requestedPerms,
                dangerousPermissions = dangerous,
                exportedActivities = exportedActivities,
                exportedServices = exportedServices,
                exportedReceivers = exportedReceivers,
                signatureSha256 = sigSha256,
            )
        }.getOrNull()
    }

    @Suppress("DEPRECATION")
    private fun extractSigningSha256(info: android.content.pm.PackageInfo): String? {
        return runCatching {
            val signatureBytes = if (android.os.Build.VERSION.SDK_INT >= 28) {
                val signingInfo = info.signingInfo
                val signers = if (signingInfo?.hasMultipleSigners() == true) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo?.signingCertificateHistory
                }
                signers?.firstOrNull()?.toByteArray()
            } else {
                info.signatures?.firstOrNull()?.toByteArray()
            } ?: return null

            val digest = MessageDigest.getInstance("SHA-256").digest(signatureBytes)
            digest.joinToString(":") { "%02X".format(it) }
        }.getOrNull()
    }
}
