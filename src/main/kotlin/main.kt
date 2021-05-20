import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.RuntimeException

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

  //1. membuatt contoh baru behavour subject
  exampleOf("Behaviour Subject"){
    //2. menciptakan composite disposable yang akan di gunakan nanti
    val subscriptions = CompositeDisposable()
    //3. menciptakan behaviorSubject baru menggunakan static factory method "create default"
    // yang mana mengambil nilai awak yang akan di tampilkan dengan segera
    val behaviorSubject = BehaviorSubject.createDefault("Initial Value")
    // catatn BehaviorSubject dapat juga di initialisasi tanpa sebua Initial value.
    // untuk menggunakannya dapat menggunakan static factory method :3

    //ini menciptakan subscription pada subject, tetapi tidak ada elemetn yang dimasukkan
    // jadi hanya initial value yang akan di tampilkan ^_^
    val subscriptionOne = behaviorSubject.subscribeBy(
      onNext = { printWithLabel("1)", it)},
      onError = { printWithLabel("1)", it)}
    )

    //memsukkan nilai baru kepada subscription
    //X akan di tampilkan karena akhirnya ada nilai yang telah di masukkan
    behaviorSubject.onNext("X")

    //1. menambahkan runtime error ke dalam subject
    behaviorSubject.onError(RuntimeException("Error"))
    //2. menciptakan subscription baru dalam subject
    subscriptions.add(behaviorSubject.subscribeBy(
      onNext = {printWithLabel("2)", it)},
      onError = { printWithLabel("2)", it) }
    ))

    //ketika di print akan memunculkan dua nilai runtime error.
    //ini adalah keuntungan dari menggunakan BehaviurSubject. yaitu dapat mengakse nilai terakhir yang imperatif
    //
  }

  exampleOf("BehaviorSubject State"){
    val subscription = CompositeDisposable()
    val behaviorSubject = BehaviorSubject.createDefault(0)

    println(behaviorSubject.value)

    //1. Subsccribe ke behaviorSubject dan menambahkan disposable sehingga dapat di unsubscribe nantinya
    subscription.add(behaviorSubject.subscribeBy{
      printWithLabel("1)", it)
    })

    //2. call onNext untuk memasukkan nilai
    behaviorSubject.onNext(1)

    //3. print nilai apapun yang di masukkan kedalam behaviorSubject
    println(behaviorSubject.value)

    //4. dipose subscription
    subscription.dispose()
    //behavior subject dapat membantu menjebatani antara dunia rx dan dunia non rx

  }
}