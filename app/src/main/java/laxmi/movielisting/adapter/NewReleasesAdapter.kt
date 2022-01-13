package laxmi.movielisting.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import laxmi.movielisting.constants.Constants
import laxmi.movielisting.R
import laxmi.movielisting.api.RetrofitApi
import laxmi.movielisting.model.MovieClass
import laxmi.movielisting.viewmodel.MainActivityViewModel

class NewReleasesAdapter (mainActivityViewModel: MainActivityViewModel): RecyclerView.Adapter<NewReleasesAdapter.MovieViewHolder>() {
    var newReleases = ArrayList<MovieClass>()
    val viewModel = mainActivityViewModel
    /*
    Method to set the movie data initially
    */
    fun setData(newReleases: ArrayList<MovieClass>) {
        this.newReleases = newReleases;
    }
    /*
    Method to update the movie list for each page limit
    */
    fun updateData(newReleases: ArrayList<MovieClass>) {
        val lastSize = this.newReleases.size
        this.newReleases.addAll(newReleases)
        var newSize = this.newReleases.size
        notifyItemRangeInserted(lastSize, newSize - 1)
    }

    class MovieViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val movieThumbnail = view.findViewById<ImageView>(R.id.movie_image_view)
        val movieName = view.findViewById<TextView>(R.id.movie_name)
        val movieYear = view.findViewById<TextView>(R.id.release_year)

        fun bindData(data: MovieClass) {
            movieName.text = data.title
            movieYear.text = getYear(data.release_date)
            // If backdrop path is null, we will set poster path as image thumbnail
            val path = if (data.backdrop_path == null) (Constants.BACKDROP_BASE_PATH + data.backdrop_path) else (Constants.POSTER_BASEPATH + data.poster_path)
            Picasso.get().load(path).into(movieThumbnail)
        }

        fun getYear(date: String): String {
            val splits = date.split("-")
            return splits[0]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, null)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bindData(newReleases.get(position))
        if (position == newReleases.size - Constants.LAZY_LOAD_LIMIT && RetrofitApi.PAGE < Constants.PAGE_MAX) {
            RetrofitApi.PAGE += Constants.DEFAULT_INCREMENT
            viewModel.getMovieList(RetrofitApi.PAGE)
        }
    }

    override fun getItemCount(): Int {
        return newReleases.size
    }
}