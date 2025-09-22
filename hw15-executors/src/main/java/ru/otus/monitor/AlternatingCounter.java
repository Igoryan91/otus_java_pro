package ru.otus.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlternatingCounter {
    private static final Logger logger = LoggerFactory.getLogger(AlternatingCounter.class);

    private static final int MIN = 1;
    private static final int MAX = 10;

    private String last = "second";
    private int count = 1;
    private boolean shouldUpdateCount = false;
    private boolean isIncrement = true;

    private synchronized void action(String message) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (last.equals(message)) {
                    this.wait();
                }

                logger.info("{}", count);
                updateCount();
                last = message;
                sleep();
                notifyAll();
                logger.info("after notify");
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        AlternatingCounter alternatingCounter = new AlternatingCounter();
        new Thread(() -> alternatingCounter.action("first"), "first").start();
        new Thread(() -> alternatingCounter.action("second"), "second").start();
    }

    private static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void updateCount() {
        if (shouldUpdateCount) {
            if (isIncrement) {
                count++;
            } else {
                count--;
            }

            if (count == MAX || count == MIN) {
                isIncrement = !isIncrement;
            }
        }

        shouldUpdateCount = !shouldUpdateCount;
    }
}
