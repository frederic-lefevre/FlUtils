package org.fl.util.io;

import java.util.List;
import java.util.ArrayList;

//A thread safe circular int buffer with multiple readers and one writer
public class MultiReaderCircularBuffer<E> {

    private static final int DEFAULT_CAPACITY = 8192;
    protected static final int MINIMAL_CAPACITY = 512;
    private static final int MAXIMAL_CAPACITY = 1048576;

    protected static final int MAX_READ_CLIENTS = 8;

    private final int capacity;

    private E[] buffer;

    private long writePosition;

    private List<Long> readPositions;

    public MultiReaderCircularBuffer() {
        this.capacity = DEFAULT_CAPACITY;
        init(DEFAULT_CAPACITY);
    }

    public MultiReaderCircularBuffer(int capacity) {

        this.capacity = returnBoundedCapacity(capacity);
        init(this.capacity);
    }

    // Essentially for test
    protected MultiReaderCircularBuffer(int capacity, boolean force) {

        if (force) {
            this.capacity = capacity;
        } else {
            this.capacity = returnBoundedCapacity(capacity);
        }
        init(this.capacity);
    }

    private int returnBoundedCapacity(int capacity) {
        if (capacity < MINIMAL_CAPACITY) {
            return MINIMAL_CAPACITY;
        } else {
            return Math.min(capacity, MAXIMAL_CAPACITY);
        }
    }

    public synchronized int newReadClient() {

        if (readPositions.size() >= MAX_READ_CLIENTS) {
            return -1;
        } else {
            if (writePosition > -1) {
                // if write operation has started, the new reader will only get the last write and future write
                readPositions.add(writePosition);
            } else {
                readPositions.add(0L);
            }
            return readPositions.size() - 1;
        }
    }

    public synchronized boolean write(E i) {

        if (numberOfUnreadElement() >= capacity) {
            // no more space to write
            return false;
        } else {
            int newWriteIndex = (int) ((writePosition + 1) % capacity);
            buffer[newWriteIndex] = i;
            writePosition++;
            return true;
        }
    }

    public synchronized E read(int clientId) {

        if ((clientId < 0) || (clientId >= readPositions.size())) {
            // unknown client id
            throw new IllegalArgumentException("Invalid read client id " + clientId + ". Max id=" + readPositions.size());
        }

        long readPosition = readPositions.get(clientId);
        if (writePosition < readPosition) {
            // nothing to read
            return null;
        } else {
            int readIndex = (int)(readPosition % capacity);
            E readValue = buffer[readIndex];
            readPositions.set(clientId, readPosition + 1);
            return readValue;
        }
    }

    public long available(int clientId) {
        // available being an optional estimation, it is better to not synchronize it
        // result can only be lower than the reality when the method is called by the client reader
        long readPosition = readPositions.get(clientId);
        if (writePosition < readPosition) {
            // nothing to read
            return 0;
        } else {
            return writePosition - readPosition + 1;
        }
    }

    private long numberOfUnreadElement() {
        return (writePosition - minimalReadPosition()) + 1;
    }

    private long minimalReadPosition() {
        if (readPositions.isEmpty()) {
            return 0;
        } else {
            long minPos = Long.MAX_VALUE;
            for (Long pos : readPositions) {
                if (pos < minPos) {
                    minPos = pos;
                }
            }
            return minPos;
        }
    }

    @SuppressWarnings("unchecked")
    private void init(int bufferSize) {
        this.writePosition = -1;
        buffer = (E[]) new Object[bufferSize];
        readPositions = new ArrayList<>();
    }
}
