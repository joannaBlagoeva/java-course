## Многонишково програмиране с Java

_11.12.2019_

---

#### Предната лекция говорихме за:

@ul

- S.O.L.I.D. дизайн принципите
- Design Patterns

@ulend

---

#### Днес ще разгледаме:

@ul

- Паралелно и конкурентно програмиране
- Нишки: създаване, стартиране, синхронизиране
- Атомарни типове данни
- Конкурентни колекции

@ulend

---

#### Concurrency: кратка история

@ul

- древното минало: преди появата на операционните системи
- еднозадачни операционни системи
- многозадачни операционни системи. Процеси. Комуникация между процеси
    - утилизиране на ресурсите
    - fairness
    - удобство
- едно- и многопроцесорни системи, hyper-threading (2002), многоядрени процесори (2005)

@ulend

---

![Multi-Core CPUs](images/10.1-multi-core-cpus.png)

---

#### Какво е нишка?

Отделен път на изпълнение в програма, който се изпълнява конкурентно.

![Multi-threading](images/10.2-multithreading.png)

---

#### Последователно vs конкурентно изпълнение


|               | Процеси     | Нишки             |
|:------------- |:------------|:----------------- |
| Стартиране    | Бавно       | Относително бързо |
| Изолация      | Да          | Не                |
| Комуникация   | Бавна       | Бърза             |

---

#### Ползи от многонишковото изпълнение

- Пълноценна употреба на наличните ресурси
- Подобрено потребителско изживяване (more responsive UIs)
- По-проста архитектура
    - сложен, асинхронен workflow → няколко прости, последователни workflows, изпълнявани в отделни нишки

---

#### Рискове и предизвикателства

@ul

- Thread safety
- Сложност на кода
- Източник на грешки
- *Liveness* рискове
- *Performance* рискове
- многонишкови фреймуърци

@ulend

---

#### Thread Safety

@ul

- Основна концепция в многонишковото програмиране
- Клас е thread-safe, ако работи коректно при конкурентен достъп от множество нишки
- Касае управлението на достъпа до състоянието на обектите
    - по-конкретно на shared & mutable състоянието

@ulend

---

#### Thread Safety

@ul

- thread safety цели защитата на данните от неконтролиран конкурентен достъп
- thread-safe класовете енкапсулират евентуалната необходима синхронизация, така че ползвателите им да не се грижат за нея

@ulend

---

#### Thread Safety

@ul

- Когато повече от една нишка достъпва състоянието на обект и поне една нишка може да го промени, всички тези нишки трябва да синхронизират достъпа си до това състояние
- Енкапсулацията и data hiding помагат за thread safety

@ul

---

#### Атомарност (Atomicity)

- Атомарни vs. съставни (compound) операции
- *race conditions*

---

#### Примери за race condition

- race condition тип *check-then-act*
    - lazy инициализация
- race condition тип *read-modify-write*
    - инкрементиране на брояч

---

#### Примери за race condition

```java
// check-then-act race condition
if (ref == null) {
    ref = new Something();
}
return ref;

// read-modify-write race condition
i++; // this is equivalent to i = i + 1,
     // which in fact consists of three operations
```

---

#### Атомарни типове данни

@ul

- Класове в `java.util.concurrent.atomic` пакета
- Предоставят атомарни имплементации на примитиви, масиви от примитиви и абстракция за атомарна референция
- `AtomicBoolean`, `AtomicInteger`, `AtomicLong`, `AtomicIntegerArray`, `AtomicLongArray`, `AtomicReference<ActualType>`

@ulend

---

#### Атомарни типове данни

@ul

- Предоставят възможност за атомарни съставни операции
- Използват специални CPU инструкции (“compare-and-swap”, CAS), които позволяват избягването на синхронизация чрез ключалки

@ulend

---

#### Атомарни операции

