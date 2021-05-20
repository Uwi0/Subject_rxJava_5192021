import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.AsyncSubject
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
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

    exampleOf("replaySubject"){

      val subscriptions = CompositeDisposable()

      //1. mencipatakan replaySubject dengan array buffer 2.
      //replay subject di inisialisasi dengan method static 'createWithSize'.
      val replaySubject = ReplaySubject.createWithSize<String>(2)

      //2. menambhkan tiga element kedalam subject
      replaySubject.onNext("1")

      replaySubject.onNext("2")

      replaySubject.onNext("3")

      //3. menciptakan 2 subscription ke subject
      subscriptions.add(replaySubject.subscribeBy(
        onNext = {printWithLabel("1)", it)},
        onError = {printWithLabel("1)", it)}
      ))

      subscriptions.add(replaySubject.subscribeBy(
        onNext = {printWithLabel("2)", it)},
        onError = {printWithLabel("2)", it)}
      ))
    /*
    * dua element terakhir di masukkan ke dalam subcription. 1 tidak pernah di tampilkan
    * karena 2 dan 3 telah di masukkan kedlam subcription sedangkan ukuran dari buffer hanya ada 2
    * jadi 1 di hilangkan
    * */

      replaySubject.onNext("4")
      replaySubject.onError(RuntimeException("error"))
      /*
      * replay subject di akhiri dengan error, yang mana itu akan menampilkan kembali
      * ke subscriber baru seperti yang telah di lakukan subject. tetepi buffer masih
      * berkeliaran, jadi itu akan tetap di replay selama itu belum di hentikan
      * */

      subscriptions.add(replaySubject.subscribeBy(
        onNext = {printWithLabel("3)", it)},
        onError = {printWithLabel("3)", it)}
      ))

      /*
      * 2 subscription pertama akan menerima elemant secara normal
      * karena mereka sudah subscribe keitka elemant baru ditambahkan,
      * sementara itu subscriber baru yang ke tiga hanya akan mendapatkan 2 element terbaru
      * */
    }

    exampleOf("AsyncSubject"){
      val subscriptions = CompositeDisposable()

      //1. membuat asyncSubject yang menampung nilai int
      val asyncSubject = AsyncSubject.create<Int>()

      //2. Subscribe ke subject, print kedua hasil baik onComplete atau onNext
      subscriptions.add(asyncSubject.subscribeBy(
        onNext = {printWithLabel("1)", it)},
        onComplete = {printWithLabel("1)", "complete")}
      ))

      //3. mengirim 3 nilai ke dalam subject
      asyncSubject.onNext(0)
      asyncSubject.onNext(1)
      asyncSubject.onNext(2)

      //4. subject complete
      asyncSubject.onComplete()
      /*karena 2 adalah element terakhir maka hanya 2 yang di tampilkan*/
      subscriptions.dispose()
    }
  }

  exampleOf("RxRelay"){
    val subscriptions = CompositeDisposable()

    val publishRelay = PublishRelay.create<Int>()

    subscriptions.add(publishRelay.subscribeBy(
      onNext = { printWithLabel("1)", it) }
    ))

    publishRelay.accept(1)
    publishRelay.accept(2)
    publishRelay.accept(3)
  }

  /*Key Point
  * * Subject adalah observable yang juga observer
  * * nilai dapat di kirim ke subject melalui onNext, onComplete, dan on Error
  * * "PublishSubject" di gunakan ketika hanya menginginkan untuk menerima nilai
  *   yang muncul setelah melakukan subscribe
  * * "BehaviorSubject" akan menyampaikan event terakhir yang muncul ketika melakukan subscribe
  *   termasuk niali opsional
  * * "ReplaySubject" nilai yang di konfigura akan di simpan di buffer yang akan di tampilkan ke
  *   subscriber baru. untuk menggunakan in harus berhati hati dalam menentukan besaran buffer
  * * "AsyncSubject" hanya mensubscribe nilai terakhir yang detirma seblum onCOmplete di jalankan
  * * ""RxRelay" di gunakan untuk mensubscribe nilai yang akan rerus muncul, sehingga menghindarkan
  *   dari onComplete dan onError, agar data terus berjalan*/
}