package com.riwal.rentalapp.common.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import kotlinx.android.synthetic.main.edit_view.view.*

// TODO: Change name, this represents an
class EditTextView : LinearLayout {


    /*--------------------------------------- Properties -----------------------------------------*/


    var text: String?
        get() = editText.text.toString()
        set(value) {
            editText.text = value
        }

    var hint: String?
        get() = textInputLayout.hint.toString()
        set(value) {
            textInputLayout.hint = value
        }

    var onTextChangedListener: ((String) -> Unit)? = null
        set(value) {
            field = value
            editText.onTextChangedListener = value
        }


    var onFocusChangeListener: ((View, Boolean) -> Unit)? = null
        set(value) {
            field = value
            editText.onFocusChangeListener = value
        }

    var onEditorActionListener: ((Int, KeyEvent?) -> Unit)? = null
        set(value) {
            field = value
            editText.onEditorActionListener = value
        }

    var isEditable: Boolean
        get() = isFocusable
        set(value) {
            isFocusable = value
            isFocusableInTouchMode = value
            editText.isFocusable = value
            editText.isFocusableInTouchMode = value
        }

    var error: String?
        get() = textInputLayout.error.toString()
        set(value) {
            textInputLayout.error = value
            textInputLayout.isErrorEnabled = value != null
        }

    var tintColor: Int? = R.color.material_grey_600
        set(value) {
            iconImageView.setTintFromResource(value!!)
            field = value
        }

    override fun isEnabled() = editText.isEnabled

    override fun setEnabled(enabled: Boolean) {
        editText.isEnabled = enabled
    }


    /*-------------------------------------- Constructors ----------------------------------------*/


    constructor(context: Context) : super(context) {
        initialize(context, attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context, attrs = attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initialize(context, attrs = attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {

        LayoutInflater.from(context).inflate(R.layout.edit_view, this, true)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.EditTextView, 0, 0)
            val icon = a.getDrawable(R.styleable.EditTextView_android_src)

            textInputLayout.hint = a.getText(R.styleable.EditTextView_android_hint)
            iconImageView.setImageDrawable(icon)
            editText.inputType = a.getInt(R.styleable.EditTextView_android_inputType, editText.inputType)
            editText.imeOptions = a.getInt(R.styleable.EditTextView_android_imeOptions, editText.imeOptions)
            editText.isFocusable = a.getBoolean(R.styleable.EditTextView_android_focusable, editText.isFocusable)
            editText.isFocusableInTouchMode = a.getBoolean(R.styleable.EditTextView_android_focusableInTouchMode, editText.isFocusable)
            editText.isEnabled = a.getBoolean(R.styleable.EditTextView_android_enabled, editText.isEnabled)

            a.recycle()
        }
    }


    /*------------------------------------- Super methods ----------------------------------------*/


    override fun setOnClickListener(onClickListener: OnClickListener?) {
        isClickable = true
        super.setOnClickListener(onClickListener)
        editText.setOnClickListener(onClickListener)
        iconImageView.setOnClickListener(onClickListener)
    }


}