package com.example.delmemo

import android.content.Context
import android.net.Uri
import java.util.Date

object SmsReader {
    fun readLatestSms(context: Context, message: String): SmsInfo? {
        val uri = Uri.parse("content://sms/inbox")
        val cursor = context.contentResolver.query(
            uri,
            arrayOf("address", "body", "date"),
            null,
            null,
            "date DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val body = it.getString(it.getColumnIndexOrThrow("body"))
                if (body.contains(message.trim().take(10))) { // 앞부분 비교
                    val address = it.getString(it.getColumnIndexOrThrow("address"))
                    val timestamp = it.getLong(it.getColumnIndexOrThrow("date"))
                    return SmsInfo(address, body, Date(timestamp))
                }
            }
        }

        return null
    }
}