package com.paisapulse.app.data.remote.models

data class GeminiRequest(val contents: List<Content>)
data class Content(val parts: List<Part>)
data class Part(val text: String)

data class GeminiResponse(
    val candidates: List<Candidate> // This resolves .candidates
)
data class Candidate(
    val content: Content // This resolves .content
)