```java
// Всички имплементации имат методи get() и set() за достъп до
// съхраняваната променлива.
AtomicInteger atomicInt = new AtomicInteger();
atomicInt.set(2019);
System.out.println(atomicInt.get()); // 2019
```

---

#### Примери за атомарни операции

```java
// thread-safe вариант на ++i (i=i+1)
System.out.println(atomicInt.incrementAndGet()); // 2020

// thread-safe вариант на i += x (i=i+x)
System.out.println(atomicInt.addAndGet(5)); // 2025

AtomicReference<String> atomicRef = new AtomicReference<>();
// atomicRef не е null, но стойността, която съдържа (wrap-ва), е

// thread-safe вариант на if (ref == expected) { ref = update; }
// NB! Сравняваме референции затова използваме `==`, а не equals
atomicRef.compareAndSet(null, "Happy new year!");
System.out.println(atomicRef); // Happy new year!
```

---

#### Управление на нишки в Java

@ul

- жизнен цикъл: създаване, стартиране, изпълнение, статус
- синхронизация
- комуникация между нишки

@ulend

---

#### Създаване на нишка

@ul

- Всяка Java програма при стартирането си съдържа една нишка (main)
- За да създадем нова нишка в Java, наследяваме класа `java.lang.Thread` или имплементираме интерфейса `java.lang.Runnable`
- Логиката (инструкциите за изпълнение) е в метода `run()`

@ulend

---

#### Създаване на нишка

```java
// Option 1: extend java.lang.Thread
public class CustomThread extends Thread {
    public void run() {
        System.out.println("Hello asynchronous world!");
    }
}

Thread customThread = new CustomThread();

// Option 2: implement java.lang.Runnable
public class CustomRunnable implements Runnable {
    public void run() {
        System.out.println("Hello asynchronous world!");
    }
}

Thread customThread = new Thread(new CustomRunnable());
```

---

#### Стартиране на нишка

@ul

- За да стартираме нишка, трябва да:
    - инстанцираме класа, наследяващ `Thread` или
    - инстанцираме `Thread` и да подадем като аргумент имплементация на `Runnable`
    - извикаме метода `start()` (който вътрешно ще извика `run()`)

@ulend

---

#### Спиране на нишка

@ul

- Нишка не може да бъде спряна експлицитно веднъж щом е стартирана
- Нишката прекратява изпълнението си автоматично след приключването на метода `run()`
- Нишката не може да бъде стартирана повторно

@ulend

---

#### Thread vs Runnable

При употреба на Runnable сме по-гъвкави:

@ul

- можем да наследим друг клас
- можем да решим да изпълним имплементацията в:
  - нова нишка (като извикаме `start()`)
  - чрез thread pool (ще научим по-късно какво е това)
  - в текущата нишка (като извикаме директно `run()`)

@ulend

---

#### Thread API

<small>
Можем да дадем human-readable име на нишка чрез `setName()`. Имената нe са уникални.
Също така, нишките могат да се групират логически чрез `ThreadGroup`. Групата може да се задава само чрез конструктора.

</small>

```java
customThread.setName("Cool thread #1");

// Конструктор, който приема група и име
ThreadGroup coolThreads = new ThreadGroup("Cool thread group");
coolThread1 = new Thread(coolThreads, "Cool thread #1"); 
coolThread2 = new Thread(coolThreads, "Cool thread #2");
```

---

#### Thread API

```java
// „Спане“ – нишката „заспива“ и не получава процесорно време
// за определен интервал време
Thread.sleep(long milliseconds)

// Референция към текущата нишка
Thread.currentThread()

// Stack trace-ът на нишката
Thread.getStackTrace()
```

---

#### Приоритет на нишки


```java
// Подсказка към диспечера на нишки, каква част от процесорното
// време да получи дадена нишка. Скалата е от 1 до 10.
// По-малко число означава по-висок приоритет
// Приоритетът по подразбиране е 5.
void setPriority(int prio)

// Tекущата нишка се отказва от своето процесорно време в полза
// на друга, чийто приоритет е минимум колкото този на текущата
void yield()

// NB! 
// Добре написано приложение не трябва да разчита на
// приоритетите на нишки или на yield за своята коректност
```

