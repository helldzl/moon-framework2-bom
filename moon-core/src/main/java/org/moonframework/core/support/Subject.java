package org.moonframework.core.support;

import java.util.ArrayList;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/4/7
 */
public class Subject<T> {

    private ArrayList<Observer> observers;

    public Subject() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(Observer<T> observer) {
        if (observer == null)
            throw new NullPointerException();
        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void deleteObserver(Observer<T> o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        notifyObservers(null);
    }

    public void notifyObservers(T arg) {
        for (Observer observer : observers)
            observer.update(arg);
    }

}
