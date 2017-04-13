package com.enigmatic.applescrobbler.lastfm;


import android.util.Log;

import com.last.fm.api.LfmApi;
import com.last.fm.api.LfmError;
import com.last.fm.api.LfmRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationScrobbleHandler {

    private TrackData currentTrack;
    private long currentTrackDuration;

    public void push(TrackData data) {
        boolean newTrack = false;
        if (currentTrack == null) {
            currentTrack = data;
            newTrack = true;
        } else {
            if (currentTrack.sameTrack(data)) {
                currentTrack.mergeSame(data);
            } else {
                Log.i("Scrobbling", "New track detected");
                currentTrack.finalisePlayTime();
                if (TrackDataUtils.validScrobble(currentTrack, currentTrackDuration)) {
                    Log.i("Scrobbling", "Scrobbling track");
                    LfmApi.track().scrobble(TrackDataUtils.prepareForScrobble(currentTrack))
                            .executeWithListener(new LfmRequest.LfmRequestListener() {
                                @Override
                                public void onComplete(JSONObject response) {
                                    Log.i("Scrobbling", "Successfully Scrobbled");
                                }

                                @Override
                                public void onError(LfmError error) {
                                    Log.e("Scrobbling", error.errorMessage);
                                    Log.e("Scrobbling", error.toString());
                                }
                            });
                }
                currentTrack = data;
                newTrack = true;
            }
        }

        if (newTrack) {
            Log.i("Scrobbling", "Loading new data for song " + data.getTitle());
            LfmApi.track().getInfo(TrackDataUtils.prepareForRequest(data))
                    .executeWithListener(new LfmRequest.LfmRequestListener() {
                        @Override
                        public void onComplete(JSONObject response) {
                            try {
                                currentTrackDuration = response.getJSONObject("track").getLong("duration");
                                Log.i("Scrobbling", "Successfully loaded data for song");
                            } catch (JSONException e) {
                                Log.e("Scrobbling", e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(LfmError error) {
                            Log.e("Scrobbling", error.errorMessage);
                        }
                    });
        }
    }
}
