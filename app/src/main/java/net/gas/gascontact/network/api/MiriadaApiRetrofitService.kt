package net.gas.gascontact.network.api


import net.gas.gascontact.business.model.IdentityScopesResponse
import net.gas.gascontact.business.model.StandardJsonResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MiriadaApiRetrofitService {

    /* Посылка информации на сервер, о том что пользователь залогинился */
    @GET("sendlogintimetogasb")
    suspend fun sendLoginTimeToGasb(
        @Query("realm") realm: String,
        @Query("grant_type") grant_type: String,
        @Query("hardwareid") hardwareid: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("Authorization") token: String,
        @Query("version") version: String
    ): Response<StandardJsonResponse>

    /* Получение информации о клиенте и его правах */
    @GET("identityscopes")
    suspend fun requestIdentityScopes(
        @Query("realm") realm: String,
        @Query("grant_type") grant_type: String,
        @Query("hardwareid") hardwareid: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("Authorization") token: String
    ): Response<IdentityScopesResponse>

    /* Получение базы данных с сервера */
    @GET("downloadcontactbookdb")
    suspend fun requestGetDownloadDB(
        @Query("realm") realm: String,
        @Query("grant_type") grant_type: String,
        @Header("Authorization") token: String
    ): Response<ResponseBody>
}
