package com.enigmatic.applescrobbler.lastfm;

import java.util.ArrayList;
import java.util.Date;

public class TrackData {

    public enum PlayingState {
        Playing,
        Paused,
        Unknown
    }

    private String artist;
    private String title;
    private String album;
    private Date startTime;
    private PlayingState currentState = PlayingState.Unknown;
    private ArrayList<Long> playTimes = new ArrayList<>();
    private Date lastStateChangedTime;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setContentType(String contentType) {
        switch (contentType) {
            case "Pause":
                currentState = PlayingState.Playing;
                break;
            case "Play":
                currentState = PlayingState.Paused;
                break;
            default:
                currentState = PlayingState.Unknown;
                break;
        }
        this.lastStateChangedTime = new Date(System.currentTimeMillis());
    }

    public PlayingState getState() {
        return this.currentState;
    }

    public ArrayList<Long> getPlayTimes() {
        return this.playTimes;
    }

    public void mergeSame(TrackData data) {
        if (this.getState().equals(data.getState())) return;
        if (this.currentState.equals(PlayingState.Playing) && data.currentState == PlayingState.Paused) {
            playTimes.add(data.getStartTime().getTime() - this.lastStateChangedTime.getTime());
        }
        this.currentState = data.currentState;
        lastStateChangedTime = new Date(System.currentTimeMillis());
    }

    public void finalisePlayTime() {
        if (currentState.equals(PlayingState.Playing)) {
            playTimes.add(System.currentTimeMillis() - lastStateChangedTime.getTime());
        }
    }

    public boolean sameTrack(TrackData other) {
        return this.title != null && this.artist != null && this.album != null &&
                other.title != null && other.artist != null && other.album != null &&
                this.title.equals(other.title) && this.artist.equals(other.artist) && this.album.equals(other.album);
    }
}
