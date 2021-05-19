import io.reactivex.rxjava3.subjects.PublishSubject

fun main(){
  exampleOf("PublishSubject"){
    val publishSubject = PublishSubject.create<Int>()
    publishSubject.onNext(0)

    val subscriptionOne = publishSubject.subscribe{ int ->
      println(int)
    }

    publishSubject.onNext(1)
    publishSubject.onNext(2)

    val subscriptionTwo = publishSubject
      .subscribe{ int ->
        printWithLabel("2)", int)
      }

    publishSubject.onNext(3)

    subscriptionOne.dispose()
    publishSubject.onNext(4)

    //1
    publishSubject.onComplete()
  }
}