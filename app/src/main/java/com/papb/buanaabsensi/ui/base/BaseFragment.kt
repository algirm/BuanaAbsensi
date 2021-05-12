package com.papb.buanaabsensi.ui.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

open class BaseFragment(layoutId: Int) : Fragment(layoutId) {

    fun toastShort(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun toastError(e: Throwable?) {
        val error = e?.message
        Toast.makeText(
            context,
            "An Error Has Occurred: ${error ?: "Something Wrong"}",
            Toast.LENGTH_LONG
        ).show()
    }

    fun snackShort(text: String?) {
        Snackbar.make(
            requireView(),
            text.toString(),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun snackLong(text: String?) {
        Snackbar.make(
            requireView(),
            text.toString(),
            Snackbar.LENGTH_LONG
        ).show()
    }

}