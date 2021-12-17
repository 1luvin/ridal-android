package tv.ridal.Components.Popup

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tv.ridal.Application.ApplicationLoader
import tv.ridal.Application.Theme
import tv.ridal.ApplicationActivity
import tv.ridal.Components.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils
import kotlin.random.Random

open class BottomPopup(context: Context) : BottomSheetDialog(context, R.style.BottomPopup)
{

    private val DIM_EXPANDED: Float = 0.33F

    private var currentDim: Float = DIM_EXPANDED
        set(value) {
            field = value
            this.window?.setDimAmount(currentDim)
        }

    init
    {
        this.window?.setDimAmount(DIM_EXPANDED)

        this.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
            override fun onSlide(bottomSheet: View, slideOffset: Float)
            {

                if (slideOffset >= 0) {
                    bottomSheet.alpha = 1F
                } else if (slideOffset < 0 && slideOffset >= -0.5F) {
                    bottomSheet.alpha = 1 + slideOffset * 2
                } else {
                    bottomSheet.alpha = 0F
                }

                currentDim = (1 + slideOffset) * DIM_EXPANDED
            }
        })
    }

    class Builder(private val context: Context)
    {
        private val popup: BottomPopup = BottomPopup(context)

        var hasHolderView: Boolean = false

        fun setContentView(view: View)
        {
            popup.setContentView(view)
        }

        fun build(): BottomPopup
        {
            return popup
        }

        private fun createHolderView() : View
        {
            val holderLayout = FrameLayout(context).apply {
                layoutParams = LayoutHelper.createFrame(
                    LayoutHelper.MATCH_PARENT, 22
                )
            }

            val holder = Theme.createRect(Theme.color_popup_holder, FloatArray(4).apply {
                fill(Utils.dp(4F))
            })
            (holder as ShapeDrawable).apply {
                intrinsicWidth = Utils.dp(32)
                intrinsicHeight = Utils.dp(4)
            }

            val holderView = ImageView(context).apply {
                setImageDrawable(holder)
            }

            holderLayout.addView(holderView, LayoutHelper.createFrame(
                LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
                Gravity.CENTER
            ))

            return holderLayout
        }

    }
}





































//