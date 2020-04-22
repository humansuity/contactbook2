package net.gas.gascontact.business.model

data class IdentityScopesResponse(
    val dn: String,
    val preferred_username: String,
    val confValue: Array<String>
)
