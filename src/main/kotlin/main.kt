import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
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

    //1. mengirim event complete sepanjang subject melalu method complete
    // ini secara efective mengakhiri subject observable sequence
    publishSubject.onComplete()
    //2. mengirim element lain 5 ke dalam subject. ini tidak akan di munculkan dan di print
    //meskipun, karena subject sudah di terminasi
    publishSubject.onNext(5)
    //3. jangan pesnah lupa menutup subscription dengan dispose ketika sudah selesai ;)
    subscriptionTwo.dispose()
    //4. menciptakan subscription baru, akan memulai subject kembali ke aksi? oh tentu tidak '<'
    // tetapi tetap sadja akan mendapat onComplete event '<'
    val subscriptionThree = publishSubject.subscribeBy(
      onNext = { printWithLabel("3)", it) },
      onComplete = {printWithLabel("3)", "Complete")}
    )

    publishSubject.onNext(6)
  }

  //1
  exampleOf("Behaviour Subject"){
    val subscriptions = CompositeDisposable()
  }
}