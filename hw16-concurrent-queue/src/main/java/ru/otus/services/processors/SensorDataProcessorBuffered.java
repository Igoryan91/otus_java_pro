package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final PriorityBlockingQueue<SensorData> dataBuffer;
    private final AtomicBoolean flushing = new AtomicBoolean(false);

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new PriorityBlockingQueue<>(bufferSize, Comparator.comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {
        dataBuffer.add(data);

        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        if (!flushing.compareAndSet(false, true)) {
            return;
        }

        if (dataBuffer.isEmpty()) {
            flushing.set(false);
            return;
        }

        List<SensorData> bufferedData = new ArrayList<>();
        dataBuffer.drainTo(bufferedData);

        try {
            writer.writeBufferedData(bufferedData);
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        } finally {
            flushing.set(false);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
