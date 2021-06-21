package hk.xhy.backgroundtest

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import hk.xhy.backgroundtest.job.TestJobService
import hk.xhy.backgroundtest.service.StaticService
import hk.xhy.backgroundtest.utils.Constants

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/16
 * Time: 3:54 下午
 * Description: No Description
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        startJob()

        //startService(Intent(this, StaticService::class.java))
    }

    private fun startJob() {
        val builder = JobInfo.Builder(
            Constants.JOB_APP_START_ID,
            ComponentName(this, TestJobService::class.java)
        )
        builder.setMinimumLatency(50)
            .setOverrideDeadline(2000)
            .setExtras(PersistableBundle().apply {
                putLong("time", System.currentTimeMillis())
            })

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(builder.build())
        if (result < 0) {
            Log.i(MainActivity.TAG, "result is $result Schedule failed ")
        }
    }
}