package com.proshiftteam.proshift.Utilities

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.*

class ProShiftAuthenticatorService : Service() {
   override fun onBind(intent: Intent?): IBinder? {
        return mAuthenticator.iBinder
    }

    override fun onCreate() {
        super.onCreate()
        mAuthenticator = ProShiftAuthenticator(this)
    }

    companion object {
        lateinit var mAuthenticator: ProShiftAuthenticator
            private set
    }
}