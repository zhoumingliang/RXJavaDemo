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
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
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
//        lift();
//        flatmap();
//        compose();
//        defer();
//        merge();
//        filter();
//        take();
//        groupby();
//        zip();
        buffer();
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

    private void buffer(){
        Observable.just(1, 2, 3, 4, 1).distinct().buffer(2).retry()
                .subscribe(new Action1<List<Integer>>() {
                    @Override public void call(List<Integer> integers) {
                        Log.d("main", integers.toString());
                    }
                });

    }

    private void zip(){
        Observable<String> observable = Observable.just("1");
        Observable<String> observable1 = Observable.just("2");


        Observable.zip(observable, observable1, new Func2<String, String, Object>() {
            @Override public Object call(String s, String s2) {

                return s+s2;
            }
        }).subscribe(new Action1<Object>() {
            @Override public void call(Object o) {
                Log.d("main","o="+o);
            }
        });
    }

    private void defer() {

        Observable.defer(new Func0<Observable<String>>() {
            @Override public Observable<String> call() {
                return Observable.just("1");
            }
        }).subscribe(new Action1<String>() {
            @Override public void call(String s) {
                Log.d("main", "call");
            }
        });
    }

    private void merge() {
        Observable<String> observable = Observable.just("1");
        Observable<String> observable1 = Observable.just("2");
        Observable.merge(observable, observable1).subscribe(new Action1<String>() {
            @Override public void call(String s) {
                Log.d("main", s);
            }
        });
    }

    private void compose() {
        Observable.just("1").compose(new Observable.Transformer<String, String>() {
            @Override public Observable<String> call(Observable<String> stringObservable) {
                stringObservable.subscribeOn(Schedulers.computation());
                return stringObservable;
            }
        }).subscribe(new Action1<Object>() {
            @Override public void call(Object o) {
                Log.d("main", "o=" + o.toString());
            }
        });
    }

    private void filter() {
        Integer[] arr = {1, 2, 3};
        Observable.from(arr)
                .filter(new Func1<Integer, Boolean>() {
                    @Override public Boolean call(Integer integer) {
                        return integer == 2;
                    }
                }).subscribe(new Action1<Integer>() {
            @Override public void call(Integer integer) {
                Log.d("main", integer + "");
            }
        });
    }

    private void take() {
        Observable.just("1", "2", "3").
                take(2)
                .subscribe(new Action1<String>() {
                    @Override public void call(String s) {
                        Log.d("main", s);
                    }
                });
    }

    class B{}

    private void groupby() {
        Observable.just("1", "2", "3", "1", "2", new B())
                .groupBy(new Func1<Object, String>() {
                    @Override public String call(Object o) {
                        if (o instanceof String) {
                            return "Str";
                        }
                        return "B";
                    }
                }).subscribe(new Action1<GroupedObservable<String, Object>>() {
            @Override public void call(GroupedObservable<String, Object> stringObjectGroupedObservable) {
                Log.d("main", stringObjectGroupedObservable.getKey());
                stringObjectGroupedObservable.subscribe(new Action1<Object>() {
                    @Override public void call(Object o) {
                        Log.d("main",o.toString());
                    }
                });
            }
        });
    }

    private void lift() {


        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override public void call(Subscriber<? super Integer> subscriber) {
                System.out.println("1>>>>>>:");
                subscriber.onNext(1);
                subscriber.onError(new RuntimeException());
            }
        }).lift(new Observable.Operator<String, Integer>() {
            @Override public Subscriber<? super Integer> call(final Subscriber<? super String> subscriber) {
                return new Subscriber<Integer>() {
                    @Override public void onCompleted() {
                        System.out.println("2>>>>>>:onCompleted");
                        subscriber.onCompleted();
                    }

                    @Override public void onError(Throwable e) {
                        System.out.println("2>>>>>>:onError");
                        subscriber.onError(e);
                    }

                    @Override public void onNext(Integer integer) {
                        System.out.println("2>>>>>>:" + integer);
                        subscriber.onNext(integer + "");
                    }
                };
            }
        }).subscribe(new Subscriber<String>() {
            @Override public void onCompleted() {
                System.out.println("3>>>>>>:onCompleted");
            }

            @Override public void onError(Throwable e) {
                System.out.println("3>>>>>>:onError");
            }

            @Override public void onNext(String s) {
                System.out.println("3>>>>>>:" + s);
            }
        });

