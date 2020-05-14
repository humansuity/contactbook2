package net.gas.gascontact.network.api

import net.gas.gascontact.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface KeycloackRetrofitService {

    @POST("auth/realms/{realm}/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun requestAccessToken(
        @Path("realm") realm: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Response<TokenResponse>

    @POST("auth/realms/{realm}/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun requestGrant(
        @Path("realm") realm: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Response<TokenResponse>

}
