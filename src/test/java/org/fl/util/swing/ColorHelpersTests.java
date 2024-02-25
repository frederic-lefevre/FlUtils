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

package org.fl.util.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class ColorHelpersTests {

	@Test
	void testBlue() {

		Color c = ColorHelpers.parse("BLUE");
		assertThat(c).isEqualTo(Color.BLUE);

		c = ColorHelpers.parse("blue");
		assertThat(c).isEqualTo(Color.blue);

		c = ColorHelpers.parse("Blue");
		assertThat(c).isNull();
	}

	@Test
	void testGreen() {

		Color c = ColorHelpers.parse("GREEN");
		assertThat(c).isEqualTo(Color.GREEN);

		c = ColorHelpers.parse("green");
		assertThat(c).isEqualTo(Color.green);

		c = ColorHelpers.parse("Green");
		assertThat(c).isNull();
	}

	@Test
	void testWrongString() {

		Color c = ColorHelpers.parse("WRONG");
		assertThat(c).isNull();
	}

	@Test
	void testRGB() {

		Color c = ColorHelpers.parse("#C6C6C6");
		assertThat(c).isNotNull();
	}

	@Test
	void testRGB2() {

		Color c = ColorHelpers.parse("0");
		assertThat(c).isNotNull().isEqualTo(Color.black);
	}

	@Test
	void testEmpy() {

		Color c = ColorHelpers.parse("");
		assertThat(c).isNull();
	}

	@Test
	void testNull() {

		Color c = ColorHelpers.parse(null);
		assertThat(c).isNull();
	}
}
