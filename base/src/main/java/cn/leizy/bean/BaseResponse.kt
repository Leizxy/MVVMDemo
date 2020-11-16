package cn.leizy.bean

/**
 * @author Created by wulei
 * @date 2020/11/16
 * @description
 */
class BaseResponse<T> {
    var message: String? = null
    var data: T? = null
}