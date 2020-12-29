package org.jayield.lastfm;

public class TracksDto {
    private final Track[] track;

    public TracksDto(Track[] track) {
        this.track = track;
    }

    public Track[] getTrack() {
        return track;
    }
}
