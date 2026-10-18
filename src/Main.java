//лабораторная работа 4 с потерей результата создать проект разобраться с кодом//есть методлы котррые помогают избежать потери 

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {//основной класс старт
    public static void main(String[] args) throws InterruptedException {
        new InterferenceExample().example();//вызывает объект класса InterferenceExample() а потом сразу же вызызвает объект метода example()
    }
}
class InterferenceThread extends Thread {//наследуется класс InterferenceThread от потока thread
    private final InterferenceExample checker;// final нужен для неизменности  константа
    private static int i;
    private AtomicInteger counter = new AtomicInteger(0);//АТОМАРНЫЙ СЧЕТЧИК БЕЗ ПОТЕРЬ
//    private final Semaphore semaphore = new Semaphore(1);//ИСПОЛЬЗОВАНИЕ СЕМАФОРА
//    private void increment(){
//        try{
//            semaphore.acquire();//захват разрешения
//    i++;
//  catch (InterruptedException e) {
//        Thread.currentThread().interrupt();
//    } finally {
//        semaphore.release(); // Обязательно возвращаем разрешение в блоке finally!
//    }

    InterferenceThread(InterferenceExample checker) {
        this.checker = checker;
    }

    private synchronized void increment() {//метод//ПЕРВАЯ ЗАЩИТА БЕЗ ПОТЕРЬ
        counter.incrementAndGet();//безопасный счетчик для инкремента без потери
        i++;
    }

    int getI() {//метод геттер
        return i;
//        return counter.get();//ИТОГОВОЕ ЗНАЧЕНИЕ
    }

    public void run() {//идти до  тех пор пока не остановлен поток
        while (!checker.stop()) {
            increment();
        }
    }
}
class InterferenceExample {
    private static final int HUNDRED_MILLION = 100_000_000;
    private AtomicInteger counter = new AtomicInteger();//атомарный счетчик

    boolean stop(){
        return counter.incrementAndGet() > HUNDRED_MILLION;//incrementAndGet()- метод атомарного счетчика
    }

    void example() throws InterruptedException {// throws-делегирует ошибку в исключение типа InterruptedException
        InterferenceThread thread1 = new InterferenceThread(this);
        InterferenceThread thread2 = new InterferenceThread(this);
        thread1.start();
        thread2.start();
        thread1.join();//вызов двух потоков
        thread2.join();
        System.out.println("Expected: " + HUNDRED_MILLION);
        System.out.println("Result: " + thread1.getI());


    }
}


