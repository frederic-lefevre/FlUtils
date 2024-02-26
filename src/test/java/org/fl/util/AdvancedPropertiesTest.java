/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import java.util.List;

import org.junit.jupiter.api.Test;

class AdvancedPropertiesTest {

	@Test
	void testKeys() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c.k2.z", "s2");
		advProps.setProperty("a.b.c.k1.e", "s1");
		advProps.setProperty("a.b.c.k4", "s4");
		advProps.setProperty("a.b.c.k3.t.v", "s3");

		assertThat(advProps.getProperty("a.b.c.k1.e")).isEqualTo("s1");
		assertThat(advProps.getProperty("a.b.c.k2.z")).isEqualTo("s2");
		assertThat(advProps.getProperty("a.b.c.k3.t.v")).isEqualTo("s3");
		assertThat(advProps.getProperty("a.b.c.k4")).isEqualTo("s4");

		List<String> keys = advProps.getKeysElements("a.b.c.");
		assertThat(keys).hasSize(4).hasSameElementsAs(List.of("k1", "k2", "k3", "k4"));
	}

	@Test
	void testKeys2() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "10");

		List<String> keys = advProps.getKeysElements("a.b.c.");
		assertThat(keys).isNotNull().isEmpty();

		keys = advProps.getKeysElements("a.b");
		assertThat(keys).isNotNull().isEmpty();

		keys = advProps.getKeysElements(".");
		assertThat(keys).isNotNull().isEmpty();

		keys = advProps.getKeysElements("");
		assertThat(keys).isNotNull().singleElement().isEqualTo("p1");
	}

	@Test
	void testKeys3() {
		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "10");

		assertThatNullPointerException().isThrownBy(() -> advProps.getKeysElements(null));
	}

	@Test
	void testInt() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "10");
		assertThat(advProps.getProperty("p1")).isEqualTo("10");

		int i = advProps.getInt("p1", 9);
		assertThat(i).isEqualTo(10);
	}

	@Test
	void testInt2() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "10");
		assertThat(advProps.getProperty("unknown")).isNull();

		int i = advProps.getInt("unknown", 9);
		assertThat(i).isEqualTo(9);
	}

	@Test
	void testInt3() {

		AdvancedProperties advProps = new AdvancedProperties();
		LoggerCounter noLog = LoggerCounter.getLogger();
		advProps.setLog(noLog);

		advProps.setProperty("p1", "notAnumber");
		assertThat(advProps.getProperty("p1")).isEqualTo("notAnumber");

		int i = advProps.getInt("p1", 9);
		assertThat(i).isEqualTo(9);
		assertThat(noLog.getErrorCount()).isEqualTo(1);
	}

	@Test
	void testLong() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "10");
		assertThat(advProps.getProperty("p1")).isEqualTo("10");

		long i = advProps.getLong("p1", 9);
		assertThat(i).isEqualTo(10);
	}

	@Test
	void testLong2() {

		AdvancedProperties advProps = new AdvancedProperties();
		LoggerCounter noLog = LoggerCounter.getLogger();
		advProps.setLog(noLog);

		advProps.setProperty("p1", "notAnumber");
		assertThat(advProps.getProperty("p1")).isEqualTo("notAnumber");

		long i = advProps.getLong("p1", 1000000000);
		assertThat(i).isEqualTo(1000000000);
		assertThat(noLog.getErrorCount()).isEqualTo(1);
	}

	@Test
	void testLong3() {

		AdvancedProperties advProps = new AdvancedProperties();

		advProps.setProperty("p1", "10");
		assertThat(advProps.getProperty("unknown")).isNull();

		long i = advProps.getLong("unknown", 9);
		assertThat(i).isEqualTo(9);
	}

	@Test
	void testDouble() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "-10.2");
		assertThat(advProps.getProperty("p1")).isEqualTo("-10.2");

		double i = advProps.getDouble("p1", 9.5);
		assertThat(i).isEqualTo(-10.2);
	}

	@Test
	void testDouble2() {

		AdvancedProperties advProps = new AdvancedProperties();
		LoggerCounter noLog = LoggerCounter.getLogger();
		advProps.setLog(noLog);

		advProps.setProperty("p1", "notAnumber");
		assertThat(advProps.getProperty("p1")).isEqualTo("notAnumber");

		double i = advProps.getDouble("p1", 1000000000.1);
		assertThat(i).isEqualTo(1000000000.1);
		assertThat(noLog.getErrorCount()).isEqualTo(1);
	}

	@Test
	void testDouble3() {

		AdvancedProperties advProps = new AdvancedProperties();

		advProps.setProperty("p1", "10.7");
		assertThat(advProps.getProperty("unknown")).isNull();

		double i = advProps.getDouble("unknown", 9.8);
		assertThat(i).isEqualTo(9.8);
	}

	@Test
	void testBoolean() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "true");
		assertThat(advProps.getProperty("p1")).isEqualTo("true");

		assertThat(advProps.getBoolean("p1", false)).isTrue();
	}

	@Test
	void testBoolean2() {

		AdvancedProperties advProps = new AdvancedProperties();
		LoggerCounter noLog = LoggerCounter.getLogger();
		advProps.setLog(noLog);

		advProps.setProperty("p1", "notBool");
		assertThat(advProps.getProperty("p1")).isEqualTo("notBool");

		assertThat(advProps.getBoolean("p1", true)).isTrue();
		assertThat(advProps.getBoolean("p1", false)).isFalse();
		assertThat(noLog.getErrorCount()).isEqualTo(2);
	}

	@Test
	void testBoolean3() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "True");
		assertThat(advProps.getProperty("p1")).isEqualTo("True");

		assertThat(advProps.getBoolean("p1", false)).isTrue();
	}

	@Test
	void testBoolean4() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "True");
		assertThat(advProps.getProperty("p1")).isEqualTo("True");

		assertThat(advProps.getBoolean("unknown", true)).isTrue();
		assertThat(advProps.getBoolean("unknown", false)).isFalse();
	}

	@Test
	void testChar() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "c");
		assertThat(advProps.getProperty("p1")).isEqualTo("c");

		char i = advProps.getChar("p1", 'r');
		assertThat(i).isEqualTo('c');
	}

	@Test
	void testChar2() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "char");
		assertThat(advProps.getProperty("p1")).isEqualTo("char");

		char i = advProps.getChar("p1", 'r');
		assertThat(i).isEqualTo('c');
	}

	@Test
	void testChar3() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "c");
		assertThat(advProps.getProperty("p1")).isEqualTo("c");

		char i = advProps.getChar("unknown", 'r');
		assertThat(i).isEqualTo('r');
	}

	@Test
	void testChar4() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("p1", "");
		assertThat(advProps.getProperty("p1")).isEmpty();

		char i = advProps.getChar("p1", 'r');
		assertThat(i).isEqualTo('r');
	}

	@Test
	void testArrayOfInts() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "5,4,3,2,1");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("5,4,3,2,1");

		int[] ints = advProps.getArrayOfInt("a.b.c", ",");
		assertThat(ints).containsExactly(5,4,3,2,1);
	}

	@Test
	void testArrayOfInts2() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "5");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("5");

		int[] ints = advProps.getArrayOfInt("a.b.c", ",");
		assertThat(ints).containsExactly(5);
	}

	@Test
	void testArrayOfInts3() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "5");

		advProps.setProperty("a.b.c", "5");

		int[] ints = advProps.getArrayOfInt("a.b.c", "");
		assertThat(ints).isNull();
	}

	@Test
	void testArrayOfInts4() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "5");

		int[] ints = advProps.getArrayOfInt("unknown", ",");
		assertThat(ints).isNull();
	}

	@Test
	void testArrayOfInts5() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "5,4,NotAnumber,2,1");
		LoggerCounter noLog = LoggerCounter.getLogger();
		advProps.setLog(noLog);

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("5,4,NotAnumber,2,1");

		int[] ints = advProps.getArrayOfInt("a.b.c", ",");
		assertThat(ints).isNull();
		assertThat(noLog.getErrorCount()).isEqualTo(1);
	}

	@Test
	void testArrayOfStrings() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "cinq,quatre,trois,deux,un");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("cinq,quatre,trois,deux,un");

		String[] strings = advProps.getArrayOfString("a.b.c", ",");
		assertThat(strings).hasSize(5).containsExactly("cinq", "quatre", "trois", "deux", "un");
	}

	@Test
	void testArrayOfStrings2() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "trois");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("trois");

		String[] strings = advProps.getArrayOfString("a.b.c", ",");
		assertThat(strings).singleElement().isEqualTo("trois");
	}

	@Test
	void testArrayOfStrings3() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "trois");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("trois");

		String[] strings = advProps.getArrayOfString("a.b.c", "");
		assertThat(strings).isNull();
	}

	@Test
	void testArrayOfStrings4() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "trois");

		String[] strings = advProps.getArrayOfString("unknown", ",");
		assertThat(strings).isNull();
	}

	@Test
	void testListOfStrings() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "cinq,quatre,trois,deux,un");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("cinq,quatre,trois,deux,un");

		List<String> strings = advProps.getListOfString("a.b.c", ",");
		assertThat(strings).hasSize(5).containsExactly("cinq", "quatre", "trois", "deux", "un");
	}

	@Test
	void testListOfStrings2() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "trois");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("trois");

		List<String> strings = advProps.getListOfString("a.b.c", ",");
		assertThat(strings).singleElement().isEqualTo("trois");
	}

	@Test
	void testListOfStrings3() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "trois");

		assertThat(advProps.getProperty("a.b.c")).isEqualTo("trois");

		List<String> strings = advProps.getListOfString("a.b.c", "");
		assertThat(strings).isNull();
	}

	@Test
	void testListOfStrings4() {

		AdvancedProperties advProps = new AdvancedProperties();
		advProps.setProperty("a.b.c", "trois");

		List<String> strings = advProps.getListOfString("unknown", ",");
		assertThat(strings).isNull();
	}
}
