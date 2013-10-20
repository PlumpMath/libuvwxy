package de.uvwxy.audio;

import android.media.SoundPool;

import com.google.common.base.Preconditions;

public class AudioParameters {
	private AudioParameters backup;

	public float volumeLeft = 1f;
	public float volumeRight = 1f;
	public float speed = 1f;
	public int soundId = -1;
	public int streamId = -1;
	public int loop = -1;
	public int priority = 0;

	public void mute() {
		if (backup == null) {
			backup = new AudioParameters();
		}
		backup.volumeLeft = this.volumeLeft;
		backup.volumeRight = this.volumeRight;
		this.volumeLeft = 0;
		this.volumeRight = 0;
	}

	public void restore() {
		if (backup == null) {
			backup = new AudioParameters();
		}
		this.volumeLeft = backup.volumeLeft;
		this.volumeRight = backup.volumeRight;
	}

	public void playOn(SoundPool pool) {
		Preconditions.checkNotNull(pool);
		this.streamId = pool.play(soundId, volumeLeft, volumeRight, priority, loop, speed);
	}

	public void muteOn(SoundPool pool) {
		Preconditions.checkNotNull(pool);
		mute();
		pool.setVolume(streamId, volumeLeft, volumeRight);
	}

	public void restoreOn(SoundPool pool) {
		Preconditions.checkNotNull(pool);
		restore();
		pool.setVolume(streamId, volumeLeft, volumeRight);
	}

	public void setVolumeOn(SoundPool pool) {
		Preconditions.checkNotNull(pool);
		pool.setVolume(streamId, volumeLeft, volumeRight);
	}
}
