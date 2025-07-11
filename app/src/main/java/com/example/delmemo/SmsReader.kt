package com.example.delmemo

import android.content.Context
import android.provider.Telephony
import android.util.Log
import java.util.Date


object SmsReader {
    fun readLatestSms(context: Context, message: String): SmsInfo? {
        val uri = Telephony.Sms.Inbox.CONTENT_URI
        val projection = arrayOf("address", "body", "date")
        val sortOrder = "date DESC"

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val cleanedMsg = cleanString(message)

            while (it.moveToNext()) {
                val body = it.getString(it.getColumnIndexOrThrow("body"))
                val sender = it.getString(it.getColumnIndexOrThrow("address"))
                val dateMillis = it.getLong(it.getColumnIndexOrThrow("date"))
                val date = Date(dateMillis)

                val cleanedBody = cleanString(body)

//                Log.d("SmsReader", "🧪 공유된 cleaned: ${cleanedMsg.take(20)}")
//                Log.d("SmsReader", "📬 문자함 cleaned: ${cleanedBody.take(20)}")

                // 핵심 매칭 로직
                Log.d("cleanedBody",cleanedBody)
                Log.d("cleanedMsg",cleanedMsg)
                Log.d("same??",cleanedBody.contains(cleanedMsg.take(15)).toString())
                if (cleanedBody.contains(cleanedMsg.take(15))) {
                    Log.d("SmsReader", "✅ 문자 매칭 성공!")
                    return SmsInfo(sender, body, date)
                }
            }
        }

        Log.d("SmsReader", "❌ 일치하는 문자 없음 (fallback 없음)")
        return null
    }

    private fun cleanString(input: String): String {
        return input
            .lowercase()
            .replace("[^\\p{L}\\p{N}]".toRegex(), "") // 문자, 숫자 제외 모두 제거
    }
}
