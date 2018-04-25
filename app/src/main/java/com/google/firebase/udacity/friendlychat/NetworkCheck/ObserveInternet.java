package com.google.firebase.udacity.friendlychat.NetworkCheck;

import java.util.Observable;

public class ObserveInternet extends Observable {
    private static final ObserveInternet ourInstance = new ObserveInternet();

    private ObserveInternet() {
    }

    public static ObserveInternet getInstance() {
        return ourInstance;
    }

    public void notifyActivity(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
