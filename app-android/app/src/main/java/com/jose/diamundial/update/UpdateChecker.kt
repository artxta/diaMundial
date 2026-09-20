package com.jose.diamundial.update

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

object UpdateChecker {
    private const val TAG = "DiaMundial/Update"

    fun checkForUpdate(activity: Activity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity.applicationContext)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                    Log.d(TAG, "Update available: ${appUpdateInfo.availableVersionCode()}")
                    showUpdateDialog(activity)
                }
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    Log.d(TAG, "No update available")
                }
                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    Log.d(TAG, "Update in progress")
                }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to check for update", e)
        }
    }

    private fun showUpdateDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("Actualización disponible")
            .setMessage("Hay una nueva versión de la aplicación disponible en Google Play. ¿Deseas actualizar?")
            .setCancelable(false)
            .setPositiveButton("Actualizar") { _, _ ->
                openPlayStore(activity)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun openPlayStore(activity: Activity) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${activity.packageName}"))
            intent.setPackage("com.android.vending")
            activity.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Play Store app not found, opening browser", e)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${activity.packageName}"))
            activity.startActivity(intent)
        }
    }
}
