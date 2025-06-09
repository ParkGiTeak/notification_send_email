package com.example.notificationsendemail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationsendemail.databinding.ItemInstalledApplicationBinding
import com.example.notificationsendemail.model.AppInfoData

class InstalledAppListAdapter :
    RecyclerView.Adapter<InstalledAppListAdapter.InstalledApplicationVH>() {
    private var itemList: MutableList<AppInfoData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstalledApplicationVH {
        val binding = ItemInstalledApplicationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InstalledApplicationVH(binding)
    }

    override fun onBindViewHolder(holder: InstalledApplicationVH, position: Int) {
        val safetyPosition = holder.adapterPosition
        if (safetyPosition != RecyclerView.NO_POSITION) {
            holder.bind(safetyPosition)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setItemList(itemList: MutableList<AppInfoData>) {
        this.itemList = itemList
        this.notifyDataSetChanged()
    }

    inner class InstalledApplicationVH(private val binding: ItemInstalledApplicationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val packageManager = binding.root.context.packageManager

            binding.tvAppName.text = itemList[position].appLabelName
            itemList[position].appPackageName?.let {
                binding.ivAppIcon.setImageDrawable(packageManager.getApplicationIcon(it))
            }
        }
    }
}