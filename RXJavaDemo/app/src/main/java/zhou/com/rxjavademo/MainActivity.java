package zhou.com.rxjavademo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhouml on 2016/1/6.
 */
public class MainActivity extends Activity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        just();
//        from();
//        action();
//        scheduce();
//        map();
//        flatmap();
        lift();
    }

    Subscriber<String> sub = new Subscriber<String>() {
        @Override public void onStart() {
            super.onStart();
            Log.d("main", "start");
        }

        @Override public void onCompleted() {
            Log.d("main", "completed");
        }

        @Override public void onError(Throwable e) {

        }

        @Override public void onNext(String s) {
            Log.d("main", "s=" + s);
        }



    };

    private void lift(){


        Observable.just("1").lift(new Observable.Operator<String, String>() {
            @Override public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<String>() {
                    @Override public void onCompleted() {
                        subscriber.onCompleted();
                    }

                    @Override public void onError(Throwable e) {
                        subscriber.onError(e);
                    }

                    @Override public void onNext(String s) {
                        Log.d("main","lift");
                        subscriber.onNext(s);
                    }
                };
            }
        }).subscribe(new Action1<String>() {
            @Override public void call(String s) {
                Log.d("main","start");
            }
        });
    }


    private void flatmap(){
        final List<String[]> list = new ArrayList<>();
        String[] str1 = {"11","12","13"};
        String[] str2 = {"21","22","23"};
        String[] str3 = {"31","32","33"};
        list.add(str1);
        list.add(str2);
        list.add(str3);
        Observable.from(list)
                .flatMap(new Func1<String[], Observable<String>>() {
                    @Override public Observable<String> call(String[] strings) {
                        return Observable.from(strings);
                    }
                }).subscribe(sub);
//        Observable.create(new Observable.OnSubscribe<List<String[]>>() {
//            @Override public void call(Subscriber<? super List<String[]>> subscriber) {
//                subscriber.onNext(list);
//            }
//        }).flatMap(new Func1<List<String[]>, Observable<?>>() {
//            @Override public Observable<String[]> call(List<String[]> strings) {
//                return Observable.from(strings);
//            }
//        }).subscribe(new Action1<Object>() {
//        });
    }

    private void map(){
        Observable.just(1)
                .map(new Func1<Integer, String>() {
                    @Override public String call(Integer integer) {
                        return integer+"";
                    }
                }).subscribe(sub);
    }

    private void scheduce() {
        Observable.
                create(new Observable.OnSubscribe<String>() {
                    @Override public void call(Subscriber<? super String> subscriber) {
                        Log.d("main", "thread name =" + Thread.currentThread().getName());
                        subscriber.onNext("1");

                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        Log.d("main","thread name = "+ Thread.currentThread().getName());
                    }
                });
    }

    private void action() {
        Observable.just("1", null, "3").subscribe(new Action1<String>() {
            @Override public void call(String s) {
                Log.d("main", s);
            }
        }, new Action1<Throwable>() {
            @Override public void call(Throwable throwable) {
                Log.d("main", "error");
            }
        }, new Action0() {
            @Override public void call() {
                Log.d("main", "complete");
            }
        });
    }

    private void from() {
//        String[] ss = {"1","2","3"};
        List<String> ss = new ArrayList<>();
        ss.add("1");
        ss.add("2");
        ss.add("3");
        Observable.from(ss).subscribe(sub);
    }

    private void just() {
        Observable.just("1", "2", "3").subscribe(sub);
    }

    private void base() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("test");
                subscriber.onCompleted();
            }
        });
        observable.subscribe(sub);
    }

}