---

#### Присъединяване към друга нишка

Дадена нишка може да паузира изпълнението си, докато друга нишка приключи, чрез метода `join()`

![Thread Join](images/10.3-thread-join.png)

---

#### Присъединяване към друга нишка

```java
// Извикващата нишка блокира, докато нишката, на която е извикала
// join приключи
void join()

// Ако нишката приключи или зададеното време изтече, извикващата
// нишка ще продължи изпълнението си
void join(long millis)

// Можем да проверим дали дадена нишка не е приключила изпълнението си
boolean isAlive()
```

---

#### Daemon нишки

@ul

- Според режима на работа, нишките в Java могат да бъдат два вида:
    - Стандартни (non-daemon) нишки
    - Демон (daemon) нишки

@ulend

---

#### Стандартни нишки

@ul

- изпълняват задачи, които са свързани с основната идея на програмата
- всяка JVM работи, докато има поне една работеща стандартна нишка

@ulend

---

#### Daemon нишки

<small>
- изпълняват задачи, които не са жизненоважни за програмата 
- JVM ще прекрати работата на нишките от този тип, ако няма поне една работеща стандартна нишка
- Нишките наследяват режима на работа от тази, която ги е създала, или го задават експлицитно


</small>


```java
// Може да сменим режима на нишка чрез:
void setDaemon(boolean flag)
```

---

#### Състояние на нишка

- Нишката може да бъде в различно състояние в даден момент от изпълнението си
- Методът `getState()` ни дава възможност да проверим моментното състояние на нишка

---

#### Thread.State

enum, съдържащ всички възможни състояния:

- NEW
- RUNNABLE
- BLOCKED
- WAITING
- TIMED_WAITING
- TERMINATED

---

![Thread States](images/10.4-thread-states.png)

---

#### Конкурентен достъп до споделени ресурси

---

#### Синхронизирана (Критична) секция

@ul

- Когато две или повече нишки достъпват конкурентно даден ресурс, който може да бъде променян, е необходима синхронизация
- Постига се чрез ключовата дума `synchronized`. Секцията се cъстои от:
    - монитор – логическа „ключалка“
    - блок код, който ще се изпълни ексклузивно от една нишка за даден монитор

@ulend

---

#### Синхронизирана секция


```java
public void depositMoney(BankAccount acc, double amount) {
    // Критична секция – една-единствена нишка за дадена сметка
    // acc може да изпълнява кода в синхронизираната секция
    synchronized (acc) {
        acc.deposit(amount);
    }

    // Не-критична секция - много нишки могат да бъдат едновременно тук
    System.out.println("Deposit completed");
}
```

---

#### Синхронизирана секция

@ul

- Всеки обект има вътрешен имплицитен монитор (ключалка, lock) т.е. може да се ползва за монитор
- Само една нишка в даден момент за даден монитор може да изпълнява кода (mutex == mutual exclusion)
- всеки достъп до дадено състояние, което може да бъде променено от друга нишка, трябва винаги да става в синхронизирана секция по един и същ монитор

@ulend

---

Мониторът се управлява имплицитно от JVM:

@ul

- при влизане, ако е свободен, се маркира за „зает“ от съответната нишка
- при влизане, ако не е свободен, нишката блокира и чака
- при излизане, lock-ът се освобождава и, ако има блокирани нишки, те могат да се опитат да вземат ключалката

@ulend

---

#### Синхронизиран метод

<small>Много често искаме да поставим цялото тяло на даден метод в критична секция. С цел по-четим код, Java ни предлага по-сбит вариант.</small>


