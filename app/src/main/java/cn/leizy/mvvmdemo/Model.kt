package cn.leizy.mvvmdemo

import cn.leizy.base.m.IModel

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
class Model : IModel {
    fun getData(block: (String) -> Unit) {
        block("test")
    }
}