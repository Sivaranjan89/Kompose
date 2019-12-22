package com.droidlib.kompose.edit

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputFilter.LengthFilter
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import com.droidlib.kompose.R
import com.droidlib.kompose.utils.*


class UIEditText(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private var mContext = context

    private var compWidth: Float = 0F
    private var compHeight: Float = 0F
    private var text: String = ""
    private var bgColor: Int = -1
    private var strokeColor: Int = -1
    private var cornerRadius: Float = 0F
    private var strokeWidth: Float = 0F
    private var textSize: Float = 0F
    private var textColor: Int = -1
    private var textStyle: Int = 0
    private var font: Typeface? = null
    private var icon: Int = -1
    private var iconSize: Float = 0F
    private var iconPosition: Int = 1
    private var gravity: Int = 1
    private var spacing: Float = 0F
    private var hint: String = ""
    private var hintColor: Int = -1
    private var useOriginalBackground: Boolean = false
    private var visibleCursor: Boolean = false
    private var padding: Float = 0F
    private var inputType: Int = -1
    private var allowPaste: Boolean = true
    private var allCaps: Boolean = false
    private var maxLength: Int = 0
    private var lines: Int = 0
    private var letterSpacing: Float = 0F
    private var lineSpacing: Float = 0F
    private var imeOptions: Int = 6
    private var digits: String = ""
    private var maxLines: Int = 0

    private lateinit var gradientDrawable: GradientDrawable
    private lateinit var parent: LinearLayout
    private lateinit var editText: TextView
    private lateinit var image: ImageView
    private lateinit var space: Space
    private var clickListener: ClickListener? = null
    private var imageClickListener: ImageClickListener? = null

    init {
        val ta = mContext.obtainStyledAttributes(attributeSet, R.styleable.UIEditText)
        compWidth = ta.getDimension(R.styleable.UIEditText_compWidth, WRAP_CONTENT.toFloat())
        compHeight = ta.getDimension(R.styleable.UIEditText_compHeight, WRAP_CONTENT.toFloat())
        bgColor = ta.getColor(R.styleable.UIEditText_backgroundColor, Color.TRANSPARENT)
        strokeColor = ta.getColor(R.styleable.UIEditText_strokeColor, Color.BLACK)
        strokeWidth = ta.getDimension(R.styleable.UIEditText_strokeWidth, dpToPx(0F))
        cornerRadius = ta.getDimension(R.styleable.UIEditText_cornerRadius, dpToPx(0F))
        textSize = ta.getDimension(R.styleable.UIEditText_textSize, dpToPx(15F))
        textColor = ta.getColor(R.styleable.UIEditText_textColor, Color.BLACK)
        textStyle = ta.getInt(R.styleable.UIEditText_textStyle, TEXTSTYLE_NORMAL)
        icon = ta.getResourceId(R.styleable.UIEditText_icon, -1)
        iconSize = ta.getDimension(R.styleable.UIEditText_iconSize, dpToPx(15F))
        iconPosition = ta.getInt(R.styleable.UIEditText_iconPosition, POSITION_LEFT)
        gravity = ta.getInt(R.styleable.UIEditText_gravity, GRAVITY_CENTER)
        spacing = ta.getDimension(R.styleable.UIEditText_spacing, dpToPx(10F))
        hintColor = ta.getColor(R.styleable.UIEditText_hintColor, Color.BLACK)
        useOriginalBackground = ta.getBoolean(R.styleable.UIEditText_useOriginalBackground, false)
        visibleCursor = ta.getBoolean(R.styleable.UIEditText_visibleCursor, true)
        padding = ta.getDimension(R.styleable.UIEditText_padding, dpToPx(5F))
        inputType = ta.getInt(R.styleable.UIEditText_inputType, INPUT_TEXT)
        allowPaste = ta.getBoolean(R.styleable.UIEditText_allowPaste, true)
        allCaps = ta.getBoolean(R.styleable.UIEditText_allCaps, false)
        maxLength = ta.getInt(R.styleable.UIEditText_maxLength, 0)
        lines = ta.getInt(R.styleable.UIEditText_lines, 0)
        letterSpacing = ta.getDimension(R.styleable.UIEditText_letterSpacing, dpToPx(0F))
        lineSpacing = ta.getDimension(R.styleable.UIEditText_lineSpacing, dpToPx(0F))
        imeOptions = ta.getInt(R.styleable.UIEditText_imeOptions, 6)
        maxLines = ta.getInt(R.styleable.UIEditText_maxLines, 0)

        ta.getString(R.styleable.UIEditText_text)?.let { it ->
            text = it
        }

        ta.getString(R.styleable.UIEditText_hint)?.let { it ->
            hint = it
        }

        ta.getString(R.styleable.UIEditText_fontPath)?.let { path ->
            font = Typeface.createFromAsset(mContext.assets, path)
        }

        ta.getString(R.styleable.UIEditText_digits)?.let { it ->
            digits = it
        }


        ta.recycle()

        drawEditText()
    }

    private fun drawEditText() {
        this.removeAllViews()

        drawParent()
        drawBackground()
        drawText()

        if (icon != -1) {
            drawIcon()
            drawSpace()
        }

        parent.background = gradientDrawable

        if (iconPosition == POSITION_RIGHT) {
            parent.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }

        if (iconPosition == POSITION_TOP || iconPosition == POSITION_LEFT || iconPosition == POSITION_RIGHT) {
            if (icon != -1) {
                parent.addView(image)
                parent.addView(space)
            }
            parent.addView(editText)
        } else {
            parent.addView(editText)
            if (icon != -1) {
                parent.addView(space)
                parent.addView(image)
            }
        }
        this.addView(parent, LayoutParams(compWidth.toInt(), compHeight.toInt()))
        this.setOnClickListener {
            clickListener?.onButtonClicked(it)
        }
    }

    private fun drawIcon() {
        image = ImageView(mContext)
        image.layoutParams = LayoutParams(iconSize.toInt(), iconSize.toInt())
        image.setBackgroundResource(icon)
        image.setOnClickListener {
            if (imageClickListener != null) {
                imageClickListener!!.onImageClicked(it)
            } else if (clickListener != null) {
                clickListener?.onButtonClicked(it)
            }
        }
    }

    private fun drawText() {
        editText = EditText(mContext)
        editText.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        editText.text = text
        editText.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            pxToDp(textSize)
        )
        editText.setTextColor(textColor)
        editText.setTypeface(font, textStyle)
        editText.hint = hint
        editText.setHintTextColor(hintColor)
        editText.gravity = GRAVITY_LEFT_CENTERVERTICAL
        editText.isCursorVisible = visibleCursor
        editText.inputType = inputType
        editText.imeOptions = imeOptions
        editText.isLongClickable = allowPaste

        if (!useOriginalBackground) {
            editText.background = null
        }

        val filters: ArrayList<InputFilter> = ArrayList()
        if (maxLength > 0) {
            filters.add(LengthFilter(maxLength))
        }
        if (allCaps) {
            filters.add(AllCaps())
        }
        val finalFilters = arrayOfNulls<InputFilter>(filters.size)
        for (i in 0 until filters.size) {
            finalFilters[i] = filters[i]
        }

        if (maxLength > 0 || allCaps) {
            editText.filters = finalFilters
        }

        if (maxLines > 0) {
            editText.maxLines = maxLines
        }
        if (lines > 0) {
            editText.setLines(lines)
        }
        if (!digits.isEmpty()) {
            editText.setKeyListener(DigitsKeyListener.getInstance(digits))
        }

        editText.letterSpacing = letterSpacing
        editText.setLineSpacing(lineSpacing, 1F)
    }

    private fun drawParent() {
        parent = LinearLayout(mContext)
        parent.layoutParams = LayoutParams(
            compWidth.toInt(),
            compHeight.toInt()
        )
        parent.gravity = gravity

        if (iconPosition == POSITION_TOP || iconPosition == POSITION_BOTTOM) {
            parent.orientation = LinearLayout.VERTICAL
        } else {
            parent.orientation = LinearLayout.HORIZONTAL
        }

        parent.setPadding(padding.toInt(), padding.toInt(), padding.toInt(), padding.toInt())
    }

    private fun drawBackground() {
        gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = cornerRadius
        gradientDrawable.setStroke(strokeWidth.toInt(), strokeColor)
        gradientDrawable.setColor(bgColor)
    }

    private fun drawSpace() {
        space = Space(mContext)
        if (iconPosition == POSITION_TOP || iconPosition == POSITION_BOTTOM) {
            space.layoutParams = LayoutParams(1, spacing.toInt())
        } else {
            space.layoutParams = LayoutParams(spacing.toInt(), 1)
        }
    }

    interface ClickListener {
        fun onButtonClicked(view: View)
    }

    interface ImageClickListener {
        fun onImageClicked(view: View)
    }

    fun onButtonClicked(clickListener: (view: View) -> Unit = {}): ClickListener {
        val buttonClicked = object : ClickListener {
            override fun onButtonClicked(view: View) {
                clickListener.invoke(view)
            }
        }

        this.clickListener = buttonClicked

        return buttonClicked
    }

    fun onImageClicked(clickListener: (view: View) -> Unit = {}): ImageClickListener {
        val imageClicked = object : ImageClickListener {
            override fun onImageClicked(view: View) {
                clickListener.invoke(view)
            }
        }

        this.imageClickListener = imageClicked

        return imageClicked
    }
}