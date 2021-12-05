package tv.ridal.Adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.ridal.Application.ApplicationLoader
import tv.ridal.ApplicationActivity
import tv.ridal.CatalogFragment
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.Components.MovieView
import tv.ridal.HDRezka.Movie
import tv.ridal.SearchFragment
import tv.ridal.Utils.Utils

class MoviesAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<MoviesAdapter.ViewHolder>()
{
    var onMovieClick: ((Movie) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        init {
            itemView.setOnClickListener {
                onMovieClick?.invoke(movies[adapterPosition])
            }
        }

        fun bind(current: Movie)
        {
            val movieView = itemView as MovieView
            movieView.apply {
                posterUrl = current.posterUrl
                movieName = current.name
                movieType = current.type.ruType
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val movieView = MovieView(parent.context)

        if (ApplicationActivity.currentFragment() is CatalogFragment)
        {
            movieView.layoutParams = RecyclerView.LayoutParams(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT
            )
        }
        else if (ApplicationActivity.currentFragment() is SearchFragment)
        {
            movieView.layoutParams = RecyclerView.LayoutParams(
                LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT
            )
        }

        return ViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
       holder.bind(movies[position])
    }

    override fun getItemCount(): Int {
        return movies.size
    }

}





































//