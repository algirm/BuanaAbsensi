package com.papb.buanaabsensi.ui.riwayat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.papb.buanaabsensi.data.model.Presensi
import com.papb.buanaabsensi.databinding.ItemRiwayatBinding
import com.papb.buanaabsensi.util.Constants.Companion.ALPHA
import com.papb.buanaabsensi.util.Constants.Companion.BELUM_PRESENSI
import com.papb.buanaabsensi.util.Constants.Companion.SELESAI
import java.text.SimpleDateFormat
import java.util.*

class RiwayatAdapter : RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>() {

    inner class RiwayatViewHolder(
        val binding: ItemRiwayatBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Presensi>() {
        override fun areItemsTheSame(oldItem: Presensi, newItem: Presensi): Boolean {
            return oldItem.tanggal == newItem.tanggal
        }

        override fun areContentsTheSame(oldItem: Presensi, newItem: Presensi): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var listPresensi: List<Presensi>
    get() = differ.currentList
    set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatViewHolder {
        return RiwayatViewHolder(
            ItemRiwayatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RiwayatViewHolder, position: Int) {
        val presensi = listPresensi[position]
        with(holder.binding) {
            presensi.tanggal?.let {
                textTanggal.text =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it.toDate())
            }
            presensi.statusPresensi?.let {
                val status = when(it) {
                    ALPHA -> "ALPHA"
                    BELUM_PRESENSI -> "Belum Presensi"
                    SELESAI -> "Hadir"
                    else -> "Error"
                }
                textStatus.text = status
            }
        }
    }

    override fun getItemCount(): Int {
        return listPresensi.size
    }
}