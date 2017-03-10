package com.enigmatic.applescrobbler.lastfm;

import com.ag.lfm.LfmParameters;
import com.ag.lfm.ScrobbleParameters;

import java.util.Date;

public class TrackData {

    private String artist;
    private String title;
    private String album;
    private Date scrobbleTime;

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

    public Date getScrobbleTime() {
        return scrobbleTime;
    }

    public void setScrobbleTime(Date scrobbleTime) {
        this.scrobbleTime = scrobbleTime;
    }

    public ScrobbleParameters prepareForScrobble() {
        ScrobbleParameters params = new ScrobbleParameters();
        params.put("track", this.title);
        params.put("album", this.album);
        params.put("artist", this.artist);
        params.put("timestamp", String.valueOf(this.scrobbleTime.getTime()/1000));

        return params;
    }
}
