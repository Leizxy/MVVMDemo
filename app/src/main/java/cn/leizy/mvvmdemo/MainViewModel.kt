package cn.leizy.mvvmdemo

import androidx.lifecycle.ViewModel

/**
 * @author Created by wulei
 * @date 2020/11/12, 012
 * @description
 */
class MainViewModel: ViewModel() {
    var testBean:TestBean? = null

    override fun onCleared() {
        testBean = null
    }
}