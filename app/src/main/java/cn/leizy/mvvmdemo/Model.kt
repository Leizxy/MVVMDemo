package cn.leizy.mvvmdemo

import cn.leizy.base.m.BaseModel

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
class Model : BaseModel {
    fun getData(block: (String) -> Unit) {
        block("test")
    }
}