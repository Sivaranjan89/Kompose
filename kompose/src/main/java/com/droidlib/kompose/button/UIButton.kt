package com.droidlib.kompose.button

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import com.droidlib.kompose.R
import com.droidlib.kompose.utils.*
import java.lang.reflect.Type


class UIButton(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    private var mContext = context

    private var compWidth: Float = 0F
    private var compHeight: Float = 0F
    private var text: String = ""
    private var bgColor: Int = -1
    private var strokeColor : Int = -1
    private var cornerRadius : Float = 0F
    private var strokeWidth : Float = 0F
    private var textSize: Float = 0F
    private var textColor : Int = -1
    private var textStyle : Int = 0
    private var font : Typeface? = null
    private var icon: Int = -1
    private var iconSize: Float = 0F
    private var iconPosition: Int = 1
    private var backgroundImage: Int = -1
    private var gravity: Int = 1
    private var spacing: Float = 0F

    private lateinit var gradientDrawable: GradientDrawable
    private lateinit var parent: LinearLayout
    private lateinit var textView: TextView
    private lateinit var image: ImageView
    private lateinit var space: Space
    private var clickListener: ClickListener? = null
    private var imageClickListener: ImageClickListener? = null

    init {
        val ta = mContext.obtainStyledAttributes(attributeSet, R.styleable.UIButton)
        compWidth = ta.getDimension(R.styleable.UIButton_compWidth, WRAP_CONTENT.toFloat())
        compHeight = ta.getDimension(R.styleable.UIButton_compHeight, WRAP_CONTENT.toFloat())
        text = ta.getString(R.styleable.UIButton_text)!!
        bgColor = ta.getColor(R.styleable.UIButton_backgroundColor, Color.TRANSPARENT)
        strokeColor = ta.getColor(R.styleable.UIButton_strokeColor, Color.BLACK)
        strokeWidth = ta.getDimension(R.styleable.UIButton_strokeWidth, dpToPx(0F))
        cornerRadius = ta.getDimension(R.styleable.UIButton_cornerRadius, dpToPx(0F))
        textSize = ta.getDimension(R.styleable.UIButton_textSize, dpToPx(15F))
        textColor = ta.getColor(R.styleable.UIButton_textColor, Color.BLACK)
        textStyle = ta.getInt(R.styleable.UIButton_textStyle, TEXTSTYLE_NORMAL)
        icon = ta.getResourceId(R.styleable.UIButton_icon, -1)
        iconSize = ta.getDimension(R.styleable.UIButton_iconSize, dpToPx(15F))
        iconPosition = ta.getInt(R.styleable.UIButton_iconPosition, POSITION_LEFT)
        backgroundImage = ta.getResourceId(R.styleable.UIButton_backgroundImage, -1)
        gravity = ta.getInt(R.styleable.UIButton_gravity, GRAVITY_CENTER)
        spacing = ta.getDimension(R.styleable.UIButton_spacing, dpToPx(10F))

        ta.getString(R.styleable.UIButton_fontPath)?.let {path ->
            font = Typeface.createFromAsset(mContext.assets, path)
        }


        ta.recycle()

        drawButton()
    }

    private fun drawButton() {
        this.removeAllViews()

        drawParent()
        drawBackground()
        drawText()
        drawIcon()
        drawSpace()

        if (backgroundImage == -1) {
            parent.background = gradientDrawable
        } else {
            parent.setBackgroundResource(backgroundImage)
        }

        if (iconPosition == POSITION_TOP || iconPosition == POSITION_LEFT) {
            if (icon != -1) {
                parent.addView(image)
                parent.addView(space)
            }
            parent.addView(textView)
        } else {
            parent.addView(textView)
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
            }
            else if (clickListener != null) {
                clickListener?.onButtonClicked(it)
            }
        }
    }

    private fun drawText() {
        textView = TextView(mContext)
        textView.layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        textView.text = text
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            pxToDp(textSize)
        )
        textView.setTextColor(textColor)
        textView.setTypeface(font, textStyle)
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
            space.layoutParams = LayoutParams(1, spacing!!.toInt())
        } else {
            space.layoutParams = LayoutParams(spacing!!.toInt(), 1)
        }
    }

    interface ClickListener {
        fun onButtonClicked(view: View)
    }

    interface ImageClickListener {
        fun onImageClicked(view: View)
    }

    fun onButtonClicked(clickListener: (view: View) -> Unit = {}) : ClickListener {
        val buttonClicked = object : ClickListener {
            override fun onButtonClicked(view: View) {
                clickListener.invoke(view)
            }
        }

        this.clickListener = buttonClicked

        return buttonClicked
    }

    fun onImageClicked(clickListener: (view: View) -> Unit = {}) : ImageClickListener {
        val imageClicked = object : ImageClickListener {
            override fun onImageClicked(view: View) {
                clickListener.invoke(view)
            }
        }

        this.imageClickListener = imageClicked

        return imageClicked
    }
}