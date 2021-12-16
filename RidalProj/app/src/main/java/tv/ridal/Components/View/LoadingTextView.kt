package tv.ridal.Components.View

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import tv.ridal.Application.Theme

class LoadingTextView(context: Context) : TextView(context)
{
    var color: Int = Theme.color(Theme.color_text)
        set(value) {
            field = value

            if ( ! animator.isRunning) this.setTextColor(color)
        }
    var loadColor: Int = Theme.color(Theme.color_main)

    var loadSpeed: Long = 2
        set(value) {
            field = value

            animator.duration = loadSpeed * 1000
        }

    var loadSpanWidth: Float = 0.4F
        set(value) {
            field = value

            animator.setFloatValues(0F, 1F + loadSpanWidth)

            halfSpanWidth = loadSpanWidth / 2
        }
    private var halfSpanWidth: Float = 0.2F


    private var animator = ValueAnimator.ofFloat(0F, 1F + loadSpanWidth).apply {
        interpolator = DecelerateInterpolator()
        duration = loadSpeed * 1000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
    }

    private var left: Float = 0F
    private var middle: Float = 0F
    private var right: Float = 0F

    fun startLoading()
    {
        animator.apply {
            addUpdateListener {

                val currProcent = it.animatedValue as Float

                // от 0 до 0.4
                if (currProcent < loadSpanWidth)
                {
                    left = 0F
                    if (currProcent < halfSpanWidth) middle = 0F
                    else middle = currProcent - halfSpanWidth
                    right = currProcent
                }
                // от 0.4 до 1
                else if (currProcent < 1F)
                {
                    left = currProcent - loadSpanWidth
                    middle = currProcent - halfSpanWidth
                    right = currProcent
                }
                // от 1 до 1.4
                else // if (currProcent < 1.4F)
                {
                    left = currProcent - loadSpanWidth
                    if (currProcent < 1F + halfSpanWidth) middle = currProcent - halfSpanWidth
                    else middle = 1F
                    right = 1F
                }


                val shader = LinearGradient(
                    0F, 0F, this@LoadingTextView.measuredWidth + 0F, this@LoadingTextView.measuredHeight + 0F,
                    intArrayOf(color, loadColor, color),
                    floatArrayOf(left, middle, right),
                    Shader.TileMode.CLAMP
                )

                this@LoadingTextView.paint.shader = shader
                this@LoadingTextView.invalidate()
            }
        }

        animator.start()
    }
}





































//