//        Observable.just("1").lift(new Observable.Operator<String, String>() {
//            @Override public Subscriber<? super String> call(final Subscriber<? super String> subscriber) {
//                return new Subscriber<String>() {
//                    @Override public void onCompleted() {
//                        subscriber.onCompleted();
//                    }
//
//                    @Override public void onError(Throwable e) {
//                        subscriber.onError(e);
//                    }
//
//                    @Override public void onNext(String s) {
//                        Log.d("main", "lift");
//                        subscriber.onNext(s);
//                    }
//                };
//            }
//        }).subscribe(new Action1<String>() {
//            @Override public void call(String s) {
//                Log.d("main", "start");
//            }
//        });
    }


    private void flatmap() {
        final List<String[]> list = new ArrayList<>();
        String[] str1 = {"11", "12", "13"};
        String[] str2 = {"21", "22", "23"};
        String[] str3 = {"31", "32", "33"};
        list.add(str1);
        list.add(str2);
        list.add(str3);
        Observable.create(
                new Observable.OnSubscribe<Integer>() {
                    @Override public void call(Subscriber<? super Integer> subscriber) {
                        System.out.println("1>>>>>>:");
                        subscriber.onNext(1);
                        subscriber.onCompleted();
//                subscriber.onError(new RuntimeException());
                    }
                }).flatMap(new Func1<Integer, Observable<String>>() {
            @Override public Observable<String> call(Integer integer) {
                System.out.println("2>>>>>>:");
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override public void call(Subscriber<? super String> subscriber) {
                        System.out.println("2>>>>>>:call");
//                        subscriber.onNext("2");
                        subscriber.onError(new RuntimeException());
                    }
                });
            }
        })
                .map(new Func1<String, Integer>() {
                    @Override public Integer call(String s) {
                        return 6;
                    }
                })
                .flatMap(new Func1<Integer, Observable<String>>() {
                    @Override public Observable<String> call(Integer integer) {
                        return Observable.create(new Observable.OnSubscribe<String>() {
                            @Override public void call(Subscriber<? super String> subscriber) {
                                subscriber.onNext("6");
                            }
                        });
                    }
                })

                .subscribe(new Subscriber<String>() {
                    @Override public void onCompleted() {
                        System.out.println("3>>>>>>:onCompleted");
                    }

                    @Override public void onError(Throwable e) {
                        System.out.println("3>>>>>>:onError");
                    }

                    @Override public void onNext(String s) {
                        System.out.println("4>>>>>>:onNext" + s);
                    }
                });

                /*.subscribe(new Action1<Integer>() {

                    @Override public void call(Integer integer) {
                        System.out.println("4>>>>>>:" + integer);
                    }
                });*/
                /*.flatMap(new Func1<List<String[]>, Observable<Integer>>() {
                    @Override public Observable<Integer> call(List<String[]> strings) {
                        Log.d("main", "flatMap");
                        return Observable.just(1);
                    }
                })
                .subscribe(new Action1<Object>() {
                    @Override public void call(Object o) {
                        Log.d("main", "call");
                    }
                });*/
//        Observable.from(list)
//                .flatMapIterable(new Func1<String[], Iterable<Integer>>() {
//                    @Override public Iterable<Integer> call(String[] strings) {
//
//
//                        return null;
//                    }
//                })
//                .flatMap(new Func1<Integer, Observable<?>>() {
//                    @Override public Observable<?> call(Integer integer) {
//                        return null;
//                    }
//                })
//                .toList()
//                .subscribe(new Action1<List<Object>>() {
//                    @Override public void call(List<Object> objects) {
//
//                    }
//                })
//
//
//                .subscribe(sub);

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

    private void map() {
        Observable.just(1)
                .map(new Func1<Integer, String>() {
                    @Override public String call(Integer integer) {
                        return integer + "";
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
                        Log.d("main", "thread name = " + Thread.currentThread().getName());
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
