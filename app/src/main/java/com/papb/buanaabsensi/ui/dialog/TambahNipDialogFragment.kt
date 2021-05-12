package com.papb.buanaabsensi.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.papb.buanaabsensi.R
import com.papb.buanaabsensi.databinding.DialogTambahNipBinding
import com.papb.buanaabsensi.ui.base.BaseDialogFragment

class TambahNipDialogFragment : BaseDialogFragment() {

    private var _binding: DialogTambahNipBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTambahNipBinding.inflate(LayoutInflater.from(context))
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.setPositiveButton(R.string.OK) { _, _ ->
                val bundle = Bundle()
                bundle.putString("nip", binding.etNip.text.toString())
                listener.onDialogPositiveClick(this, arguments?.getInt("eventCode"), bundle)
            }.setNegativeButton(R.string.batal) { _, _ ->
                listener.onDialogNegativeClick(this)
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): TambahNipDialogFragment {
            return TambahNipDialogFragment()
        }
    }

}