package tv.ridal.ui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import tv.ridal.util.Theme
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.layout.Layout
import tv.ridal.hdrezka.Loader
import tv.ridal.util.Utils
import kotlin.math.ceil

class MovieView(context: Context) : FrameLayout(context)
{
    private var posterView: ImageView
    var posterUrl: String = ""
        set(value) {
            field = value

            Loader.loadImage(posterUrl, this::onPosterLoaded)
        }

    private fun onPosterLoaded(poster: Drawable?)
    {
        if (poster == null) return

        val rawBitmap = (poster as BitmapDrawable).bitmap

        val resizedBitmap = Bitmap.createScaledBitmap(rawBitmap, posterWidthPx, posterHeightPx, false)

        posterDrawable = RoundedBitmapDrawableFactory.create(resources, resizedBitmap).apply {
            cornerRadius = Utils.dp(6F)
        }

        posterView.setImageDrawable(posterDrawable)
    }

    private var posterDrawable: Drawable? = null
    private var posterWidthPx = Utils.dp(113)
        set(value) {
            field = value

            posterHeightPx = ceil(posterWidthPx * 1.5F).toInt()

            posterView.layoutParams = Layout.ezFrame(
                Utils.px(posterWidthPx), Utils.px(posterHeightPx),
                Gravity.START or Gravity.TOP
            )

            gradient = LinearGradient(
                0F, posterHeightPx + 0F, 0F, posterHeightPx / 2F,
                Theme.color(Theme.color_bg),
                Theme.COLOR_TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            gradientPaint.shader = gradient
        }

    private var posterHeightPx = Utils.dp(170)

    private var movieTypeView: TextView
    var movieType: String = ""
        set(value) {
            field = value

            movieTypeView.text = movieType
        }

    private var gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var gradient: LinearGradient

    private var movieNameView: TextView
    var movieName: String? = null
        set(value) {
            field = value

            movieNameView.text = movieName
        }

    init
    {
        isClickable = true
        setOnTouchListener( InstantPressListener(this) )

        background = Theme.rect(Theme.color_bg)

        // children

        posterView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_XY
        }
        addView(posterView, Layout.frame(
            posterWidthPx, posterHeightPx,
            Gravity.START or Gravity.TOP
        ))

        movieTypeView = TextView(context).apply {
            setPadding(Utils.dp(5), Utils.dp(2), Utils.dp(5), Utils.dp(2))

            background = Theme.rect(
                Theme.alphaColor(Theme.COLOR_LIGHT_CHERRY, 0.7F),
                radii = floatArrayOf(
                    0F, Utils.dp(6F), 0F, Utils.dp(7F)
                )
            )

            setTextColor( Theme.COLOR_WHITE )
            textSize = 13F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
        }
        addView(movieTypeView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.END or Gravity.TOP
        ))

        gradient = LinearGradient(
            0F, posterHeightPx + 0F, 0F, posterHeightPx / 2F,
            Theme.color(Theme.color_bg),
            Theme.COLOR_TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        gradientPaint.shader = gradient

        movieNameView = RTextView(context).apply {
            setTextColor( Theme.color_text )
            textSize = 16F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(movieNameView, Layout.frame(
            Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
            Gravity.START or Gravity.BOTTOM
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        if (widthMode == MeasureSpec.UNSPECIFIED)
        {
            posterWidthPx = Utils.dp(113)
        }
        else if (widthMode == MeasureSpec.EXACTLY)
        {
            posterWidthPx = width
        }

        val nameHeight = Utils.dp(30)

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(posterWidthPx, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(posterHeightPx + nameHeight, MeasureSpec.EXACTLY)
        )

        var availableWidth = measuredWidth - Utils.dp(20)
        movieTypeView.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            0
        )

        availableWidth = measuredWidth
        movieNameView.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.AT_MOST ),
            MeasureSpec.makeMeasureSpec( nameHeight, MeasureSpec.AT_MOST )
        )
    }

    override fun dispatchDraw(canvas: Canvas)
    {
        super.dispatchDraw(canvas)

        canvas.drawRect(0F, posterHeightPx / 2F, posterWidthPx + 0F, posterHeightPx + Utils.dp(1F), gradientPaint)
    }

}





































//