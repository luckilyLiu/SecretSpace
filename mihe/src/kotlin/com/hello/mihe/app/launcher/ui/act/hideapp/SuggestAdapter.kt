package com.hello.mihe.app.launcher.ui.act.hideapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.android.launcher3.databinding.MiheItemSuggestPackageBinding
import com.hello.mihe.app.launcher.view.adapter.BaseAdapter
import com.hello.mihe.app.launcher.view.adapter.BaseHolder
import com.hello.mihe.app.launcher.view.adapter.BaseItemClickListener

class SuggestAdapter(var appClickListener: BaseItemClickListener<AppExt>) : BaseAdapter<AppExt>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<AppExt, ViewBinding, BaseAdapter<AppExt>> {
        return SuggestHolder(

            MiheItemSuggestPackageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), this
        )
    }
}

class SuggestHolder(binding: MiheItemSuggestPackageBinding, adapter: SuggestAdapter) : BaseHolder<AppExt, MiheItemSuggestPackageBinding, SuggestAdapter>(binding, adapter) {
    override fun bindView(positionData: AppExt, position: Int) {
        binding.tvItemName.text = positionData.app.label
        binding.imgApp.setImageBitmap(positionData.app.icon)
        binding.itemZfileListFileCheckBox.isSelected = positionData.isSelected
        adapter.itemClickListener
        binding.imgApp.setOnClickListener {
            adapter.appClickListener.onClick(this, positionData, position)
        }
    }

}