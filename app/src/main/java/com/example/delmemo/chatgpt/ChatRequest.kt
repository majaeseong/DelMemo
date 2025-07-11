data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.3
)

data class Message(
    val role: String,
    val content: String
)