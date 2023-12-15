package com.drofficial.googlegeminiapi

data class Chat(
    var message: String,
    var sentBy: String
) {
    companion object {
        const val SEND_BY_ME = "me"
        const val SEND_BY_GEMINI = "gemini"
    }
}

