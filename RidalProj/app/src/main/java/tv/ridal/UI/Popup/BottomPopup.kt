package tv.ridal.UI.Popup

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import tv.ridal.Application.Theme
import tv.ridal.UI.Layout.LayoutHelper
import tv.ridal.R
import tv.ridal.Utils.Utils

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
        this.window?.apply {
            setDimAmount(DIM_EXPANDED)

//            attributes.windowAnimations = R.style.BottomPopupAnimations
        }

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

    var isDraggable: Boolean
        get() = this.behavior.isDraggable
        set(value) {
            this.behavior.isDraggable = value
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

            val holder = Theme.rect(Theme.color_popup_holder, radii = FloatArray(4).apply {
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