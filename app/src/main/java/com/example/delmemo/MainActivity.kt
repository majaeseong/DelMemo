package com.example.delmemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.lifecycle.lifecycleScope
import com.example.delmemo.chatgpt.GptHelper
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent) // 인텐트를 업데이트 해줘야 함!
        handleIntent(intent)


//        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
//            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
//            sharedText?.let {
//                viewModel.addText(it)
//            }
//        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestSmsPermissionIfNeeded() // 권한요청
        handleIntent(intent)


        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                SharedTextListScreen(viewModel = viewModel)
            }
        }
    }

    private fun requestSmsPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), 100)
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            sharedText?.let {

                // 문자함에서 실제 발신자/시간을 조회
                val smsInfo = SmsReader.readLatestSms(this, it)

                if (smsInfo != null) {

                    val smsText = smsInfo.body
                    val fallbackPhone = smsInfo.sender
                    val receivedAt = smsInfo.date.toString()

                    // ✅ ChatGPT 호출
                    lifecycleScope.launch {
                        try {
                            val apiKey = BuildConfig.OPENAI_API_KEY
                            val result = GptHelper.analyzeText(apiKey, smsText, fallbackPhone, receivedAt)

                            Log.d("Result!@#$", result)
                            // ✅ 결과 표시
                            viewModel.addText("📦 분석 결과:\n$result")
                        } catch (e: Exception) {
                            viewModel.addText("❌ GPT 분석 실패: ${e.message}")
                        }
                    }

                    val message = """
                    문자 내용: ${smsInfo.body}
                    발신자: ${smsInfo.sender}
                    수신 시간: ${smsInfo.date}
                """.trimIndent()
                    viewModel.addText(message)

                    Log.d("hhhhhhhhhh","여기 옴?");

                } else {
                    // fallback
                    viewModel.addText(it)

                    Log.d("hhhhhhhhhh2","여기 옴?2");
                }
            }
        }
    }




}


@Composable
fun SharedTextListScreen(viewModel: MainViewModel) {
    val list by viewModel.textList.collectAsState()

    LazyColumn {
        items(list) { text ->
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

