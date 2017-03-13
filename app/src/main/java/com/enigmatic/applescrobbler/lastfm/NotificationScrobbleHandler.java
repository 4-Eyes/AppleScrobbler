package com.enigmatic.applescrobbler.lastfm;


import android.util.Log;

import com.ag.lfm.LfmError;
import com.ag.lfm.LfmRequest;
import com.ag.lfm.api.LfmApi;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationScrobbleHandler {

    private TrackData currentTrack;
    private long currentTrackDuration;

    public NotificationScrobbleHandler() {

    }

    public void push(TrackData data) {
        boolean newTrack = false;
        if (currentTrack == null) {
            currentTrack = data;
            newTrack = true;
        } else {
            if (currentTrack.sameTrack(data)) {
                currentTrack.mergeSame(data);
            } else {
                if (TrackDataUtils.validScrobble(currentTrack, currentTrackDuration)) {
                    LfmApi.track().scrobble(TrackDataUtils.prepareForScrobble(currentTrack))
                            .executeWithListener(new LfmRequest.LfmRequestListener() {
                                @Override
                                public void onComplete(JSONObject response) {
                                    Log.i("Scrobbling", "Successfully Scrobbled");
                                }

                                @Override
                                public void onError(LfmError error) {

                                }
                            });
                }
                currentTrack = data;
                newTrack = true;
            }
        }

        if (newTrack) {
            LfmApi.track().getInfo(TrackDataUtils.prepareForRequest(data))
                    .executeWithListener(new LfmRequest.LfmRequestListener() {
                        @Override
                        public void onComplete(JSONObject response) {
                            try {
                                currentTrackDuration = response.getJSONObject("track").getLong("duration");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(LfmError error) {

                        }
                    });
        }
    }
}
