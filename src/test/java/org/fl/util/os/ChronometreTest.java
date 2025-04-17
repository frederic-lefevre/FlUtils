/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

package org.fl.util.os;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ChronometreTest {

	@Test
	void testNewChronometre() {
		
		Chronometre chronometre = new Chronometre();
		assertThat(chronometre).isNotNull();
		
		assertThat(chronometre.getValue()).isZero();
	}
	
	@Test
	void testRunningChronometre() throws InterruptedException {
		
		Chronometre chronometre = new Chronometre();
		chronometre.start();
		Thread.sleep(200);
		assertThat(chronometre.getValue()).isGreaterThan(180);
	}
	
	@Test
	void testPausedChronometre() throws InterruptedException {
		
		Chronometre chronometre = new Chronometre();
		chronometre.start();
		Thread.sleep(100);
		long t1 = chronometre.getValue();
		Thread.sleep(100);
		long t2 = chronometre.pause();
		Thread.sleep(100);
		long t3 = chronometre.getValue();
		
		assertThat(chronometre.getValue())
			.isGreaterThan(180)
			.isEqualTo(t3)
			.isEqualTo(t2)
			.isGreaterThan(t1);
	}
	
	@Test
	void testResetChronometre() throws InterruptedException {
		
		Chronometre chronometre = new Chronometre();
		chronometre.start();
		Thread.sleep(100);
		long t1 = chronometre.getValue();
		Thread.sleep(100);
		long t2 = chronometre.reset();
		Thread.sleep(100);
		long t3 = chronometre.getValue();
		
		assertThat(chronometre.getValue())
			.isZero()
			.isEqualTo(t3);
		assertThat(t2).isGreaterThan(150).isGreaterThan(t1);
		assertThat(t1).isGreaterThan(50);
	}
}
