package com.droidlib.kompose.otpview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import androidx.core.widget.doAfterTextChanged
import com.droidlib.kompose.R
import com.droidlib.kompose.utils.dpToPx
import com.droidlib.kompose.utils.hideKeyboard
import com.droidlib.kompose.utils.pxToDp
import com.droidlib.kompose.utils.showKeyboard


class OTPView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val mContext = context

    private var boxWidth : Float?
    private var boxHeight : Float?
    private var spacing : Float?
    private var count : Int?
    private var isVertical : Boolean?
    private var showOtp : Boolean?
    private var openWithKeyboard : Boolean?
    private var disableOtp : Boolean?
    private var textColor : Int?
    private var textSize : Float?
    private var backgroundColor : Int?
    private var strokeColor : Int?
    private var cornerRadius : Float?
    private var strokeWidth : Float?
    private var useOriginalBackground : Boolean?
    private var visibleCursor : Boolean?
    private var disabledAlpha : Float?

    private var editList : MutableList<EditText> = ArrayList()
    private var spaceList : MutableList<Space> = ArrayList()
    private var otp: String = ""
    private lateinit var gradientDrawable: GradientDrawable
    private lateinit var parent: LinearLayout
    private lateinit var observeEnteredOTP: ObserveEnteredOTP

    init {
        val ta = mContext.obtainStyledAttributes(attrs, R.styleable.OTPView)
        boxWidth = ta.getDimension(R.styleable.OTPView_boxWidth, WRAP_CONTENT.toFloat())
        boxHeight = ta.getDimension(R.styleable.OTPView_boxHeight, WRAP_CONTENT.toFloat())
        spacing = ta.getDimension(R.styleable.OTPView_spacing,
            dpToPx(0F)
        )
        count = ta.getInt(R.styleable.OTPView_boxCount, 4)
        isVertical = ta.getBoolean(R.styleable.OTPView_isVertical, false)
        showOtp = ta.getBoolean(R.styleable.OTPView_showOtp, false)
        openWithKeyboard = ta.getBoolean(R.styleable.OTPView_openWithKeyboard, true)
        disableOtp = ta.getBoolean(R.styleable.OTPView_disableOtp, false)
        textColor = ta.getColor(R.styleable.OTPView_textColor, Color.BLACK)
        textSize = ta.getDimension(R.styleable.OTPView_textSize,
            dpToPx(15F)
        )
        backgroundColor = ta.getColor(R.styleable.OTPView_backgroundColor, Color.TRANSPARENT)
        strokeColor = ta.getColor(R.styleable.OTPView_strokeColor, Color.BLACK)
        cornerRadius = ta.getDimension(R.styleable.OTPView_cornerRadius,
            dpToPx(0F)
        )
        strokeWidth = ta.getDimension(R.styleable.OTPView_strokeWidth,
            dpToPx(0F)
        )
        useOriginalBackground = ta.getBoolean(R.styleable.OTPView_useOriginalBackground, false)
        visibleCursor = ta.getBoolean(R.styleable.OTPView_visibleCursor, true)
        disabledAlpha = ta.getFloat(R.styleable.OTPView_disabledAlpha, 0.5F)

        ta.recycle()

        drawOTPView()
    }

    private fun drawOTPView() {
        this.removeAllViews()

        if (disabledAlpha!! > 1 || disabledAlpha!! < 0) {
            disabledAlpha = 0.5F
        }

        drawParent()
        drawBackground()
        drawEditTexts()
        addWatchers()
        addKeyListener()
        addFocusListener()
        drawSpace()

        for (i in 0..count!! - 1) {
            parent.addView(editList.get(i))

            if (i < count!! - 1) {
                parent.addView(spaceList.get(i))
            }
        }

        this.addView(parent, LayoutParams(WRAP_CONTENT, WRAP_CONTENT))

        if (openWithKeyboard!!) {
            editList.get(0).requestFocus()
        }
    }

    private fun drawParent() {
        parent = LinearLayout(mContext)

        if (isVertical!!) {
            parent.orientation = LinearLayout.VERTICAL
        } else {
            parent.orientation = LinearLayout.HORIZONTAL
        }
    }

    private fun drawBackground() {
        gradientDrawable = GradientDrawable()
        gradientDrawable.cornerRadius = cornerRadius!!
        gradientDrawable.setStroke(strokeWidth!!.toInt(), strokeColor!!)
        gradientDrawable.setColor(backgroundColor!!)
    }

    private fun drawSpace() {
        for (i in 0..count!! - 2) {
            var space = Space(mContext)
            if (isVertical!!) {
                space.layoutParams = LayoutParams(1, spacing!!.toInt())
            } else {
                space.layoutParams = LayoutParams(spacing!!.toInt(), 1)
            }
            spaceList.add(space)
        }
    }

    private fun drawEditTexts() {
        for (i in 0..count!! - 1) {
            var edit = EditText(mContext)
            edit.imeOptions = EditorInfo.IME_ACTION_DONE
            edit.gravity = Gravity.CENTER
            edit.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                pxToDp(textSize!!)
            )
            edit.setTextColor(textColor!!)
            edit.inputType = InputType.TYPE_CLASS_NUMBER

            edit.isCursorVisible = visibleCursor!!

            if (!useOriginalBackground!!) {
                edit.background = gradientDrawable
            }

            if (showOtp!!) {
                edit.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                edit.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            val params = LayoutParams(boxWidth!!.toInt(), boxHeight!!.toInt())
            edit.layoutParams = params
            edit.tag = i

            if (i != 0 || disableOtp!!) {
                edit.isEnabled = false
                edit.alpha = disabledAlpha!!
            }

            editList.add(edit)
        }
    }

    private fun addKeyListener() {
        for (i in 0..count!! - 1) {
            editList.get(i).setOnKeyListener { v, keyCode, event ->
                if (event.getAction()!=KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && i != 0) {
                    if ((v as EditText).text.length > 0) {
                        v.text.clear()
                    } else {
                        moveFocusBackward(i - 1)
                    }
                }

                false
            }
        }
    }

    private fun addFocusListener() {
        for (i in 0..count!! - 1) {
            editList.get(i).setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    (v as EditText).setSelection(v.text.length)
                    v.showKeyboard()
                }
            }
        }
    }

    private fun addWatchers() {
        for (i in 0..count!! - 1) {
            editList.get(i).doAfterTextChanged {
                if (!it.toString().isEmpty()) {
                    if (it.toString().length > 1) {
                        editList.get(i).setText(removeLastCharacter(it.toString()))
                    }
                    else {
                        if (i < count!! - 1) {
                            moveFocusForward(i + 1)
                        } else {
                            editList.get(i).clearFocus()
                            hideKeyboard()
                            appendOTP()
                            observeEnteredOTP
                            observeEnteredOTP.onOTPEntered(otp)
                        }
                    }
                }
            }
        }
    }

    private fun moveFocusBackward(position: Int) {
        editList.get(position + 1).isEnabled = false
        editList.get(position).isEnabled = true
        editList.get(position).alpha = 1F
        editList.get(position + 1).alpha = disabledAlpha!!
        editList.get(position).requestFocus()
    }

    private fun moveFocusForward(position: Int) {
        editList.get(position - 1).isEnabled = false
        editList.get(position).isEnabled = true
        editList.get(position).alpha = 1F
        editList.get(position - 1).alpha = disabledAlpha!!
        editList.get(position).requestFocus()
    }

    private fun removeLastCharacter(string: String) : String {
        return string.substring(string.length - 1, string.length)
    }

    private fun appendOTP() {
        otp = ""
        for (box in editList) {
            otp = otp + box.text.toString()
        }
    }

    interface ObserveEnteredOTP {
        fun onOTPEntered(otp: String)
    }

    fun observeEnteredOTP(observeEnteredOTP: (otp: String) -> Unit = {}) : ObserveEnteredOTP {
        val observer = object : ObserveEnteredOTP {
            override fun onOTPEntered(otp: String) {
                observeEnteredOTP.invoke(otp)
            }
        }

        this.observeEnteredOTP = observer

        return observer
    }
}