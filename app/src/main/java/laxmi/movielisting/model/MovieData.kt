package laxmi.movielisting.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieData(
    val dates: Dates,
    val page: Int,
    val results: ArrayList<MovieClass>,
    val total_pages: Int,
    val total_results: Int
): Parcelable {
   @Parcelize
   data class Dates(
       val maximum: String,
       val minimum: String
   ): Parcelable
}
