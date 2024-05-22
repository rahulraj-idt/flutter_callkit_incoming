package com.example.flutter_callkit_incoming_example

import android.os.Bundle
import android.util.Log
import com.hiennv.flutter_callkit_incoming.FlutterCallkitIncomingPlugin
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity(), FlutterCallkitIncomingPlugin.CallEventListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlutterCallkitIncomingPlugin.registerCallEventListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        FlutterCallkitIncomingPlugin.unregisterCallEventListener(this)
    }

    override fun onCallEvent(event: String, body: Map<String, Any>) {
        Log.i(TAG, "Event: $event received with data: $body")
    }
}
