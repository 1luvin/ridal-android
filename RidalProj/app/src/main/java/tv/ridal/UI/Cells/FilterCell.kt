package tv.ridal.UI.Cells

import android.text.TextUtils
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import tv.ridal.Application.Theme
import tv.ridal.ApplicationActivity
import tv.ridal.UI.InstantPressListener
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

class FilterCell : FrameLayout(ApplicationActivity.instance())
{
    private var nameView: TextView
    private var valueView: TextView
    private var pointerView: ImageView

    var filterName: String = ""
        set(value) {
            field = value

            nameView.text = filterName
            requestLayout()
        }

    var filterValue: String = ""
        set(value) {
            field = value

            valueView.text = filterValue
            requestLayout()
        }

    init
    {
        isClickable = true
        setOnTouchListener(InstantPressListener(this))

        setPadding(Utils.dp(20), Utils.dp(10), Utils.dp(15), Utils.dp(10))

        nameView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            textSize = 17F
            typeface = Theme.typeface(Theme.tf_bold)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(nameView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.START or Gravity.CENTER_VERTICAL))

        valueView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_main))
            textSize = 16.5F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
        }
        addView(valueView, LayoutHelper.createFrame(
            LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
            Gravity.END or Gravity.CENTER_VERTICAL,
            0, 0, 24, 0))

        val pointerDrawable = Theme.drawable(R.drawable.pointer_forward, Theme.color_main)
        pointerView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable(pointerDrawable)
        }
        addView(pointerView, LayoutHelper.createFrame(
            24, 24,
            Gravity.END or Gravity.CENTER_VERTICAL))
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED)
        )

        val availableWidth = measuredWidth - paddingLeft - paddingRight - Utils.dp(24)
        var width = availableWidth / 2

        valueView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            0
        )

        width = availableWidth - valueView.measuredWidth - Utils.dp(10)

        nameView.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            0
        )
    }
}





































//