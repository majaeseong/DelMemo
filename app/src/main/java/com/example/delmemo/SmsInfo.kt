package com.example.delmemo

import java.util.Date

data class SmsInfo(
    val sender: String,     // 발신자 전화번호
    val body: String,       // 문자 내용
    val date: Date          // 수신 시간
)