package org.xottys.transfermessage;

import java.util.Observable;

/**
 * 被观察者，所有需要监控的变量都要在setter中判断，并在值变化时设置setChanged()
 */

public class MyObservable extends Observable {

    private String data = "";

    public String getData() {
        return data;
    }

    public void setData(String s) {
        if (!this.data.equals(s)) {
            this.data = s;
            setChanged();
        }

        //只有在setChanged()被调用后，notifyObservers()才会去调用update()，否则什么都不干
        notifyObservers(data);

    }
}

