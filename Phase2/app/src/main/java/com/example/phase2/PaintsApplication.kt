package com.example.phase2

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class PaintsApplication : Application() {
    val scope = CoroutineScope(SupervisorJob())
    val db by lazy {PaintsDatabase.getDatabase(applicationContext)}
    val paintsRepository by lazy { PaintsRepository(scope, db.PaintsDao()) }
}