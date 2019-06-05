package com.venmo.android.pin.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff.Mode
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.venmo.android.pin.R

class PinKeyboardView : KeyboardView {
    private var mKeyBackgroundDrawable: Drawable? = null
    private var mShowUnderline: Boolean = false
    private var mUnderlinePadding: Int = 0
    private var mPaint: Paint? = null
    private var mUnderlinePaint: Paint? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet, defStyle: Int) {
        // @formatter:off
        val a = context.obtainStyledAttributes(attrs, R.styleable.PinKeyboardView, defStyle, 0)
        // @formatter:on
        val res = resources
        mKeyBackgroundDrawable =
            a.getDrawable(R.styleable.PinKeyboardView_pinkeyboardview_keyBackground)
        mShowUnderline =
            a.getBoolean(R.styleable.PinKeyboardView_pinkeyboardview_showUnderline, false)
        mUnderlinePadding = a.getDimensionPixelSize(
            R.styleable.PinKeyboardView_pinkeyboardview_underlinePadding,
            res.getDimensionPixelSize(R.dimen.keyboard_underline_padding)
        )
        val textSize = a.getDimensionPixelSize(
            R.styleable.PinKeyboardView_pinkeyboardview_textSize,
            res.getDimensionPixelSize(R.dimen.pin_keyboard_default_text_size)
        )
        val textColor =
            a.getColor(R.styleable.PinKeyboardView_pinkeyboardview_textColor, Color.BLACK)
        val underlineColor = a.getColor(
            R.styleable.PinKeyboardView_pinkeyboardview_keyUnderlineColor,
            ContextCompat.getColor(context, R.color.pin_light_gray_50)

        )
        a.recycle()

        mPaint = Paint()
        mPaint!!.textAlign = Paint.Align.CENTER
        mPaint!!.isAntiAlias = true
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            mPaint!!.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        }
        mPaint!!.textSize = textSize.toFloat()
        mPaint!!.color = textColor

        mUnderlinePaint = Paint()

        mUnderlinePaint!!.color = underlineColor
        val stroke = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f,
            resources.displayMetrics
        )
        mUnderlinePaint!!.strokeWidth = stroke
        isPreviewEnabled = false
        keyboard = Keyboard(context, R.xml.keyboard_number_pad)
        val back = ContextCompat.getDrawable(context, R.drawable.key_back)
        back!!.setColorFilter(textColor, Mode.SRC_ATOP)
    }

    override fun onDraw(canvas: Canvas) {
        val keyBackground = mKeyBackgroundDrawable
        val keys = keyboard.keys

        for (key in keys) {
            if (keyBackground != null && (key.icon != null || key.label != null)) {
                val state = key.currentDrawableState
                keyBackground.state = state
                keyBackground.setBounds(key.x, key.y, key.x + key.width, key.y + key.height)
                keyBackground.draw(canvas)
            }
            if (key.label != null) {
                val label = key.label.toString()
                val desiredW = mPaint!!.measureText(label)
                val desiredH = mPaint!!.measureText(label)
                val x = (key.x + (key.width shr 1)).toFloat()
                val y = key.y.toFloat() + (key.height shr 1).toFloat() + desiredH / 2
                canvas.drawText(label, x, y, mPaint!!)
                if (mShowUnderline) {
                    canvas.drawLine(
                        (key.x + mUnderlinePadding).toFloat(),
                        (key.y + key.height - mUnderlinePadding).toFloat(),
                        (key.x + key.width - mUnderlinePadding).toFloat(),
                        (key.y + key.height - mUnderlinePadding).toFloat(),
                        mUnderlinePaint!!
                    )
                }
            } else if (key.icon != null) {
                val startX = key.x + (key.width - key.icon.intrinsicWidth) / 2
                val startY = key.y + (key.height - key.icon.intrinsicHeight) / 2
                key.icon.setBounds(
                    startX,
                    startY,
                    startX + key.icon.intrinsicWidth,
                    startY + key.icon.intrinsicHeight
                )
                key.icon.draw(canvas)
            }
        }
    }

    abstract class PinPadActionListener : OnKeyboardActionListener {
        abstract override fun onKey(primaryCode: Int, keyCodes: IntArray)

        override fun onPress(primaryCode: Int) {}

        override fun onRelease(primaryCode: Int) {}

        override fun onText(text: CharSequence) {}

        override fun swipeLeft() {}

        override fun swipeRight() {}

        override fun swipeDown() {}

        override fun swipeUp() {}
    }

    companion object {
        const val KEYCODE_DELETE = -5
    }
}
