package cn.fover.jk_demo.utils;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


/**
 * RxKotlin的版本
 */
public final class RxBus {
    private final PublishSubject<Object> bus = PublishSubject.create();
    private static RxBus instance;

    private RxBus(){}

    public void send(final Object event) {
        bus.onNext(event);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }

    public static RxBus getInstance() {
        if (instance==null){
            instance=new RxBus();
        }
        return instance;
    }
}
