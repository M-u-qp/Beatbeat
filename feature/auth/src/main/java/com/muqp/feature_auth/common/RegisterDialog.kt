package com.muqp.feature_auth.common

import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import java.lang.ref.WeakReference
import com.muqp.beatbeat.ui.R as CoreUi

class RegisterDialog(context: Context) : AlertDialog.Builder(context) {
    private val contextRef = WeakReference(context)

    private var savedUsername: String = ""
    private var savedEmail: String = ""
    private var savedPassword: String = ""

    private var alertDialog: AlertDialog? = null

    fun show(
        title: String,
        onRegister: (username: String, email: String, password: String) -> Unit
    ) {
        contextRef.get()?.let {
            if (alertDialog == null) {
                val inputLayout = LinearLayout(it).apply {
                    orientation = LinearLayout.VERTICAL
                }

                val inputUsername = EditText(it).apply {
                    hint = context.getString(CoreUi.string.login)
                    setText(savedUsername)
                }
                inputLayout.addView(inputUsername)

                val inputEmail = EditText(it).apply {
                    hint = context.getString(CoreUi.string.email)
                    setText(savedEmail)
                }
                inputLayout.addView(inputEmail)

                val inputPassword = EditText(it).apply {
                    hint = context.getString(CoreUi.string.password)
                    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    setText(savedPassword)
                }

                inputLayout.addView(inputPassword)
                setTitle(title)
                setView(inputLayout)

                setPositiveButton(context.getString(CoreUi.string.register)) { _, _ ->
                    savedUsername = inputUsername.text.toString()
                    savedEmail = inputEmail.text.toString()
                    savedPassword = inputPassword.text.toString()
                    onRegister(savedUsername, savedEmail, savedPassword)
                }

                setNegativeButton(context.getString(CoreUi.string.cancel)) { _, _ ->
                    clearSavedData()
                    alertDialog?.dismiss()
                }

                alertDialog = create().apply {
                    setCancelable(false)
                }
            }
            alertDialog?.show()
        }
    }

    private fun clearSavedData() {
        savedUsername = ""
        savedEmail = ""
        savedPassword = ""
    }
}
