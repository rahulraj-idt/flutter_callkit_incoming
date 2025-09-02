package com.hiennv.flutter_callkit_incoming

import android.content.Context
import android.content.Intent
import android.os.Bundle

object AppUtils {
    fun getAppIntent(context: Context, action: String? = null, data: Bundle? = null): Intent? {
        return try {
            // First try the standard approach
            var intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.cloneFilter()
            
            // If null (Android 11+ package visibility issue), create intent manually
            if (intent == null) {
                // Create intent to main activity manually
                intent = Intent().apply {
                    setPackage(context.packageName)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                
                // Try to resolve the main activity
                val resolveInfo = context.packageManager.queryIntentActivities(intent, 0).firstOrNull()
                if (resolveInfo != null) {
                    intent.component = android.content.ComponentName(
                        context.packageName,
                        resolveInfo.activityInfo.name
                    )
                } else {
                    // Fallback: create a basic intent to bring app to foreground
                    intent = Intent(Intent.ACTION_MAIN).apply {
                        setPackage(context.packageName)
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            }
            
            intent?.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent?.putExtra("EXTRA_CALLKIT_CALL_DATA", data)
            intent?.action = action
            intent
        } catch (e: Exception) {
            android.util.Log.e("AppUtils", "Error getting app intent", e)
            // Last resort fallback
            try {
                Intent(Intent.ACTION_MAIN).apply {
                    setPackage(context.packageName)
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    putExtra("EXTRA_CALLKIT_CALL_DATA", data)
                    this.action = action
                }
            } catch (fallbackException: Exception) {
                android.util.Log.e("AppUtils", "Fallback intent creation failed", fallbackException)
                null
            }
        }
    }
}