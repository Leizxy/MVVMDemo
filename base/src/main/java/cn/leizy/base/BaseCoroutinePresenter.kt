package cn.leizy.base

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import com.google.gson.Gson
import com.zhong.commonbase.base.BaseResponse
import com.zhong.commonbase.http.Exceptions
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * @author Created by wulei
 * @date 2020/9/29, 029
 * @description
 */
abstract class BaseCoroutinePresenter<V : IView> : IPresenter<V> {
    /*    override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main + Job()*/
    private val presenterScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main + Job())
    }

    private var view: V? = null
    private var weakReference: WeakReference<V>? = null
    private val jobs: MutableList<Job> = mutableListOf()

    protected fun showToast(str: String?) {
        view?.showToast(str)
    }

    @SuppressLint("SupportAnnotationUsage")
    @StringRes
    protected fun showToast(idRes: Int) {
        view?.showToast(idRes)
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
            view?.showLoading()
        }
        launchMain {
            tryCatch(tryBlock, successBlock, failBlock, finallyBlock, isChain, showTips)
        }
    }

    fun launchMain(block: suspend CoroutineScope.() -> Unit) =
            addJob(presenterScope.launch(Dispatchers.Main, block = block))


    fun launchOnIO(block: suspend CoroutineScope.() -> Unit) =
            addJob(presenterScope.launch(Dispatchers.IO) {
                //            Log.i(this@BaseCoroutinePresenter.javaClass.simpleName, "launchOnUI thread: ${Thread.currentThread().name}")
                //            Log.i(this@BaseCoroutinePresenter.javaClass.simpleName, "launchOnUI thread: ${Thread.currentThread().id}")
                block()
            })

    fun launchOnDefault(block: suspend CoroutineScope.() -> Unit) =
            addJob(presenterScope.launch(Dispatchers.Default) { block() })

    private fun addJob(job: Job) {
        jobs.add(job)
        job.invokeOnCompletion {
            Log.i("BaseCoroutinePresenter", "addJob: remove ${job.key}")
            jobs.remove(job)
        }
    }

    open fun blocking(block: suspend CoroutineScope.() -> Unit) = runBlocking(Dispatchers.IO) { block() }

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
                    if (isChain) view?.hideLoading()
                    if (showTips) showToast(response?.message)
                })
            } catch (e: Throwable) {
                Exceptions.handleException(e)
                failBlock(e.message)
                if (isChain) view?.hideLoading()
                if (showTips) showToast(e.message)
            } finally {
                finallyBlock?.let {
                    it()
                }
                if (!isChain && showTips) {
                    view?.hideLoading()
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
                response!!.isSuccess -> successBlock()
                else -> failBlock()
            }
        }
    }

    override fun attachView(view: V) {
        weakReference = WeakReference(view)
        try {
            this.view = Proxy.newProxyInstance(
                    view::class.java.classLoader,
                    view::class.java.interfaces,
                    MvpViewHandler(weakReference?.get()!!)
            ) as V
        } catch (e: Exception) {
            this.view = Proxy.newProxyInstance(
                    view::class.java.classLoader,
                    view::class.java.superclass.interfaces,
                    MvpViewHandler(weakReference?.get()!!)
            ) as V
        }
        Log.i("BaseCoroutinePresenter", "attachView: ${view}")
    }

    @ExperimentalCoroutinesApi
    override fun detachView() {
        Log.i("BaseCoroutinePresenter", "detachView: " + jobs.size)
        if (jobs.size > 0) {
            for (job in jobs) {
                job.cancel()
            }
            jobs.clear()
        }
        Log.i("BaseCoroutinePresenter", "detachView: " + jobs.size)
        if (isViewAttached) {
            weakReference!!.clear()
            weakReference = null
        }
    }

    protected fun getPostBody(params: MutableMap<String, Any>?): RequestBody {
        return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), Gson().toJson(params).toString())
    }

    override fun isViewAttached(): Boolean {
        return weakReference != null && weakReference?.get() != null
    }

    override fun getView(): V {
        return view!!
    }

    private inner class MvpViewHandler(private val mvpView: V) : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            return if (isViewAttached) {
                if (args != null)
                    method.invoke(mvpView, *args)
                else method.invoke(mvpView)
            } else null
        }
    }
}