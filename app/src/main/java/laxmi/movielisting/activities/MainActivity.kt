package laxmi.movielisting.activities

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import laxmi.movielisting.constants.Constants
import laxmi.movielisting.R
import laxmi.movielisting.adapter.BestRatedAdapter
import laxmi.movielisting.adapter.NewReleasesAdapter
import laxmi.movielisting.adapter.NewVideosAdapter
import laxmi.movielisting.api.RetrofitApi
import laxmi.movielisting.databinding.ActivityMainBinding
import laxmi.movielisting.viewmodel.MainActivityViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    /*Viewmodel initialisation by delegates*/
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()
    lateinit var activityBinder: ActivityMainBinding
    private val TAG = "MainActivity"

    lateinit var newVideosAdapter: NewVideosAdapter
    lateinit var newReleasesAdapter: NewReleasesAdapter
    lateinit var bestRatedAdapter: BestRatedAdapter

    lateinit var animationController: LayoutAnimationController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinders()
        getMovies()
    }

    /*
    Method to set view model binders
    */
    private fun setBinders() {
        activityBinder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        activityBinder.activityBinder = mainActivityViewModel
        activityBinder.lifecycleOwner = this
        setViews()
        setObservers()
        setAdapters()
    }
    /*
    Method to set Recycler view adapters
    */
    private fun setAdapters() {
        newVideosAdapter = NewVideosAdapter(mainActivityViewModel)
        newReleasesAdapter = NewReleasesAdapter(mainActivityViewModel)
        bestRatedAdapter = BestRatedAdapter(mainActivityViewModel)
    }

    /*
    Method to initalise views
    */
    private fun setViews() {
        supportActionBar?.hide() //Hiding action bar
        activityBinder.newReleaseSection.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        activityBinder.newReleaseSection.recyclerView.setHasFixedSize(true)
        activityBinder.newVideosSection.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        activityBinder.newVideosSection.recyclerView.setHasFixedSize(true)
        activityBinder.bestRatedSection.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        activityBinder.bestRatedSection.recyclerView.setHasFixedSize(true)
        activityBinder.newReleaseSection.textView.text = Constants.NEW_RELEASES
        activityBinder.newVideosSection.textView.text = Constants.NEW_VIDEOS
        activityBinder.bestRatedSection.textView.text = Constants.BEST_RATED

        animationController = AnimationUtils.loadLayoutAnimation(this, R.anim.animator)

    }

    /*
    Observers for recycler view list updation
    */
    private fun setObservers() {

        mainActivityViewModel.newVideos.observe(this, {
            if (newVideosAdapter.newVideos.isEmpty()) {
                newVideosAdapter.setData(it)
                activityBinder.newVideosSection.recyclerView.adapter = newVideosAdapter
            } else {
                newVideosAdapter.updateData(it)
            }
            activityBinder.newVideosSection.recyclerView.animation = animationController.animation
            activityBinder.newVideosSection.recyclerView.scheduleLayoutAnimation()
        })

        mainActivityViewModel.newReleases.observe(this, {
            if (newReleasesAdapter.newReleases.isEmpty()) {
                newReleasesAdapter.setData(it)
                activityBinder.newReleaseSection.recyclerView.adapter = newReleasesAdapter
            } else {
                newReleasesAdapter.updateData(it)
            }
            activityBinder.newReleaseSection.recyclerView.animation = animationController.animation
            activityBinder.newReleaseSection.recyclerView.scheduleLayoutAnimation()
        })
        mainActivityViewModel.bestRated.observe(this, {
            if (bestRatedAdapter.bestRated.isEmpty()) {
                bestRatedAdapter.setData(it)
                activityBinder.bestRatedSection.recyclerView.adapter = bestRatedAdapter
            } else {
                bestRatedAdapter.updateData(it)
            }
            activityBinder.bestRatedSection.recyclerView.animation = animationController.animation
            activityBinder.bestRatedSection.recyclerView.scheduleLayoutAnimation()
        })
    }

    /*Getting the the movies of page 1(initial loading)*/
    private fun getMovies() {
        mainActivityViewModel.getMovieList(RetrofitApi.PAGE)
    }
}