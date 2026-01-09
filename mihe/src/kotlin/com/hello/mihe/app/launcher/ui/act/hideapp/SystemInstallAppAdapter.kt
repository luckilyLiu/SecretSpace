package com.hello.mihe.app.launcher.ui.act.hideapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.android.launcher3.databinding.MiheItemSearchPackageBinding
import com.hello.mihe.app.launcher.view.adapter.BaseAdapter
import com.hello.mihe.app.launcher.view.adapter.BaseHolder
import com.hello.mihe.app.launcher.view.adapter.BaseItemClickListener

class SystemInstallAppAdapter(var appClickListener: BaseItemClickListener<AppExt>) : BaseAdapter<AppExt>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseHolder<AppExt, ViewBinding, SystemInstallAppAdapter> {
        return SystemInstallAppHolder(
            MiheItemSearchPackageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            this
        )
    }
}

class SystemInstallAppHolder(
    itemViewBinding: MiheItemSearchPackageBinding,
    adapter: SystemInstallAppAdapter
) :
    BaseHolder<AppExt, MiheItemSearchPackageBinding, SystemInstallAppAdapter>(
        itemViewBinding,
        adapter
    ) {
    override fun bindView(positionData: AppExt, position: Int) {
        binding.name.text = positionData.app.label
        binding.icon.setImageBitmap(positionData.app.icon)
        binding.tvSortLetter.text = positionData.firstLetter
        var visibility = View.VISIBLE
        if (position != 0 && adapter.getData()[position - 1].firstLetter == positionData.firstLetter) {
            visibility = View.GONE
        }
        if (visibility == View.VISIBLE) {
            binding.tvSortLetter.text = positionData.firstLetter
        }
        binding.tvSortLetter.visibility = visibility
        if (position == adapter.itemCount - 1) {
            binding.includeLine.root.visibility = View.GONE
        } else {
            binding.includeLine.root.visibility = View.VISIBLE
        }
        binding.root.setOnClickListener {
            adapter.appClickListener.onClick(this, positionData, position)
        }
        binding.itemZfileListFileCheckBox.isSelected = positionData.isSelected
    }

}
