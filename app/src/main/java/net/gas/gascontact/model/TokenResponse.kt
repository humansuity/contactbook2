package net.gas.gascontact.model

data class TokenResponse(
    val access_token: String,
    val expires_in: Int,
    val tokenType: String,
    val scope: String,
    val refresh_token: String,
    val token_type: String,
    val session_state: String,
    val refresh_expires_in: Int
)
