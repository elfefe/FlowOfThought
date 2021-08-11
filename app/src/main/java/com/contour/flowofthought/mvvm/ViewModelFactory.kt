package com.contour.flowofthought.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.contour.flowofthought.mvvm.repository.MainDbRepository
import com.contour.flowofthought.mvvm.viewmodel.MainDbViewModel

class ViewModelFactory : ViewModelProvider.Factory {

    private val thoughtRepository = MainDbRepository()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainDbViewModel::class.java)) {
            return MainDbViewModel(thoughtRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
    companion object {
        val INSTANCE = ViewModelFactory()
        fun PROVIDER(viewModelStoreOwner: ViewModelStoreOwner) = ViewModelProvider(viewModelStoreOwner, INSTANCE)
    }
}