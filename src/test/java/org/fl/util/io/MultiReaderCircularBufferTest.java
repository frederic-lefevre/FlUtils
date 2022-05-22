package org.fl.util.io;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.fl.util.io.MultiReaderCircularBuffer.MAX_READ_CLIENTS;
import static org.fl.util.io.MultiReaderCircularBuffer.MINIMAL_CAPACITY;

public class MultiReaderCircularBufferTest {

    @ParameterizedTest
    @ValueSource(ints = {4096, 0, -1, Integer.MAX_VALUE})
    void regularTest(int capacity) {


        MultiReaderCircularBuffer<Integer> integerBuffer;
        if (capacity == -1) {
            // test with default constructor
            integerBuffer = new MultiReaderCircularBuffer<>();
        } else {
            integerBuffer = new MultiReaderCircularBuffer<>(capacity);
        }

        int readClient1 = integerBuffer.newReadClient();
        int readClient2 = integerBuffer.newReadClient();

        assertThat(integerBuffer.read(readClient1)).isNull();
        assertThat(integerBuffer.read(readClient2)).isNull();

        int value = 22;
        assertThat(integerBuffer.write(22)).isTrue();

        assertThat(integerBuffer.read(readClient1)).isEqualTo(value);
        assertThat(integerBuffer.read(readClient1)).isNull();

        assertThat(integerBuffer.read(readClient2)).isEqualTo(value);
        assertThat(integerBuffer.read(readClient2)).isNull();

        int nbWrites = 440;
        for (int i = 0; i < nbWrites; i++) {
            assertThat(integerBuffer.write(i)).isTrue();
            assertThat(integerBuffer.available(readClient1)).isEqualTo(integerBuffer.available(readClient2)).isEqualTo(i+1);
        }

        for (int i = 0; i < nbWrites; i++) {
            assertThat(integerBuffer.read(readClient1)).isEqualTo(i);
            assertThat(integerBuffer.available(readClient1)).isEqualTo(nbWrites - i - 1);
        }
        assertThat(integerBuffer.read(readClient1)).isNull();

        for (int i = 0; i < nbWrites; i++) {
            assertThat(integerBuffer.read(readClient2)).isEqualTo(i);
        }
        assertThat(integerBuffer.read(readClient2)).isNull();

        int nbWrites2 = 444;
        for (int i = 0; i < nbWrites2; i++) {
            assertThat(integerBuffer.write(i)).isTrue();
            assertThat(integerBuffer.read(readClient2)).isEqualTo(i);
            if (i % 2 == 0) {
                assertThat(integerBuffer.read(readClient1)).isEqualTo(i/2);
            }
        }
        assertThat(integerBuffer.read(readClient2)).isNull();

        for (int i = nbWrites2/2; i < nbWrites2; i++) {
            assertThat(integerBuffer.read(readClient1)).isEqualTo(i);
        }
        assertThat(integerBuffer.read(readClient1)).isNull();
    }

    @Test
    void invalidClientId() {

        MultiReaderCircularBuffer<Integer> integerBuffer = new MultiReaderCircularBuffer<>();
        assertThatIllegalArgumentException().isThrownBy(() -> integerBuffer.read(0));

        assertThatIllegalArgumentException().isThrownBy(() -> integerBuffer.read(-1));
    }

    @Test
    void readClientCreationAfterFirstWrite() {

        MultiReaderCircularBuffer<Integer> integerBuffer = new MultiReaderCircularBuffer<>();
        int nbWrites = 44;
        for (int i = 0; i < nbWrites; i++) {
            assertThat(integerBuffer.write(i)).isTrue();
        }

        int readClient = integerBuffer.newReadClient();

        // only the last byte is available
        assertThat(integerBuffer.available(readClient)).isEqualTo(1);
        assertThat(integerBuffer.read(readClient)).isEqualTo(nbWrites-1);
        assertThat(integerBuffer.available(readClient)).isZero();
        assertThat(integerBuffer.read(readClient)).isNull();

        assertThat(integerBuffer.write(11)).isTrue();
        assertThat(integerBuffer.read(readClient)).isEqualTo(11);
    }

    @Test
    void maximalNumberOfClient() {

        MultiReaderCircularBuffer<Integer> integerBuffer = new MultiReaderCircularBuffer<>();

        int clientId;
        int numberOfClient = -1;
        do {
            clientId = integerBuffer.newReadClient();
            numberOfClient++;
        } while (clientId != -1);
        assertThat(numberOfClient).isEqualTo(MAX_READ_CLIENTS);
    }

    @Test
    void bufferFull() {

        // Size of the buffer will be the minimal size
        MultiReaderCircularBuffer<Integer> integerBuffer = new MultiReaderCircularBuffer<>(1);
        int readClient1 = integerBuffer.newReadClient();
        int readClient2 = integerBuffer.newReadClient();

        int i = 0;
        boolean success;
        do {
            success = integerBuffer.write(i++);
        } while (success);
        // Buffer is now full

        Integer result = -1;
        int lastValidResult = -1;
        while (result != null) {
            lastValidResult = result;
           result = integerBuffer.read(readClient1);
        }
        assertThat(lastValidResult).isEqualTo(MINIMAL_CAPACITY - 1);

        // Still not possible to write (readClient2 has not read anything)
        assertThat(integerBuffer.write(144)).isFalse();

        // readClient2 reads the first byte written (0)
        assertThat( integerBuffer.read(readClient2)).isZero();

        // now it is possible to write
        assertThat(integerBuffer.write(145)).isTrue();

        // but just one byte !
        assertThat(integerBuffer.write(146)).isFalse();
    }
}
