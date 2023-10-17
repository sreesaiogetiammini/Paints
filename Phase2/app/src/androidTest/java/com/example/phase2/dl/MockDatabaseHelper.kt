package com.example.phase2.dl

import android.content.Context
import com.example.phase2.DatabaseHelper

class MockDatabaseHelper(private val context: Context) : DatabaseHelper(context) {

    private val userMap = mutableMapOf<String, String>() // Simulate user data

     override fun checkUser(email: String, password: String): Boolean {
        val storedPassword = userMap[email]
        return storedPassword == password
    }

     override fun insertData(email: String, password: String): Boolean {
        if (!userMap.containsKey(email)) {
            userMap[email] = password
            return true
        }
        return false
    }

    // Add other overridden methods as needed for your test cases
}