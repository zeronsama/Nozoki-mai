package `fun`.zerrun.nozoki_mai.network

import `fun`.zerrun.nozoki_mai.data.*
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/maimaidxprober/query/player")
    suspend fun getPlayerInfo(@Body request: PlayerRequest): PlayerResponse
}