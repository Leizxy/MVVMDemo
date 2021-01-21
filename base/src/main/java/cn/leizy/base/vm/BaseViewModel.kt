package cn.leizy.base.vm

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.leizy.base.App
import cn.leizy.base.m.IModel
import cn.leizy.base.v.IView
import cn.leizy.bean.BaseResponse
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
open class BaseViewModel<V : IView, M : IModel> : AndroidViewModel(App.getInstance()), IViewModel {

    private val jobs: MutableList<Job> = mutableListOf()
    private lateinit var weakReference:WeakReference<V>
    protected lateinit var view: V
    protected val model: M

    init {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val clazzM: Class<M> = pt.actualTypeArguments[1] as Class<M>
        model = clazzM.newInstance()
    }

    fun bindView(view: V) {
        weakReference = WeakReference(view)
        this.view = weakReference.get()!!
    }

    protected fun showToast(str: String?) {
        view.showToast(str!!)
    }

    @SuppressLint("SupportAnnotationUsage")
    @StringRes
    protected fun showToast(idRes: Int) {
        view.showToast(idRes)
    }

    /**
     * @param tryBlock 传入请求的代码块
     * @param successBlock 成功代码块
     * @param failBlock 失败代码块（可不传，采用默认处理）
     * @param finallyBlock 最终处理代码块（可不传）
     * @param isChain 链式调用（为了防止链式调用时暂时别dismiss loading）
     */
    protected fun <T> launchRequest(
            tryBlock: suspend CoroutineScope.() -> BaseResponse<T>?,
            successBlock: suspend CoroutineScope.(T?) -> Unit,
            failBlock: (suspend CoroutineScope.(String?) -> Unit) = {
                launch {
                    if (showTips) {
                        showToast(it)
                    }
                }
            },
            finallyBlock: (suspend CoroutineScope.() -> Unit)? = null,
            isChain: Boolean = false,
            showTips: Boolean = true
    ) {
        if (showTips) {
            view.showLoading()
        }
        launchMain {
            tryCatch(tryBlock, successBlock, failBlock, finallyBlock, isChain, showTips)
        }
    }

    fun launchMain(block: suspend CoroutineScope.() -> Unit) =
            addJob(viewModelScope.launch(Dispatchers.Main, block = block))

    fun launchOnIO(block: suspend CoroutineScope.() -> Unit) =
            addJob(viewModelScope.launch(Dispatchers.IO) { block() })

    fun launchOnDefault(block: suspend CoroutineScope.() -> Unit) =
            addJob(viewModelScope.launch(Dispatchers.Default) { block() })

    /**
     * @param tryBlock 传入请求的代码块
     * @param successBlock 成功代码块
     * @param failBlock 失败代码块
     * @param finallyBlock 最终处理代码块
     * @param isChain 链式调用（为了防止链式调用时暂时别dismiss loading）
     */
    private suspend fun <T> tryCatch(
            tryBlock: suspend CoroutineScope.() -> BaseResponse<T>?,
            successBlock: suspend CoroutineScope.(T?) -> Unit,
            failBlock: suspend CoroutineScope.(String?) -> Unit,
            finallyBlock: (suspend CoroutineScope.() -> Unit)? = null,
            isChain: Boolean,
            showTips: Boolean
    ) {
        coroutineScope {
            try {
                val response = tryBlock()
                callResponse(response, {
                    successBlock(response?.data)
                }, {
                    failBlock(response?.message)
                    if (isChain) view.hideLoading()
                    if (showTips) showToast(response?.message)
                })
            } catch (e: Throwable) {
//                Exceptions.handleException(e)
                failBlock(e.message)
                if (isChain) view.hideLoading()
                if (showTips) showToast(e.message)
            } finally {
                finallyBlock?.let {
                    it()
                }
                if (!isChain && showTips) {
                    view.hideLoading()
                }
            }
        }
    }

    private suspend fun <T> callResponse(
            response: BaseResponse<T>?,
            successBlock: suspend CoroutineScope.() -> Unit,
            failBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            when {
//                response!!.isSuccess -> successBlock()
                else -> failBlock()
            }
        }
    }

    protected fun addJob(job: Job) {
        jobs.add(job)
        job.invokeOnCompletion {
            jobs.remove(job)
        }
    }

    fun cancelJobs() {
        if (jobs.size > 0) {
            for (job in jobs) {
                job.cancel()
            }
            jobs.clear()
        }
    }
}