package com.enigmatic.applescrobbler.lastfm;


import com.last.fm.api.LfmParameters;
import com.last.fm.api.ScrobbleParameters;

public class TrackDataUtils {

    public static ScrobbleParameters prepareForScrobble(TrackData data) {
        ScrobbleParameters params = new ScrobbleParameters();
        params.put("track", data.getTitle());
        params.put("album", data.getAlbum());
        params.put("artist", data.getArtist());
        params.put("timestamp", String.valueOf(data.getStartTime().getTime()/1000));

        return params;
    }

    public static LfmParameters prepareForRequest(TrackData data) {
        LfmParameters params = new LfmParameters();
        params.put("track", data.getTitle());
        params.put("artist", data.getArtist());
        return params;
    }

    public static boolean validScrobble(TrackData data, long duration) {
//        return true;
        long totalPlayTime = data.getPlayTimes().stream().reduce((aLong, aLong2) -> aLong + aLong2).orElseGet(() -> 0L);
        return totalPlayTime > duration / 2 || totalPlayTime > 4 * 60000;
    }
}
