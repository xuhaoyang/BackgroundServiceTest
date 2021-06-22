package hk.xhy.backgroundtest.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import hk.xhy.backgroundtest.notification.StaticNotification

class CancelService : Service() {
    companion object {
        const val TAG = "Cancel"
    }

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1
        ) {
            val notification = StaticNotification(this)
            notification.show(this)
            Thread {
                SystemClock.sleep(1000)
                //取消前台
                stopForeground(true)
                //移除弹出的通知
                notification.cancel()
                //结束自己
                stopSelf()
            }.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy: ")
        super.onDestroy()
    }
}