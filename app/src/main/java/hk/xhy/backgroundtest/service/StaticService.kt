package hk.xhy.backgroundtest.service

import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import hk.xhy.backgroundtest.MainActivity
import hk.xhy.backgroundtest.job.TestJobService
import hk.xhy.backgroundtest.notification.StaticNotification
import hk.xhy.backgroundtest.utils.Constants
import hk.xhy.backgroundtest.utils.Constants.*

class StaticService : Service() {

    companion object {
        const val TAG = "Static"
        const val DEFAULT_MESSAGE_ID = 100
        const val MUST_STATIC_MESSAGE_ID = 101
        const val CYCLE_MESSAGE_ID = 102
    }

    private val mHandler = Handler() {
        if (it.what == CYCLE_MESSAGE_ID) {
            Log.i(TAG, "handle message: cycle mode ")
            startJob()
        } else if (it.what == DEFAULT_MESSAGE_ID) {
            Log.i(TAG, "handle message: DEFAULT_MESSAGE_ID ")
            sendEmptyDelayMessage(DEFAULT_MESSAGE_ID, 3000L)
        } else if (it.what == MUST_STATIC_MESSAGE_ID) {
            Log.i(TAG, "handle message: MUST_STATIC_MESSAGE_ID ")
            sendEmptyDelayMessage(MUST_STATIC_MESSAGE_ID, 3000L)
        }

        return@Handler true
    }

    private fun sendEmptyDelayMessage(what: Int, delay: Long) {
        mHandler.sendEmptyMessageDelayed(what, delay)
    }

    private fun startJob() {
        val builder =
            JobInfo.Builder(Constants.JOB_CYCLE_ID, ComponentName(this, TestJobService::class.java))
        builder.setMinimumLatency(2000)//2s后执行
            .setOverrideDeadline(10000)//最迟10s后执行

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(builder.build())
        if (result < 0) {
            Log.i(TAG, "result is $result Schedule failed ")
        }
    }

    private var mode: Int = 0

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate: ")
        super.onCreate()
        val notification = StaticNotification(this)
        notification.show(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: ${intent}, $flags, $startId")
        mode = intent?.extras?.getInt(EXTRA_MODE_STATIC_SERVICE) ?: 0

        if (CYCLE_MODE == mode) {
            Log.i(TAG, "onStartCommand: CYCLE MODE delay running")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val notification = StaticNotification(this)
                notification.mustShow(this, "CYCLE MODE Static Service")
            }
            mHandler.sendEmptyMessageDelayed(CYCLE_MESSAGE_ID, 5000)
        } else if (MUST_STATIC_MODE == mode) {
            Log.i(TAG, "onStartCommand: MUST_STATIC_MODE")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val notification = StaticNotification(this)
                notification.mustShow(this, "MUST STATIC MODE Static Service")

            }
            mHandler.sendEmptyMessage(MUST_STATIC_MESSAGE_ID)
        } else {

            Log.i(TAG, "onStartCommand: DEFAULT_MESSAGE_ID")
            mHandler.sendEmptyMessage(DEFAULT_MESSAGE_ID)
        }

        return START_STICKY
        // 重新创建service intent = null
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: ")
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}