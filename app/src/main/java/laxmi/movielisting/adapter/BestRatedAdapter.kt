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

class BestRatedAdapter(mainActivityViewModel: MainActivityViewModel): RecyclerView.Adapter<BestRatedAdapter.MovieViewHolder>() {
    var bestRated = ArrayList<MovieClass>()
    val viewModel = mainActivityViewModel

    /*
    Method to set the movie data initially
    */
    fun setData(bestRated: ArrayList<MovieClass>) {
        this.bestRated = bestRated;
    }

    /*
    Method to update the movie list for each page limit
    */
    fun updateData(bestRated: ArrayList<MovieClass>) {
        val lastSize = this.bestRated.size
        this.bestRated.addAll(bestRated)
        val newSize = lastSize + bestRated.size
        notifyItemRangeInserted(lastSize, newSize)
    }

    class MovieViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val movieThumbnail = view.findViewById<ImageView>(R.id.movie_image_view)
        val movieName = view.findViewById<TextView>(R.id.movie_name)
        val movieYear = view.findViewById<TextView>(R.id.release_year)

        fun bindData(data: MovieClass) {
            movieName.text = data.title
            movieYear.text = getYear(data.release_date)
            Picasso.get().load(Constants.POSTER_BASEPATH + data.poster_path)
                .into(movieThumbnail)
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
        holder.bindData(bestRated.get(position))
        if (position == bestRated.size - Constants.LAZY_LOAD_LIMIT && RetrofitApi.PAGE < Constants.PAGE_MAX) {
            RetrofitApi.PAGE += Constants.DEFAULT_INCREMENT
            viewModel.getMovieList(RetrofitApi.PAGE)
        }
    }

    override fun getItemCount(): Int {
        return bestRated.size
    }
}