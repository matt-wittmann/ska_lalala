package com.mattwittmann.soundexperiment;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Building on the Java platform, the Java Sound API is about as low as we can get without using
 * something like JNI. Scala currently does not provide any standard API that wraps over this,
 * so my first task is to familiarize myself with the API.
 *
 * @author matt@mattwittmann.com
 */
public class SoundExperiment {
	ByteBuffer buffer;
	SourceDataLine line;
	float sampleRate;
	int sampleSizeInBits;
	int sampleSizeInBytes;

	public SoundExperiment(float sampleRate, int sampleSizeInBits) {
		this.sampleRate = sampleRate;
		this.sampleSizeInBits = sampleSizeInBits;
		sampleSizeInBytes = sampleSizeInBits / 8;

		AudioFormat audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, 1, sampleSizeInBits >= 16, true);
		if (!AudioSystem.isLineSupported(new DataLine.Info(SourceDataLine.class, audioFormat))) {
			throw new RuntimeException(new LineUnavailableException("Audio format of " + sampleRate + " Hz by " + sampleSizeInBits + " bits is unsupported."));
		}
		try {
			line = AudioSystem.getSourceDataLine(audioFormat);
			line.open();
			line.start();
			buffer = ByteBuffer.allocate(line.getBufferSize());
		}
		catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

	public SoundExperiment() {
		this(44100, 8);
	}

	public SoundExperiment play(int frequency, int duration) {
		double cycle = (double) frequency / (double) sampleRate;
		int count = (int) ((sampleRate * duration) / 1000);
		double cyclePosition = 0;
		while (count > 0) {
			buffer.clear();
			int available = line.available();
			int samplesToDo = available / sampleSizeInBytes;
			for (int i = 0; i < samplesToDo; i++) {
				if (sampleSizeInBits == 16) {
					buffer.putShort((short) (Short.MAX_VALUE * Math.sin(sampleSizeInBytes * Math.PI * cyclePosition)));
				}
				else if (sampleSizeInBits == 8) {
					buffer.put((byte) (Byte.MAX_VALUE * Math.sin(sampleSizeInBytes * Math.PI * cyclePosition)));
				}
				cyclePosition += cycle;
				if (cyclePosition > 1) {
					cyclePosition -= 1;
				}
			}
			line.write(buffer.array(), 0, buffer.position());
			count -= samplesToDo;

			while (line.getBufferSize() / 2 < line.available()) {
				try {
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					close();
					throw new RuntimeException(e);
				}
			}
		}
		return this;
	}

	public void close() {
		line.drain();
		line.stop();
		line.close();
		line = null;
		buffer = null;
	}

	public static void main(String[] args) {
		new SoundExperiment(44100, 16).play(440, 100).play(410, 200).play(440, 100).close();
	}
}