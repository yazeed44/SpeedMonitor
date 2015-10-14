package net.yazeed44.speedmonitor;

import io.realm.RealmObject;

/**
 * Created by Yazeed Ahmed Almuqwishi on 10/14/15.
 */
public class Monitor extends RealmObject {

    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
