package tv.ridal

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.github.nitrico.stickyscrollview.StickyScrollView
import tv.ridal.adapter.MoviesAdapter
import tv.ridal.hdrezka.HDRezka
import tv.ridal.hdrezka.Movie
import tv.ridal.hdrezka.Parser
import tv.ridal.hdrezka.Sorting
import tv.ridal.ui.actionbar.BigActionBar
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.layout.VLinearLayout
import tv.ridal.ui.recyclerview.GridSpacingItemDecoration
import tv.ridal.ui.view.RTextView
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class SearchFragment : BaseAppFragment()
{
    override val stableTag: String
        get() = "SearchFragment"


    private lateinit var rootFrame: FrameLayout
    private lateinit var scroll: StickyScrollView
    private lateinit var layout: LinearLayout
    private lateinit var actionBar: BigActionBar
    private lateinit var searchView: SearchView

    private lateinit var moviesView: RecyclerView
    private val movies: ArrayList<Movie> = ArrayList()

    private val requestQueue: RequestQueue = App.instance().requestQueue


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        createUI()
        loadMovies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        return rootFrame
    }

    private fun createUI()
    {
        rootFrame = FrameLayout(context).apply {
            setPadding(0, Utils.dp(30), 0, 0)
            setBackgroundColor(Theme.color(Theme.color_bg))
        }

        createActionBar()
        createSearchView()
        createMoviesView()

        layout = VLinearLayout(context).apply {
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS // чтобы RecyclerView сам не скролился к верху
            addView(actionBar)
            addView(searchView, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                20, 0, 20, 15
            ))
            addView(moviesView, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT
            ))
        }

        createScroll()
        scroll.addView(layout)
        rootFrame.addView(scroll)
    }

    private fun createScroll()
    {
        scroll = StickyScrollView(context).apply {
            isVerticalScrollBarEnabled = false

            addOnStickyScrollViewListener { x, y, oldX, oldY ->

                val h = actionBar.measuredHeight
                var start: Float = 0F
                var end: Float = 0F

                if ( h in oldY until y )
                {
                    start = 1F
                }
                else if ( h in y until oldY )
                {
                    end = 1F
                }

                if (start + end == 0F) return@addOnStickyScrollViewListener

                ValueAnimator.ofFloat(start, end).apply {
                    duration = 120

                    addUpdateListener {
                        val process = it.animatedValue as Float
                        val margin = (Utils.dp(20) * process).toInt()
                        val corner = Utils.dp(7F) * process

                        searchView.apply {
                            updateLayoutParams<LinearLayout.LayoutParams> {
                                setMargins( margin, 0, margin, bottomMargin )
                            }

                            background = Theme.rect(
                                Theme.overlayColor(Theme.color_bg, 0.04F),
                                radii = FloatArray(4).apply {
                                    fill( corner )
                                }
                            )
                        }
                    }

                    start()
                }

            }
        }
    }

    private fun createActionBar()
    {
        actionBar = BigActionBar(context).apply {
            title = Locale.string(R.string.search)
        }
    }

    private fun createSearchView()
    {
        searchView = SearchView().apply {
            background = Theme.rect(
                Theme.overlayColor(Theme.color_bg, 0.04F),
                radii = FloatArray(4).apply {
                    fill( Utils.dp(7F) )
                }
            )

            tag = "sticky"

            setOnClickListener {
                startFragment( SearchInputFragment() )
            }
        }
    }

    private fun createMoviesView()
    {
        moviesView = RecyclerView(context).apply {
            setPadding( Utils.dp(7), 0, Utils.dp(7), 0 )
            clipToPadding = false


            edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return EdgeEffect(view.context).apply { color = Theme.color(Theme.color_main) }
                }
            }

            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration( GridSpacingItemDecoration(3, Utils.dp(13)) )

            adapter = MoviesAdapter(movies).apply {
                onMovieClick {
                    startFragment( MovieFragment.newInstance(it) )
                }
            }
        }
    }


    private fun loadMovies()
    {
        val url = HDRezka.createUrl(sortingUrl = Sorting.url(Locale.string(R.string.sorting_watching)))
        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                movies.addAll( Parser.parseMovies(response)!! )
                updateMovies()
            },
            {
                println("ERROR!")
            }
        )
        requestQueue.add(request)
    }

    private fun updateMovies()
    {
        moviesView.adapter?.notifyItemRangeInserted(
            0, movies.size
        )
    }


    inner class SearchView : FrameLayout(context)
    {
        private var searchIconView: ImageView
        private var textView: TextView

        var text: String = ""
            set(value) {
                field = value

                textView.text = text
            }

        init
        {
            searchIconView = ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER

                setImageDrawable(Theme.drawable(R.drawable.search, Theme.color_text))
            }
            addView(searchIconView, Layout.ezFrame(
                50, 50
            ))

            textView = RTextView(context).apply {
                gravity = Gravity.CENTER_VERTICAL

                setTextColor(Theme.color(Theme.color_text))
                textSize = 18F
                typeface = Theme.typeface(Theme.tf_normal)
                setLines(1)
                maxLines = 1
                isSingleLine = true
                ellipsize = TextUtils.TruncateAt.END

                hint = Locale.string(R.string.search_hint)
                setHintTextColor( Theme.color(Theme.color_text2) )
            }
            addView(textView, Layout.ezFrame(
                Layout.MATCH_PARENT, Layout.MATCH_PARENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                50, 0, 0, 0
            ))
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
        {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
                MeasureSpec.makeMeasureSpec( Utils.dp(50), MeasureSpec.EXACTLY )
            )
        }
    }
}


































//