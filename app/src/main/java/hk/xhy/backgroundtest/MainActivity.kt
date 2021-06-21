package hk.xhy.backgroundtest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ServiceUtils
import com.blankj.utilcode.util.SnackbarUtils
import hk.xhy.backgroundtest.job.ChatJobService
import hk.xhy.backgroundtest.job.TestJobService
import hk.xhy.backgroundtest.service.ChatService
import hk.xhy.backgroundtest.service.StaticService
import hk.xhy.backgroundtest.utils.Constants.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "Main"
    }

    private val mJobScheduler: JobScheduler by lazy {
        getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartJob.setOnClickListener {
            startJob(it)
        }
        btnStartPeriodJob.setOnClickListener {
            startPeriodJob(it)
        }

        btnStartCycleJob.setOnClickListener {
            startCycleJob(it, TestJobService::class.java)
        }

        btnStopCycleJob.setOnClickListener {
            stopCycleJob(it)
        }


        btnStartService.setOnClickListener {
            startService(Intent(this, StaticService::class.java))
        }

        btnStartStaticService.setOnClickListener {
            val intent = Intent(this, StaticService::class.java)
            intent.putExtra(EXTRA_MODE_STATIC_SERVICE, MUST_STATIC_MODE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        btnStopStaticService.setOnClickListener {
            stopService(Intent(this, StaticService::class.java))
        }

        btnStartChatService.setOnClickListener {
            val intent = Intent(this, ChatService::class.java)
            intent.putExtra(EXTRA_MODE_STATIC_SERVICE, MUST_STATIC_MODE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        btnStopChatService.setOnClickListener {
            if (ServiceUtils.isServiceRunning(ChatService::class.java)) {
                SnackbarUtils.with(root).setMessage("Chat Service is running").show()
            }
            stopService(Intent(this, ChatService::class.java))
        }

        btnStartCycleChatJob.setOnClickListener {
            startCycleJob(it, ChatJobService::class.java)
        }

        btnStopCycleChatJob.setOnClickListener {
            stopCycleJob(it)
        }

        btnStartTimingChatJob.setOnClickListener {
            startPeriodJob(it)
        }
    }


    private fun startPeriodJob(it: View?) {
        val builder =
            JobInfo.Builder(JOB_PERIOD_ID, ComponentName(this, ChatJobService::class.java))

        builder.setPeriodic(1 * 60 * 1000L)
            .setBackoffCriteria(100, JobInfo.BACKOFF_POLICY_LINEAR)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        val result = mJobScheduler.schedule(builder.build())
        if (result < 0) {
            Log.i(TAG, "result is $result Schedule failed ")
        }
    }

    private fun <T> startCycleJob(it: View?, clazz: Class<T>) {
        val builder = JobInfo.Builder(JOB_CYCLE_ID, ComponentName(this, clazz))
        builder.setMinimumLatency(2000)//2s后执行
            .setOverrideDeadline(8000)//最迟10s后执行

        val result = mJobScheduler.schedule(builder.build())
        if (result < 0) {
            Log.i(TAG, "result is $result Schedule failed ")
        }
    }

    private fun stopCycleJob(it: View?) {
        //check service
        if (ServiceUtils.isServiceRunning(StaticService::class.java)) {
            stopService(Intent(this, StaticService::class.java))
            Log.i(TAG, "stopCycleJob: stop Static Service")
        }

        if (ServiceUtils.isServiceRunning(ChatService::class.java)) {
            stopService(Intent(this, ChatService::class.java))
            Log.i(TAG, "stopCycleJob: stop Static Service")
        }
        //check job
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobScheduler.getPendingJob(JOB_CYCLE_ID)
        }
        mJobScheduler.cancel(JOB_CYCLE_ID)
    }

    private fun startJob(it: View?) {
        val builder = JobInfo.Builder(JOB_TEST_ID, ComponentName(this, TestJobService::class.java))
        builder.setMinimumLatency(2000)//2s后执行
            .setOverrideDeadline(10000)//最迟10s后执行

        val result = mJobScheduler.schedule(builder.build())
        if (result < 0) {
            Log.i(TAG, "result is $result Schedule failed ")
        }
    }
}