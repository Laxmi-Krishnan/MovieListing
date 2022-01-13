package laxmi.movielisting.api

import laxmi.movielisting.model.MovieData
import retrofit2.http.GET
import retrofit2.http.Query

// API Interface for web request
interface RetrofitApi {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/"
        const val API_KEY = "9c0523bff54071c4fb4b716a950231b9"
        const val LANGUAGE = "en-US"
        var PAGE = 1
        const val REGION = "IN|US"
        const val WITH_RELEASE_TYPE="2|3"
    }

    @GET("3/movie/upcoming")
    suspend fun getUser(@Query("api_key") api_key: String,
    @Query("language") language: String,
    @Query("page") page: Int,
    @Query("region") region: String,
    @Query("with_release_type") with_release_type: String): MovieData
}