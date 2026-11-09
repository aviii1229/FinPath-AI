package com.finpath.modules.notification.channel;

public class ChannelDeliveryResult {
    private String channel; // IN_APP, EMAIL, PUSH
    private String status; // DELIVERED, SUPPRESSED_QUIET_HOURS, DISABLED, NO_CONSENT, FAILED
    private String detail;

    public ChannelDeliveryResult() {}

    public ChannelDeliveryResult(String channel, String status, String detail) {
        this.channel = channel;
        this.status = status;
        this.detail = detail;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
