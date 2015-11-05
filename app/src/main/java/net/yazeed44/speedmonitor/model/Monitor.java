package net.yazeed44.speedmonitor.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yazeed Ahmed Almuqwishi on 10/14/15.
 */
public class Monitor extends RealmObject {

    @PrimaryKey
    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
