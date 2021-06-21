package hk.xhy.backgroundtest.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;

import static hk.xhy.backgroundtest.utils.Constants.NF_FOREGROUD_ID;


/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/1
 * Time: 5:08 下午
 * Description: No Description
 */
public class StaticNotification extends BaseNotification<BaseData> {

    public static final String DEFAULT_GROUP_ID        = "DefaultGroup";
    public static final String DEFAULT_GROUP_NAME      = "Default Group";
    public static final String FOREGROUND_CHANNEL_ID   = "DefaultChannel";
    public static final String FOREGROUND_CHANNEL_NAME = "Default Channel";

    public StaticNotification(Context context) {
        super(
                context,
                new BaseData(
                        DEFAULT_GROUP_ID,
                        DEFAULT_GROUP_NAME,
                        FOREGROUND_CHANNEL_ID,
                        FOREGROUND_CHANNEL_NAME
                )
        );
    }

    @Override
    protected String TAG() {
        return "Notification";
    }

    @Override
    public void show() {
        throw new UnsupportedOperationException("u can't call me...");
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    void configureChannel(NotificationChannel channel) {
        //常驻通知永远不限时在锁屏
        channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_SECRET);
        channel.setImportance(NotificationManager.IMPORTANCE_MIN);
    }

    @Override
    void configureNotify(NotificationCompat.Builder mBuilder) {
        mBuilder
                .setOngoing(true)
                .setSmallIcon(getSmallIcon())
                .setContentTitle("常驻通知")
                .setCategory(Notification.CATEGORY_SERVICE);
    }

    public void show(Service service) {
        super.show();
        show(service, null);
    }

    public void show(Service service, String contentTitle) {
        super.show();
        if (!TextUtils.isEmpty(contentTitle)) {
            getBuilder().setContentTitle(contentTitle);
        }
        //Android Q 29 开始 地理位置等 需要设置启动的Foregroud类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //API大于18 弹出可见通知
            LogUtils.wTag(TAG(), "start Foreground EChat Core Service");
            service.startForeground(NF_FOREGROUD_ID, getBuilder().build());
        } else {
            service.startForeground(NF_FOREGROUD_ID, new Notification());
        }

    }

    public void mustShow(Service service) {
        mustShow(service, null);
    }

    public void mustShow(Service service, String title) {
        super.show();
        if (!TextUtils.isEmpty(title)) {
            getBuilder().setContentTitle(title);
        }
        service.startForeground(NF_FOREGROUD_ID, getBuilder().build());
    }

    public void cancel() {
        manager.cancel(NF_FOREGROUD_ID);
    }
}
