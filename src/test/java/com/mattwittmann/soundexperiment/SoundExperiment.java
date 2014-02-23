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
    private static final double C4 = 261.63;
    private static final double D4 = 293.66;
    private static final double E4 = 329.63;
    private static final double F4 = 349.23;
    private static final int G4 = 392;
    private static final int A4 = 440;
    private static final double B4 = 493.88;
    private static final double C5 = 523.23;
    private static final double D5 = 587.33;
    private static final double E5 = 659.26;
    private static final double F5 = 698.46;
    private static final double G5 = 783.99;
    private static final int A5 = 880;
    private static final double B5 = 987.77;

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

	public SoundExperiment play(double frequency, int duration) {
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
        // Scale, ascending, descending
		new SoundExperiment(44100, 16).play(C4, 200).play(D4, 200).play(E4, 200).play(F4, 200)
                                      .play(G4, 200).play(A4, 200).play(B4, 200).play(A4, 200)
                                      .play(G4, 200).play(F4, 200).play(E4, 200).play(D4, 200)
                                      .play(C4, 200)
        .close();
	}
}