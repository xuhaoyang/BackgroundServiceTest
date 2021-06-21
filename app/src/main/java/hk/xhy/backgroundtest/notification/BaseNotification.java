package hk.xhy.backgroundtest.notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Collections;
import java.util.List;

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/1
 * Time: 4:39 下午
 * Description: No Description
 */
@Keep
public abstract class BaseNotification<T extends BaseData> extends ContextWrapper {

    protected NotificationManager        manager;
    private   NotificationCompat.Builder mBuilder;
    private   T                          data;


    public BaseNotification(Context context, T data) {
        super(context);
        this.data = data;
        manager   = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        initGroup();
        initChannel();
    }

    protected abstract String TAG();

    protected String currentGroupId() {
        return data.getChannelGroupId();
    }

    protected String currentGroupName() {
        return data.getChannelGroupName();
    }

    protected String currentChannelId() {
        return data.getChannelId();
    }

    protected String currentChannelName() {
        return data.getChannelName();
    }

    public T getData() {
        return data;
    }

    public void show() {
        initBuilder(this);
    }

    private void initGroup() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                NotificationChannelGroup group = getNotificationChannelGroup(currentGroupId());
                if (group == null) {
                    group = new NotificationChannelGroup(currentGroupId(), currentGroupName());
                    manager.createNotificationChannelGroup(group);
                }
            } catch (Exception e) {
                Log.e(TAG(), "initGroup: ", e);
            }
        }
    }


    private void initChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                NotificationChannel channel = manager.getNotificationChannel(currentChannelId());
                if (channel == null) {
                    channel = new NotificationChannel(
                            currentChannelId(),
                            currentChannelName(),
                            NotificationManager.IMPORTANCE_DEFAULT
                    );
                    channel.setGroup(currentGroupId());
                    configureChannel(channel);
                    manager.createNotificationChannel(channel);
                }
            } catch (Exception e) {
                Log.e(TAG(), "initGroup: ", e);
            }
        }
    }

    private void initBuilder(Context context) {
        mBuilder = new NotificationCompat.Builder(context, currentChannelId());
        configureNotify(mBuilder);
    }

    abstract void configureNotify(NotificationCompat.Builder mBuilder);

    @RequiresApi(api = Build.VERSION_CODES.O)
    abstract void configureChannel(NotificationChannel channel);


    NotificationCompat.Builder getBuilder() {
        return mBuilder;
    }

    @Nullable
    public NotificationChannelGroup getNotificationChannelGroup(@NonNull String channelGroupId) {
        if (Build.VERSION.SDK_INT >= 28) {
            return manager.getNotificationChannelGroup(channelGroupId);
        } else if (Build.VERSION.SDK_INT >= 26) {
            // find the group in list by its ID
            for (NotificationChannelGroup group : getNotificationChannelGroups()) {
                if (group.getId().equals(channelGroupId)) return group;
            }
            // requested group doesn't exist
            return null;
        } else {
            return null;
        }
    }

    /**
     * Returns all notification channel groups belonging to the calling app
     * or an empty list on older SDKs which doesn't support Notification Channels.
     */
    @NonNull
    public List<NotificationChannelGroup> getNotificationChannelGroups() {
        if (Build.VERSION.SDK_INT >= 26) {
            return manager.getNotificationChannelGroups();
        }
        return Collections.emptyList();
    }

    protected int getSmallIcon() {
        //设置 nofication 的图标 直接读取小米推送配置的图标
        int icon = getResources().getIdentifier("mipush_small_notification", "drawable", getPackageName());
        if (icon == 0) {
            icon = getApplicationInfo().icon;
        }

        return icon;
    }

    public static void cancel(Context context, int notificationId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
