package org.benindevelopers.ina.utils;

/**
 * Created by Joane SETANGNI on 29/02/2016.
 */
public class PhoneRegisterEvent {
    private final boolean isRegistered;

    public PhoneRegisterEvent(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

}
