package cn.leizy.mvvmdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author Created by wulei
 * @date 2020/11/12, 012
 * @description
 */
class MainViewModel : ViewModel() {
    var testBean: TestBean? = null

    val stringData = MutableLiveData<String>()

    fun initString() {
        viewModelScope.launch {
            delay(2000)
            stringData.value = "test"
        }
    }

    override fun onCleared() {
    }
}