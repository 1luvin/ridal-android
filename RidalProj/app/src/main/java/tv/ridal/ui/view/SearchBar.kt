package tv.ridal.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import tv.ridal.R
import tv.ridal.ui.hideKeyboard
import tv.ridal.ui.layout.Layout
import tv.ridal.ui.listener.InstantPressListener
import tv.ridal.ui.setTextColor
import tv.ridal.ui.showKeyboard
import tv.ridal.util.Locale
import tv.ridal.util.Theme
import tv.ridal.util.Utils

class SearchBar(context: Context) : FrameLayout(context)
{
    private var backButton: ImageView
    private var editText: EditText
    private var clearButton: ImageView

    private var clearAnimator: ValueAnimator = ValueAnimator().apply {
        duration = 150
    }

    private var onBack: (() -> Unit)? = null
    fun onBack(l: () -> Unit)
    {
        onBack = l
    }

    private var onStopTyping: ((String) -> Unit)? = null
    fun onStopTyping(l: (String) -> Unit)
    {
        onStopTyping = l
    }

    private var onTextChange: (() -> Unit)? = null
    fun onTextChange(l: () -> Unit)
    {
        onTextChange = l
    }

    private var onTextClear: (() -> Unit)? = null
    fun onTextClear(l: () -> Unit)
    {
        onTextClear = l
    }

    val currentText: String get() = editText.text.toString()

    private var timer = ValueAnimator.ofFloat(0F, 1F).apply {
        duration = 100

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?)
            {
                super.onAnimationEnd(animation)

                onStopTyping?.invoke(currentText)
            }
        })
    }

    init
    {
        backButton = ImageView(context).apply {
            setOnTouchListener( InstantPressListener(this) )
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable( Theme.drawable( R.drawable.back, Theme.color_actionBar_back ) )

            setOnClickListener {
                onBack?.invoke()
            }
        }
        addView(backButton, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
            Gravity.START
        ))

        editText = EditText(context).apply {
            background = null
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            setTextColor( Theme.color_text )
            textSize = 18F
            typeface = Theme.typeface(Theme.tf_normal)
            setLines(1)
            maxLines = 1
            isSingleLine = true

            hint = Locale.string(R.string.search_hint)
            setHintTextColor( Theme.color(Theme.color_text2) )

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
                {
                    onTextChange?.invoke()

                    if (s.isEmpty())
                    {
                        showClearButton(false)
                        onTextClear?.invoke()
                    }
                    else
                    {
                        showClearButton(true)

                        if ( ! timer.isRunning)
                        {
                            timer.start()
                        }
                        else
                        {
                            timer.setCurrentFraction(0F) // restarting
                        }
                    }

                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        addView(editText, Layout.ezFrame(
            Layout.MATCH_PARENT, Layout.MATCH_PARENT,
            Gravity.START
        ))

        clearButton = ImageView(context).apply {
            setOnTouchListener( InstantPressListener(this) )
            scaleType = ImageView.ScaleType.CENTER

            setImageDrawable( Theme.drawable(R.drawable.close, Theme.color_actionBar_back) )

            // сразу кнопка спрятана
            scaleX = 0F
            scaleY = 0F

            setOnClickListener {
                editText.text.clear()

                showClearButton(false)
                showKeyboard()

                onTextClear?.invoke()
            }
        }
        addView(clearButton, Layout.ezFrame(
            Layout.WRAP_CONTENT, Layout.MATCH_PARENT,
            Gravity.END,
            15, 0, 0, 0
        ))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        val size = Utils.dp(50)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec( MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( paddingTop + size + paddingBottom, MeasureSpec.EXACTLY )
        )

        backButton.measure(
            MeasureSpec.makeMeasureSpec( size, MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( size, MeasureSpec.EXACTLY )
        )
        clearButton.measure(
            MeasureSpec.makeMeasureSpec( size, MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( size, MeasureSpec.EXACTLY )
        )

        val availableWidth = measuredWidth - backButton.measuredWidth - clearButton.measuredWidth
        editText.measure(
            MeasureSpec.makeMeasureSpec( availableWidth, MeasureSpec.EXACTLY ),
            MeasureSpec.makeMeasureSpec( size, MeasureSpec.EXACTLY )
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)

        editText.layout(
            backButton.measuredWidth,
            paddingTop,
            backButton.measuredWidth + editText.measuredWidth,
            measuredHeight - paddingBottom)
    }


    private fun showClearButton(show: Boolean)
    {
        val fromScale = clearButton.scaleX // == scaleY
        val toScale = if (show) 1F else 0F

        clearAnimator.apply {
            cancel()
            setFloatValues(fromScale, toScale)

            addUpdateListener {
                val value = it.animatedValue as Float

                clearButton.apply {
                    scaleX = value
                    scaleY = value
                }
            }

            start()
        }
    }

    fun showKeyboard()
    {
        editText.showKeyboard()
    }

    fun hideKeyboard()
    {
        editText.hideKeyboard()
    }

}


































//