package com.papb.buanaabsensi.ui.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment

interface ConfirmDialogListener {
    fun onDialogPositiveClick(dialog: DialogFragment, eventCode: Int?, bundle: Bundle?)
    fun onDialogNegativeClick(dialog: DialogFragment)
}