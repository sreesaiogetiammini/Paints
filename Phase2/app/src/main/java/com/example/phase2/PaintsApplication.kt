package com.example.phase2

import android.app.Application
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PaintsApplication(context: Context) : Application() {
    val scope = CoroutineScope(SupervisorJob())
    val db by lazy {PaintsDatabase.getDatabase(context)}
    val paintsRepository by lazy { PaintsRepository(scope, db.PaintsDao(), context) }
}
