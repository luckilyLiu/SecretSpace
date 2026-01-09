package com.hello.mihe.app.launcher.ui.act.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import app.lawnchair.util.App
import com.android.launcher3.databinding.MiheMainActivityItemBinding
import com.hello.mihe.app.launcher.view.adapter.BaseAdapter
import com.hello.mihe.app.launcher.view.adapter.BaseHolder

open class MiheMainAdapter : BaseAdapter<App>() {
    var showHideIcon = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<App, ViewBinding, BaseAdapter<App>> {
        return MiheMainHolder(
            MiheMainActivityItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), this
        )
    }
}

class MiheMainHolder(binding: MiheMainActivityItemBinding, adapter: MiheMainAdapter) : BaseHolder<App,
        MiheMainActivityItemBinding, MiheMainAdapter>(binding, adapter) {
    override fun bindView(positionData: App, position: Int) {
        binding.imageItem.setImageBitmap(positionData.icon)
        binding.tvItemName.setText(positionData.label)
        if (adapter.showHideIcon) {
            binding.imageHideIcon.visibility = View.VISIBLE
        } else {
            binding.imageHideIcon.visibility = View.GONE
        }
    }

}