# rxjava 入门
本实例中囊括了rxjava操作符（map、just、create、filter、defer）、rxjava线程切换调度、rxjava生命周期绑定、rxbus、以及retrofit与rxjava交互以及retryWhen操作符、dagger2等实例。

#### 简单介绍下其中一些操作符：

create：create操作符用法和源码分析create操作符的基本使用顾名思义,Create操作符是用来创建一个Observable的。

filter：对源Observable产生的结果按照指定条件进行过滤，只有满足条件的结果才会提交给订阅者

map：是把发射对象转成另外一个对象发射出去

flatMap 是把发射对象转成另外一个Observable,进而把这个Observable发射的对象发射出去

just：生成的Observable就是传进的数据，而from则是把列表数据展开，实例如下：

   
     Integer[] items2 = { 0, 1, 2, 3, 4, 5 };  
  
     Observable.from(items2).subscribe(new Action1<Integer>() {  
       @Override  
       public void call(Intege integer) {  
  
       }  
     },null,null);  
  
     Observable.just(items2).subscribe(new Action1<Integer[]>() {  
       @Override  
       public void call(Integer[] integer) {  
  
      }  
     },null,null);  
     
     //just 传入多个对象
     Observable.just(mStudent1, mStudent2, mStudent3).compose(this.<Student>bindToLifecycle())
                //使用map进行转换，参数1：转换前的类型，参数2：转换后的类型
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student i) {
                        String name = i.name;//获取Student对象中的name
                        return name;//返回name
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(TAG, "name " + s);
                    }
                });
  

from：它可以接收一个集合，然后将其中的子元素挨个拆出来，常用于list数组

cancatMap：cancatMap操作符与flatMap操作符类似，都是把Observable产生的结果转换成多个Observable，然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
           与flatMap操作符不同的是，concatMap操作符在处理产生的Observable时，采用的是“连接(concat)”的方式，而不是“合并(merge)”的方式，这就能保证产生结果的顺序性，也就是说提交给订阅者的结果是按照顺序提交的，不会存在交叉的情况。
           
defer： 直到有订阅，才会创建Observable，具有延时的效果。缺点：每次订阅都会创建一个新的Observable对象。而相较之create()操作符则为每一个订阅者都使用同一个函数，所以，后者效率更高。一如既往地，如果有必要可以亲测性能或者尝试优化。        

# 简介
RxJava 在 GitHub 主页上的自我介绍是 "a library for composing asynchronous and event-based programs using observable sequences for the Java VM"（一个在 Java VM 上使用可观测的序列来组成异步的、基于事件的程序的库）。这就是 RxJava ，概括得非常精准。
相较于Handler、AsyncTask而言 会显得更加简洁易懂，而且它具备线程切换调度，可以有效减少handler和thread的使用。

RxJava 的观察者模式

RxJava 有四个基本概念：Observable (可观察者，即被观察者)、 Observer (观察者)、 subscribe (订阅)、事件。Observable 和 Observer 通过 subscribe() 方法实现订阅关系，从而 Observable 可以在需要的时候发出事件来通知 Observer。

与传统观察者模式不同， RxJava 的事件回调方法除了普通事件 onNext() （相当于 onClick() / onEvent()）之外，还定义了两个特殊的事件：onCompleted() 和 onError()。

onCompleted(): 事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。RxJava 规定，当不会再有新的 onNext() 发出时，需要触发 onCompleted() 方法作为标志。
onError(): 事件队列异常。在事件处理过程中出异常时，onError() 会被触发，同时队列自动终止，不允许再有事件发出。
在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只有一个，并且是事件序列中的最后一个。需要注意的是，onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。

RxJava 的观察者模式大致如下图：

![image](pic1.jpg)

# 简单实例
   

        例子1
        for(Student student : mList){
            for(Student student2 : mList2){
                if(student.name.equals(student2.name)){
                    Log.i(TAG,"testFor 1 "+student.name);
                }
            }
        }
        列子2
        Observable.from(mList)
                .flatMap(new Func1<Student, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(Student student) {
                        student1 = student;
                        return Observable.from(mList2);
                    }
                })
                .filter(new Func1<Student, Boolean>() {
                    @Override
                    public Boolean call(Student student) {
                        return student1.name.equals(student.name);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                         Log.i(TAG,"testFor 2 "+student.name);
                    }
                });
             