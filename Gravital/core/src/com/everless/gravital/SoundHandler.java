package com.everless.gravital;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jordan on 2/24/2016.
 * Handles all sound effects
 */
public class SoundHandler {
    private boolean soundOn = true;
    public Array<Sound> sounds = new Array<>();
    private Music song1, song2;
    private Music loading;
    private Sound loaded, portalSuccess;
    private boolean assetsLoaded = false;

    SoundHandler() {}

    protected void loadInitialAssets() {
        //loading = Gdx.audio.newMusic(Gdx.files.internal("audio/sfx/riser.mp3"));
        loaded = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/door_open.mp3"));
        //loading.setVolume(.7f);
    }

    protected void playLoading(){
        if (soundOn && !loading.isPlaying()) {
            loading.play();
        }
    }

    protected void stopLoading() {
        loading.stop();
    }

    protected void playLoaded() {
        if (soundOn) {
            loaded.play(.5f);
        }
    }

    protected void playPortalSuccess() {
        if (soundOn) {
            portalSuccess.stop();
            portalSuccess.play(.6f);
        }
    }

    protected void loadGameAssets() {
        //TODO: Add sounds through assetManager?

        if (!assetsLoaded) {
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_01.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_02.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_03.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_04.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_05.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_09.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_10.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_11.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_13.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_14.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_17.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_18.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_20.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_21.wav")));
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("audio/sfx/glass/glass_24.wav")));

            portalSuccess = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/43047__noisecollector__teleport.wav"));

            //song1 = Gdx.audio.newMusic(Gdx.files.internal("audio/music/mind_over_matter.mp3"));
            song2 = Gdx.audio.newMusic(Gdx.files.internal("audio/music/gravital_ambience.mp3"));
            song2.setLooping(true);
            /*if (soundOn) {
                song2.play();
            }*/

            assetsLoaded = true;
        }
    }

    protected void play() {
        if (soundOn && sounds.size > 0) {
            int i = Utility.randInt(0, sounds.size - 1);
            float volume = Utility.randInt(60, 100);
            volume /= 100;
            sounds.get(i).play(volume);
        }
    }

    protected void soundOn() {
        soundOn = true;
        if (song2 != null && !song2.isPlaying()){
            song2.play();
        }
    }

    protected void soundOff() {
        soundOn = false;
        if (song2 != null && song2.isPlaying()) {
            song2.pause();
        }
    }

    protected boolean getSoundOn() {
        return soundOn;
    }

    private void dispose() {
        //sound.dispose();
    }
}