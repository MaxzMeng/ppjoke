package me.maxandroid.ppjoke.exoplayer;

import android.view.ViewGroup;

public interface IPlayTarget {
    ViewGroup getOwner();

    void onActive();

    void inActive();

    boolean isPlaying();
}
