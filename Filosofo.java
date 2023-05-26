import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Philosopher implements Runnable {
    private int id;
    private Lock leftFork;
    private Lock rightFork;
    private String path;

    public Philosopher(int id, Lock leftFork, Lock rightFork, String path) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.path = path;
    }

    private void think() throws InterruptedException {
        System.out.println("Filósofo " + id + " está pensando.");
        Thread.sleep(1000);
    }

    private void eat() throws InterruptedException {
        leftFork.lock();
        rightFork.lock();

        System.out.println("Filósofo " + id + " está comendo.");
        Thread.sleep(1000);

        rightFork.unlock();
        leftFork.unlock();
    }

    private void exploreFiles() {
        System.out.println("Filósofo " + id + " Está Verificando quem está pensando : " + path);
        exploreRecursive(new File(path), 0);
    }

    private void exploreRecursive(File file, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }

        System.out.println(indent + file.getName());

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    exploreRecursive(subFile, depth + 1);
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                eat();
                exploreFiles();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    public void interrupt() {
    }
}

public class Filosofo {
    public static void main(String[] args) {
        int numPhilosophers = 5;
        Lock[] forks = new ReentrantLock[numPhilosophers];
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Thread[] threads = new Thread[numPhilosophers];

        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new ReentrantLock();
        }

        String path = "/Diretorio"; // Insira o caminho do diretório que deseja explorar

        for (int i = 0; i < numPhilosophers; i++) {
            int leftForkIndex = i;
            int rightForkIndex = (i + 1) % numPhilosophers;
            philosophers[i] = new Philosopher(i, forks[leftForkIndex], forks[rightForkIndex], path);
            threads[i] = new Thread(philosophers[i]);
            threads[i].start();
        }

        try {
            Thread.sleep(5000);
            for (Philosopher philosopher : philosophers) {
                philosopher.interrupt();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}