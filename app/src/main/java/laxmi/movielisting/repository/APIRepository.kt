package laxmi.movielisting.repository

import laxmi.movielisting.api.RetrofitApi
import laxmi.movielisting.model.MovieData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class APIRepository @Inject constructor(
    retrofitApi: RetrofitApi
) {
    val retrofitApi = retrofitApi

    companion object {
        private const val TAG = "APIRepository"
    }

    suspend fun getMovieList(page: Int): MovieData {
        return retrofitApi.getUser(RetrofitApi.API_KEY,
            RetrofitApi.LANGUAGE, page,
            RetrofitApi.REGION, RetrofitApi.WITH_RELEASE_TYPE)
    }

}