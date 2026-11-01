package com.example.smilecare.utils

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.smilecare.R
import com.google.android.material.textfield.TextInputLayout

/** Estado ERROR (texto rojo bajo el campo) o limpia el error. */
fun TextInputLayout.showError(@StringRes messageRes: Int?) {
    error = messageRes?.let { context.getString(it) }
    if (messageRes != null) endIconMode = TextInputLayout.END_ICON_NONE
}

/** Estado ÉXITO: check verde al final del campo (solo para campos sin toggle de contraseña). */
fun TextInputLayout.showValid() {
    error = null
    endIconMode = TextInputLayout.END_ICON_CUSTOM
    setEndIconDrawable(R.drawable.ic_check)
    setEndIconTintList(
        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.state_success))
    )
    setEndIconOnClickListener(null)
}

/** Microinteracción: sacudida horizontal al fallar. */
fun View.shake() {
    ObjectAnimator.ofFloat(this, "translationX", 0f, 24f, -24f, 16f, -16f, 8f, 0f)
        .setDuration(400)
        .start()
}