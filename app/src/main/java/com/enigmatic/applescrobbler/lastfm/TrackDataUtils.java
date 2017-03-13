package com.enigmatic.applescrobbler.lastfm;

import com.ag.lfm.LfmParameters;
import com.ag.lfm.ScrobbleParameters;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TrackDataUtils {

    public static ScrobbleParameters prepareForScrobble(TrackData data) {
        ScrobbleParameters params = new ScrobbleParameters();
        try {
            params.put("track", URLEncoder.encode(data.getTitle(), "UTF-8"));
            params.put("album", URLEncoder.encode(data.getAlbum(), "UTF-8"));
            params.put("artist", URLEncoder.encode(data.getArtist(), "UTF-8"));
            params.put("timestamp", URLEncoder.encode(String.valueOf(data.getStartTime().getTime()/1000), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
