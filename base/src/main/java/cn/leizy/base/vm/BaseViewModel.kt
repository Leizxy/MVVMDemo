package cn.leizy.base.vm

import androidx.lifecycle.AndroidViewModel
import cn.leizy.base.App
import cn.leizy.base.m.BaseModel
import kotlinx.coroutines.Job
import java.lang.reflect.ParameterizedType

/**
 * @author Created by wulei
 * @date 2020/11/16, 016
 * @description
 */
open class BaseViewModel<M : BaseModel> : AndroidViewModel(App.getInstance()) {

    private val jobs: MutableList<Job> = mutableListOf()

    protected val model: M by lazy {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val clazz: Class<M> = pt.actualTypeArguments[0] as Class<M>
        clazz.newInstance()
    }



/*    private fun getM(): M {
        val pt = this.javaClass.genericSuperclass as ParameterizedType
        val clazz: Class<M> = pt.actualTypeArguments[0] as Class<M>
        return clazz.newInstance()
    }*/

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