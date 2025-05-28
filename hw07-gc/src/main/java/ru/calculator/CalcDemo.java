package ru.calculator;

/*
-Xms256m
-Xmx256m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./logs/heapdump.hprof
-XX:+UseG1GC
-Xlog:gc=debug:file=./logs/gc-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m
*/

/*
Пришлось уменьшить counter на один порядок, иначе было 140 сек.

Замеры до рефакторинга:

Размер хипа |Время выполнения
------------|----------------
64m         |msec:16925, sec:16
128m        |msec:14772, sec:14
256m        |msec:14374, sec:14  *
256m        |msec:14374, sec:14
512m        |msec:14425, sec:14
1024m       |msec:14455, sec:14
2048m       |msec:14217, sec:14

Вывод: последнее хоть сколько-нибудь существенное изменение по времени выполнения
произошло при увеличении хипа с 128 до 256мб (2%). Значит оптимальным размером хипа будет 256мб.


Замеры после рефакторинга:

Размер хипа     |Время выполнения
----------------|----------------
4m              |msec:59933, sec:59
8m              |msec:14926, sec:14
16m             |msec:13855, sec:13
32m             |msec:13129, sec:13  *
64m             |msec:13263, sec:13
128m            |msec:13116, sec:13

Вывод: После рефакторинга кода оптимальный размер хипа - 32мб.
*/

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalcDemo {
    private static final Logger log = LoggerFactory.getLogger(CalcDemo.class);

    public static void main(String[] args) {
        long counter = 50_000_000;
        var summator = new Summator();
        long startTime = System.currentTimeMillis();
        var data = new Data(0);

        for (var idx = 0; idx < counter; idx++) {
            data.setValue(idx);
            summator.calc(data);

            if (idx % 10_000_000 == 0) {
                log.info("{} current idx:{}", LocalDateTime.now(), idx);
            }
        }

        long delta = System.currentTimeMillis() - startTime;
        log.info("PrevValue:{}", summator.getPrevValue());
        log.info("PrevPrevValue:{}", summator.getPrevPrevValue());
        log.info("SumLastThreeValues:{}", summator.getSumLastThreeValues());
        log.info("SomeValue:{}", summator.getSomeValue());
        log.info("Sum:{}", summator.getSum());
        log.info("spend msec:{}, sec:{}", delta, (delta / 1000));
    }
}
