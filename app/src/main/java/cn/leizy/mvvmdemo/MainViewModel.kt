package cn.leizy.mvvmdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.leizy.base.vm.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Created by wulei
 * @date 2020/11/12, 012
 * @description
 */
class MainViewModel : BaseViewModel<Model>() {

    val stringData = MutableLiveData<String>()

    fun initString() {
        model.getData {
            viewModelScope.launch {
                delay(2000)
                stringData.value = "test"
            }
        }
    }

    override fun onCleared() {
    }
}