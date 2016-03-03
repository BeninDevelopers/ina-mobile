package org.benindevelopers.ina.utils;

/**
 * Created by Joane SETANGNI on 29/02/2016.
 */
public class GCMRegisterEvent {
    private final boolean isRegistered;
    private final String gcmId;

    public GCMRegisterEvent(boolean isRegistered, String gcmId) {
        this.isRegistered = isRegistered;
        this.gcmId = gcmId;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public String getGcmId() {
        return gcmId;
    }
}
