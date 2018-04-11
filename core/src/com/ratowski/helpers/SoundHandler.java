package com.ratowski.helpers;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class SoundHandler {

    private static int[] frequencyArray = new int [10];
    private static int dominantFrequency = 0;
    private static final int MAX_FREQUENCY = 1110;
    private static AudioDispatcher audioDispatcher;
    private static PitchDetectionHandler pitchDetectionHandler;
    private static AudioProcessor pitchAudioProcessor;
    private static AudioProcessor volumeAudioProcessor;
    static float pitchValueInHz;
    static double soundVolume;

    public SoundHandler(){
        for(int i=0;i< frequencyArray.length;i++) {
            frequencyArray[i] = 0;
        }
        setupHandler();
        startHandler();
    }

    public static void setupHandler() {
        audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result, AudioEvent e)
            {
                pitchValueInHz = result.getPitch();
            }
        };
    }

    public static void startHandler() {
        pitchAudioProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pitchDetectionHandler);
        audioDispatcher.addAudioProcessor(pitchAudioProcessor);
        new Thread(audioDispatcher, "VoiceHandler").start();

        volumeAudioProcessor = new AudioProcessor(){
            @Override
            public boolean process(AudioEvent audioEvent) {
                float[] audioBuffer = audioEvent.getFloatBuffer();
                soundVolume = soundPressureDBLevel(audioBuffer);
                return true;
            }

            @Override
            public void processingFinished() {}

            private double soundPressureDBLevel(final float[] buffer) {
                double power = 0.0D;
                for (float element : buffer) {
                    power += element * element;
                }
                double value = Math.pow(power, 0.5)/ buffer.length;
                return 20.0 * Math.log10(value);
            }
        };
        audioDispatcher.addAudioProcessor(volumeAudioProcessor);
    }

    public static int getSoundVolume() {
        return (int) soundVolume;
    }

    public static int getDominantFrequency() {
        for (int i = 1; i < frequencyArray.length; i++)
            frequencyArray[i] = frequencyArray[i - 1];

        frequencyArray[0] = (int) pitchValueInHz;

        if (Math.abs(frequencyArray[0] - frequencyArray[1]) < 10 &&
            Math.abs(frequencyArray[0] - frequencyArray[2]) < 10 &&
            Math.abs(frequencyArray[0] - frequencyArray[3]) < 10 &&
            Math.abs(frequencyArray[0] - frequencyArray[4]) < 10 &&
            frequencyArray[0] > 95) {
            dominantFrequency = frequencyArray[0];
        }
        else {
            dominantFrequency = 0;
        }
        if (dominantFrequency > MAX_FREQUENCY) {
            dominantFrequency = MAX_FREQUENCY;
        }
        return dominantFrequency;
    }

    public static void dispose() {
        if(audioDispatcher != null) {
            audioDispatcher.removeAudioProcessor(pitchAudioProcessor);
            audioDispatcher.stop();
        }
    }
}
