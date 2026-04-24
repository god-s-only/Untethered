package com.untethered.app.di

import rikka.shizuku.Shizuku
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuHelper @Inject constructor() {

    fun isAvailable(): Boolean {
        return try {
            Shizuku.pingBinder() && hasPermission()
        } catch (e: Exception) {
            false
        }
    }

    fun hasPermission(): Boolean {
        return try {
            if (Shizuku.isPreV11()) {
                Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        } catch (e: Exception) {
            false
        }
    }

    fun requestPermission(requestCode: Int) {
        Shizuku.requestPermission(requestCode)
    }

    fun addRequestPermissionResultListener(
        requestCode: Int,
        listener: Shizuku.OnRequestPermissionResultListener
    ) {
        Shizuku.addRequestPermissionResultListener(listener)
    }

    fun removeRequestPermissionResultListener(
        listener: Shizuku.OnRequestPermissionResultListener
    ) {
        Shizuku.removeRequestPermissionResultListener(listener)
    }
}