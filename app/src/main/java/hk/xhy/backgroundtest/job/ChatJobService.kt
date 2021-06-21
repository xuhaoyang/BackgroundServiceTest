package hk.xhy.backgroundtest.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.ThreadUtils
import hk.xhy.backgroundtest.service.ChatService
import hk.xhy.backgroundtest.utils.Constants
import hk.xhy.backgroundtest.utils.Constants.CYCLE_MODE
import hk.xhy.backgroundtest.utils.Constants.EXTRA_MODE_STATIC_SERVICE

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/16
 * Time: 2:51 下午
 * Description: No Description
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
open class ChatJobService : JobService() {
    companion object {
        const val TAG = "ChatJob"
        const val MESSAGE_ID = 100
        const val DELEY_MESSAGE_ID = 101
        const val CYCLE_MESSAGE_ID = 102
    }

    private val mJobHandler = Handler() {
        Log.i(TAG, "handle message: ")
        // 当onStartJob返回true的时候，我们必须在合适时机手动调用jobFinished方法
        // 否则该应用中的其他job将不会被执行
        if (it.what == DELEY_MESSAGE_ID) {
            Log.i(TAG, "handle message: delay message")
        } else if (it.what == CYCLE_MESSAGE_ID) {
            Log.i(TAG, "handle message: JOB_CYCLE_ID start chat service")
            startChatService()
        }
        jobFinished(it?.obj as JobParameters, false)
        // 第一个参数JobParameter来自于onStartJob(JobParameters params)中的params，
        // 这也说明了如果我们想要在onStartJob中执行异步操作，必须要保存下来这个JobParameter。
        // 第二个参数 true 是用于重复运行
        return@Handler true
    }

    private fun toString(params: JobParameters?): String {
        return "extras - >${params?.extras}"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStartJob:  job id = ${params?.jobId} ${toString(params)}")

        if (params?.jobId == Constants.JOB_APP_START_ID) {
            val time = params.extras.getLong("time")
            Log.i(TAG, "onStartJob: App Init start time: ${System.currentTimeMillis() - time}ms")
            ThreadUtils.executeByCached(object : ThreadUtils.SimpleTask<String>() {
                override fun doInBackground(): String {
                    Log.i(TAG, "background start thread")
                    val start = SystemClock.elapsedRealtime()
                    while (SystemClock.elapsedRealtime() - start < 11 * 60 * 1000) {
                        Log.i(TAG, "background time: ${SystemClock.elapsedRealtime() - start}")
                        Thread.sleep(1000)
                    }

                    return ""
                }

                override fun onSuccess(result: String?) {
                    Log.i(TAG, "background onSuccess: ok")
                    jobFinished(params, false)
                }

            })
        } else if (params?.jobId == Constants.JOB_CYCLE_ID) {
            Log.i(TAG, "JOB_CYCLE_ID onStartJob: Handler delay 6s start chat service")

            if (ServiceUtils.isServiceRunning(ChatService::class.java)) {
                Log.i(TAG, "JOB_CYCLE_ID onStartJob: Chat Service is running and stop")
                stopService(Intent(this, ChatService::class.java))
            }
            mJobHandler.sendMessageDelayed(
                Message.obtain(mJobHandler, CYCLE_MESSAGE_ID, params),
                6000
            )
        } else if (params?.jobId == Constants.JOB_PERIOD_ID) {
            Log.i(TAG, "JOB_PERIOD_ID onStartJob: period")
            return false
        } else {
            mJobHandler.sendMessage(Message.obtain(mJobHandler, MESSAGE_ID, params))
        }
        // 返回false说明job已经完成 不是个耗时的任务
        // 返回true说明job在异步执行 需要手动调用jobFinished告诉系统job完成
        // 这里我们返回了true,因为我们要做耗时操作。
        // 返回true意味着耗时操作花费的事件比onStartJob执行的事件更长
        // 并且意味着我们会手动的调用jobFinished方法

        //执行耗时操作 需要将JobParameters保存下来
        //在执行耗时操作完毕 调用jobFinished
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.i(TAG, "onStopJob: ")
        mJobHandler.removeMessages(MESSAGE_ID)
        // 当系统收到一个cancel job的请求时，并且这个job仍然在执行(onStartJob返回true)，系统就会调用onStopJob方法。
        // 但不管是否调用onStopJob，系统只要收到取消请求，都会取消该job
        // true 需要重试
        // false 不再重试 丢弃job
        return false
    }


    private fun startChatService() {
        //启动前台服务然后启动job
        val intent = Intent(this, ChatService::class.java)
        intent.putExtra(EXTRA_MODE_STATIC_SERVICE, CYCLE_MODE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        Log.i(TAG, "onStartJob: start chat service")
    }

    override fun onDestroy() {
        super.onDestroy()
        mJobHandler.removeCallbacksAndMessages(null)
        Log.i(TAG, "onDestroy: ")
    }
}