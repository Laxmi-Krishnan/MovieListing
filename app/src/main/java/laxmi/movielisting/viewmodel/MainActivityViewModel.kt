package laxmi.movielisting.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import laxmi.movielisting.constants.Constants
import laxmi.movielisting.model.MovieClass
import laxmi.movielisting.model.MovieData
import laxmi.movielisting.repository.APIRepository

class MainActivityViewModel @ViewModelInject constructor(
        private val apiRepository: APIRepository,
        private val sharedPreferences: SharedPreferences,
        private val context: Context
) : ViewModel() {
    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    private val preferenceEditor = sharedPreferences.edit()

    val defaultPreference = false
    val isSavedDataLocally = sharedPreferences.getBoolean(Constants.DATA_EXIST, defaultPreference)

    val newReleases = MutableLiveData<ArrayList<MovieClass>>()
    val newVideos = MutableLiveData<ArrayList<MovieClass>>()
    val bestRated = MutableLiveData<ArrayList<MovieClass>>()

    fun getMovieList(page: Int) {
        if (isSavedDataLocally) {
            val lastSavedPage = sharedPreferences.getInt(Constants.LAST_PAGE, Constants.INVALID)
            if (lastSavedPage != Constants.INVALID && page <= lastSavedPage) {
                val response = getLocallyCachedData(page)
                if (response != null) {
                    newReleases.postValue(response.results)
                    splitData(response)
                } else {
                    getDataFromServer(page)
                }
            } else {
                getDataFromServer(page)
            }
        } else {
            getDataFromServer(page)
        }
    }

    /*
   Method to Retrieve data from server
     */
    private fun getDataFromServer(page: Int) {
        if (checkNetwork()) {
            viewModelScope.launch {
                val response = apiRepository.getMovieList(page)
                withContext(Dispatchers.Main) {
                    saveDataLocally(response, page)
                    newReleases.postValue(response.results)
                    splitData(response)
                }
            }
        } else {
            Toast.makeText(context,"Ensure your device has internet access(if not, enable and restart the application)", Toast.LENGTH_LONG).show()
        }
    }

    /*
    Method to get locally cached data
    */
    private fun getLocallyCachedData(page: Int): MovieData? {
        var pageData = sharedPreferences.getString(Constants.PAGE_TAG + page, null)
        var movieData: MovieData? = null
        var gson = Gson()
        if (pageData != null) {
            movieData = gson.fromJson(pageData, MovieData::class.java)
            Toast.makeText(context, "Retrieving data locally for Page - ${page}", Toast.LENGTH_SHORT).show()
        }
        return movieData
    }

    /*
    Method to locally save the data fetched from sever
    */
    private fun saveDataLocally(response: MovieData, page: Int) {
        var gson = Gson()
        var jsonString = gson.toJson(response)
        preferenceEditor.putString(Constants.PAGE_TAG + page, jsonString)
        preferenceEditor.putInt(Constants.LAST_PAGE, page)
        if (!isSavedDataLocally) {
            preferenceEditor.putBoolean(Constants.DATA_EXIST, true)
        }
        preferenceEditor.commit()
        Log.i(TAG, "Locally saved for page - ${page}");
    }

    /*
    Method to split the movies into categories ( for Best Rated and New videos)
    */
    fun splitData(response: MovieData) {
        val results = response.results
        // Filtering data for New videos category based on backdrop path
        var newVideosList = results.filter { it.backdrop_path != null } as ArrayList<MovieClass>
        newVideos.postValue(newVideosList)
        Log.i(TAG, "The backdrops: ${newVideosList.size}")
        // Filtering data for Best rated list category based on average rating
        var bestRatedList = results.filter { it.vote_average > 5 } as ArrayList<MovieClass>
        Log.i(TAG, "The bestRated: ${bestRatedList.size}")
        bestRated.postValue(bestRatedList)
    }

    private fun checkNetwork(): Boolean {
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connManager != null) {
            val info = connManager!!.activeNetworkInfo
            if (info != null) {
                if (info!!.state == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false
    }
}