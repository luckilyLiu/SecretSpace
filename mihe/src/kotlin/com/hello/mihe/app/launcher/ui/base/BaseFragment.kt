package com.hello.mihe.app.launcher.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hello.mihe.app.launcher.ui.base.owner.FragmentOwner

abstract class BaseFragment : Fragment() {
    lateinit var fragmentOwner: FragmentOwner

    override fun onAttach(context: Context) {
        if (context is FragmentOwner) {
            fragmentOwner = context
        }
        super.onAttach(context)
    }

    fun <T : ViewModel> createViewModel(clazz: Class<T>): T {
        return ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return modelClass.newInstance() as T
                }
            }
        )
            .get(clazz)
    }

    abstract fun getPageName(): String?

}
