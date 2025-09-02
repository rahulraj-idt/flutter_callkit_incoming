package com.hiennv.flutter_callkit_incoming

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

class TransparentActivity : Activity() {

    companion object {
        fun getIntent(context: Context, action: String?, data: Bundle?): Intent {
            val intent = Intent(context, TransparentActivity::class.java)
            intent.action = action
            intent.putExtra("data", data)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            return intent
        }
    }


    override fun onStart() {
        super.onStart()
        setVisible(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val data = intent.getBundleExtra("data")
            val action = intent.action

            // Check if action is null
            if (action.isNullOrEmpty()) {
                android.util.Log.w("TransparentActivity", "Intent action is null or empty")
                return
            }

            // Send broadcast intent
            val broadcastIntent = CallkitIncomingBroadcastReceiver.getIntent(this, action, data)
            broadcastIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            sendBroadcast(broadcastIntent)

            // Get app intent - now with proper Android 11+ support
            val activityIntent = AppUtils.getAppIntent(this, action, data)
            if (activityIntent != null) {
                startActivity(activityIntent)
            } else {
                android.util.Log.e("TransparentActivity", "Failed to create app intent for action: $action")
            }

        } catch (e: Exception) {
            android.util.Log.e("TransparentActivity", "Error in onCreate", e)
        } finally {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}
