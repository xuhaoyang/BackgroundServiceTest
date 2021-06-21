package hk.xhy.backgroundtest.notification;

/**
 * User: xuhaoyang
 * mail: xuhaoyang3x@gmail.com
 * Date: 2021/6/4
 * Time: 3:00 下午
 * Description: No Description
 */
public class BaseData {
    private String channelGroupId;
    private String channelGroupName;
    private String channelId;
    private String channelName;

    public BaseData(String channelGroupId, String channelGroupName, String channelId, String channelName) {
        this.channelGroupId   = channelGroupId;
        this.channelGroupName = channelGroupName;
        this.channelId        = channelId;
        this.channelName      = channelName;
    }

    public String getChannelGroupId() {
        return channelGroupId;
    }

    public void setChannelGroupId(String channelGroupId) {
        this.channelGroupId = channelGroupId;
    }

    public String getChannelGroupName() {
        return channelGroupName;
    }

    public void setChannelGroupName(String channelGroupName) {
        this.channelGroupName = channelGroupName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