```java
public void doSomeWork() {
    synchronized (this) {
        // Критична секция - само една нишка може да
        // изпълнява кода за конкретната инстанция 'this'
    }
}

public synchronized void doSomeWork() { 
    // Критична секция - само една нишка може да 
    // изпълнява кода за конкретната инстанция 'this' 
}
```

---

#### Синхронизирана секция или метод?

- Методът ce препоръчва само ако е достатъчно кратък (и бърз за изпълнение) и наистина има нужда цялото тяло да бъде „охранявано"
- Синхронизираната секция е за предпочитане, когато:
    - нуждаещото се от синхронизация парче код е малка част от метод
    - искаме да ползваме монитор, различен от `this`

---

#### Рекурсивност

<small>Lock-ът е рекурсивен (reentrant): нишката, която го „притежава“, може да извиква други критични секции по същия монитор.</small>


```java
class Demo {
    public void method1() {
        synchronized (this) {
            // изпълняващата нишка вече притежава lock-а
            // следователно може да извика method2()
            method2();
        }
    }

    public synchronized void method2() {
    }
}
```

---

#### Използване на монитор, различен от `this`

<small>
- Може да изберем да синхронизираме достъпа в дадена инстанция на обект чрез член-променлива, създадена с целта, да се използва като ключалка
- понякога даден клас има нужда от различни монитори за охраняване на различно състояние

</small>

```java
private final Object dedicatedMonitor = new Object();
```

---

#### Използване на няколко монитора

<small>Една нишка може да „притежава“ много на брой монитори, стига те да са свободни:</small>

```java
// Държането на няколко ключалки е лоша практика и 
// при възможност трябва да се избягва
public void multipleLocks() {
    synchronized (lock1) {
        // нишката притежава lock1
        synchronized (lock2) {
            // нишката притежава lock1 & lock2
            synchronized (lock3) {
                // нишката притежава lock1, lock2 & lock3
            }
            // нишката притежава lock1 & lock2
        }
        // нишката притежава lock1
    }
}
```

---

#### Синхронизация между множество инстанции на клас

@ul

- Подходящо е да ползваме ключалка, обща за класа
- удобно е да ползваме статична променлива за монитор

@ulend

---

<small>
Всяка инстанция на клас има статична референция към обекта на класа, към който принадлежи. Можем да я достъпим чрез:
</small>

```java
BankAccount.class // статично
this.getClass() // чрез ‘this’
```

---

#### Статични синхронизирани методи


```java
static void incrementOpCount() {
    synchronized (BankAccount.class) {
        opCount++;
    }
}


static synchronized void incrementOpCount() {
    opCount++;
}
```

---

#### Thread-safe обекти

Някои обекти са thread-safe по подразбиране:
- Локални обекти (локални променливи на метод)
- Stateless обекти
- Immutable обекти
- Обекти, които са ефективно final (read-only)

---

#### Съставни операции

Ако комбинираме няколко thread-safe операции в една по-сложна (обща), нямаме никаква гаранция, че те ще се изпълнят атомарно

---


```java
public synchronized void withdraw(double amount) {
    this.balance -= amount;
}

public synchronized double getBalance() {
    return balance;
}

// Бъг - този метод също трябва да е синхронизиран
public void verifyAndWithdraw(double amount) {
    if (getBalance() >= amount) {
        withdraw(amount);
    }
}
```

---

#### Мъртва хватка (Deadlock)

<small>Получава се, когато две или повече нишки се блокират една друга, всяка от тях притежаваща ключалка, от която друга нишка има нужда, но чакайки за ключалка, която някоя от другите нишки притежава.</small>


![deadlock](images/10.5-thread-states.jpg)

---

#### Мъртва хватка (Deadlock)

- Нишките не могат да бъдат прекратявани отвън
- Ключалките не могат да бъдат отнемани насилствено
- Единственият изход от мъртва хватка е рестартиране на JVM

---

## Въпроси

@snap[south span-100]
@fab[github] [fmi/java-course](https://github.com/fmi/java-course)
@snapend