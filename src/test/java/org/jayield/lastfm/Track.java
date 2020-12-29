package org.jayield.lastfm;

public class Track {
    private final String name;
    private final String url;
    private final int duration;

    public Track(String name, String url, int duration) {
        this.name = name;
        this.url = url;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }
}
