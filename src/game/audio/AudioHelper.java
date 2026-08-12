package game.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioSystem;

public class AudioHelper
{
    private static final float SAMPLE_RATE = 44100.0f;
    private static volatile boolean backgroundRunning = false;
    private static Thread backgroundThread;

    public static void playTone(final double frequency, final int durationMs, final double volume)
    {
        new Thread(() -> {
            try
            {
                byte[] toneBuffer = createToneBuffer(frequency, durationMs, volume);
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format))
                {
                    line.open(format);
                    line.start();
                    line.write(toneBuffer, 0, toneBuffer.length);
                    line.drain();
                }
            }
            catch (LineUnavailableException ignored)
            {
            }
        }, "AudioHelper-tone").start();
    }

    public static void playPickupSound()
    {
        playTone(880.0, 120, 0.6);
        playTone(1100.0, 90, 0.4);
    }

    public static void playHitSound()
    {
        playTone(220.0, 180, 0.6);
        playTone(330.0, 120, 0.4);
    }

    public static void playGameOverSound()
    {
        playTone(220.0, 260, 0.5);
        playTone(196.0, 260, 0.5);
    }

    public static void playLevelUpSound()
    {
        playTone(880.0, 80, 0.4);
        playTone(1047.0, 80, 0.4);
        playTone(1320.0, 80, 0.4);
    }

    public static void playBackgroundMusic()
    {
        if (backgroundRunning)
            return;

        backgroundRunning = true;
        backgroundThread = new Thread(() -> {
            try
            {
                AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
                try (SourceDataLine line = AudioSystem.getSourceDataLine(format))
                {
                    line.open(format);
                    line.start();

                    while (backgroundRunning)
                    {
                        byte[] toneBuffer = createToneBuffer(220.0, 300, 0.08);
                        line.write(toneBuffer, 0, toneBuffer.length);
                        Thread.sleep(100);
                    }
                    line.drain();
                }
            }
            catch (LineUnavailableException | InterruptedException ignored)
            {
            }
        }, "AudioHelper-background");

        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public static void stopBackgroundMusic()
    {
        backgroundRunning = false;
        if (backgroundThread != null)
        {
            backgroundThread.interrupt();
            backgroundThread = null;
        }
    }

    private static byte[] createToneBuffer(final double frequency, final int durationMs, final double volume)
    {
        int sampleCount = (int) (SAMPLE_RATE * durationMs / 1000.0);
        byte[] buffer = new byte[sampleCount * 2];

        for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++)
        {
            double angle = 2.0 * Math.PI * frequency * sampleIndex / SAMPLE_RATE;
            short amplitude = (short) (Math.sin(angle) * Short.MAX_VALUE * Math.max(0.0, Math.min(volume, 1.0)));
            buffer[sampleIndex * 2] = (byte) (amplitude & 0xFF);
            buffer[sampleIndex * 2 + 1] = (byte) ((amplitude >> 8) & 0xFF);
        }

        return buffer;
    }
}
