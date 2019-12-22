package com.droidlib.kompose.toast

import android.R.layout
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import com.droidlib.kompose.utils.GRAVITY_CENTER
import com.droidlib.kompose.utils.TEXTSTYLE_BOLD
import com.droidlib.kompose.utils.dpToPx
import com.droidlib.kompose.utils.pxToDp


class UIToast(toastBuilder: Build) {

    private var context: Context
    private var toastText: String
    private var duration: Int
    private var actionText: String
    private var actionColor: Int
    private var cornerRadius: Float
    private var backgroundColor: Int
    private var textColor: Int
    private var textSize: Float
    private var isBoldText: Boolean
    private var strokeColor: Int
    private var strokeWidth: Float
    private var icon: Int
    private var iconSize: Float
    private var clickListener: ToastActionClickListener? = null

    private lateinit var gradientDrawable: GradientDrawable
    private lateinit var parent: LinearLayout
    private lateinit var textView: TextView
    private lateinit var image: ImageView
    private lateinit var space: Space

    init {
        context = toastBuilder.context
        toastText = toastBuilder.text
        duration = toastBuilder.duration
        actionText = toastBuilder.actionText
        actionColor = toastBuilder.actionColor
        cornerRadius = toastBuilder.cornerRadius
        backgroundColor = toastBuilder.backgroundColor
        textColor = toastBuilder.textColor
        textSize = toastBuilder.textSize
        isBoldText = toastBuilder.isBoldText
        strokeColor = toastBuilder.strokeColor
        strokeWidth = toastBuilder.strokeWidth
        icon = toastBuilder.icon
        iconSize = toastBuilder.iconSize
        clickListener = toastBuilder.clickListener

        renderToast()
    }

    private fun renderToast() {
        drawBackground()
        drawParent()
        drawText()
        drawSpace()

        if (icon != -1) {
            drawIcon()
            parent.addView(image)
            parent.addView(space)
        }
        parent.addView(textView)

        val toast = Toast(context)
        toast.duration = duration
        toast.view = parent
        toast.show()
    }

    private fun drawIcon() {
        image = ImageView(context)
        image.layoutParams = LinearLayout.LayoutParams(iconSize.toInt(), iconSize.toInt())
        image.setBackgroundResource(icon)
    }

    private fun drawText() {
        textView = TextView(context)
        textView.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        textView.text = toastText
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_DIP,
            pxToDp(textSize)
        )
        textView.setTextColor(textColor)
        if (isBoldText) {
            textView.setTypeface(null, TEXTSTYLE_BOLD)
        }
    }

    private fun drawParent() {
        parent = LinearLayout(context)
        parent.layoutParams = LinearLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )
        parent.gravity = Gravity.CENTER
        parent.orientation = LinearLayout.HORIZONTAL

        val padding = dpToPx(10F).toInt()
        parent.setPadding(padding, padding, padding, padding)
        parent.background = gradientDrawable
    }

    private fun drawBackground() {
        gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = cornerRadius
        gradientDrawable.setStroke(strokeWidth.toInt(), strokeColor)
        gradientDrawable.setColor(backgroundColor)
    }

    private fun drawSpace() {
        space = Space(context)
        space.layoutParams = LinearLayout.LayoutParams(dpToPx(10F).toInt(), 1)
    }


    class Build(internal val context: Context, internal val text: String) {
        internal var duration: Int = Toast.LENGTH_SHORT
        internal var actionText: String = ""
        internal var actionColor: Int = Color.CYAN
        internal var cornerRadius: Float = 0F
        internal var backgroundColor: Int = Color.TRANSPARENT
        internal var textColor: Int = Color.BLACK
        internal var textSize: Float = dpToPx(10F)
        internal var isBoldText: Boolean = false
        internal var strokeColor: Int = Color.BLACK
        internal var strokeWidth: Float = 0F
        internal var icon: Int = -1
        internal var iconSize: Float = dpToPx(10F)
        internal var clickListener: ToastActionClickListener? = null

        fun duration(duration: Int): Build {
            this.duration = duration
            return this
        }

        fun textColor(color: Int): Build {
            this.textColor = color
            return this
        }

        fun textSize(size: Float): Build {
            this.textSize = dpToPx(size)
            return this
        }

        fun makeBold(isBold: Boolean): Build {
            this.isBoldText = isBold
            return this
        }

        fun backgroundColor(color: Int): Build {
            this.backgroundColor = color
            return this
        }

        fun roundCornered(cornerRadius: Float): Build {
            this.cornerRadius = cornerRadius
            return this
        }

        fun renderStroke(strokeWidth: Float, strokeColor: Int): Build {
            this.strokeWidth = dpToPx(strokeWidth)
            this.strokeColor = strokeColor
            return this
        }

        fun renderIcon(icon: Int, iconSize: Float): Build {
            this.icon = icon
            this.iconSize = dpToPx(iconSize)
            return this
        }

        fun action(text: String, color: Int): Build {
            this.actionText = text
            this.actionColor = color
            return this
        }

        fun execute(clickListener: ToastActionClickListener): Build {
            this.clickListener = clickListener
            return this
        }

        fun show(): UIToast {
            return UIToast(this)
        }

    }

}