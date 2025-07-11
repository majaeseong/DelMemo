package com.example.delmemo.chatgpt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import kotlinx.coroutines.*
import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.3
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

interface OpenAIService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): ChatResponse
}

object GptHelper {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(OpenAIService::class.java)

    suspend fun analyzeText(apiKey: String, smsText: String, fallbackPhone: String, receivedAt: String): String {
        val prompt = """
            다음 문자를 읽고 아래 항목으로 분류해서 JSON 형식으로 정리해줘:
            - 주소
            - 이름
            - 전화번호 (내용에 없으면 이걸 써: $fallbackPhone)
            - 수신시간: $receivedAt
            - 수량 (없으면 생략)
            
            문자 내용:
            $smsText
        """.trimIndent()

        val request = ChatRequest(
            messages = listOf(Message("user", prompt))
        )

        val response = service.getChatCompletion(
            auth = "Bearer $apiKey",
            request = request
        )

        return response.choices.first().message.content
    }
}