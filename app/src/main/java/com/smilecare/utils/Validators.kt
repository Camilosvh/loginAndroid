package com.example.smilecare.utils

import android.util.Patterns
import androidx.annotation.StringRes
import com.example.smilecare.R

object Validators {

    @StringRes
    fun name(value: String): Int? =
        if (value.isBlank()) R.string.err_name_empty else null

    @StringRes
    fun email(value: String): Int? = when {
        value.isBlank() -> R.string.err_email_empty
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> R.string.err_email_format
        else -> null
    }

    @StringRes
    fun password(value: String): Int? = when {
        value.isEmpty() -> R.string.err_password_empty
        value.length < 8 -> R.string.err_password_short
        value.none { it.isUpperCase() } -> R.string.err_password_upper
        value.none { it.isDigit() } -> R.string.err_password_digit
        else -> null
    }

    @StringRes
    fun code(value: String): Int? =
        if (value.length != 6 || !value.all { it.isDigit() }) R.string.err_code_invalid else null
}