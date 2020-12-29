/*
 * Copyright (c) 2020, Fernando Miguel Carvalho, mcarvalho@cc.isel.ipl.pt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jayield.lastfm;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.*;

public class LastfmWebApi {
    private static final String LASTFM_API_KEY = "038cde478fb0eff567330587e8e981a4";
    private static final String LASTFM_HOST = "http://ws.audioscrobbler.com/2.0/";
    private static final String LASTFM_TOPTRACKS = LASTFM_HOST
                                                    + "?method=artist.gettoptracks&artist=%s&format=json&limit=%s&api_key="
                                                    + LASTFM_API_KEY;

    static final HttpClient httpClient = HttpClient.newHttpClient();
    static final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    static final Gson gson = new Gson();

    static HttpRequest request(String url) {
         return requestBuilder.uri(URI.create(url)).build();
    }

    public static CompletableFuture<Track[]> topTracks(String artist, int limit) {
        String url = String.format(LASTFM_TOPTRACKS, encode(artist, UTF_8), limit);
        return httpClient
            .sendAsync(request(url), HttpResponse.BodyHandlers.ofString()) // 1 - Fetch the url
            .thenApply(HttpResponse::body)
            .thenApply(body -> gson.fromJson(body, TopTracksDto.class))
            .thenApply(dto -> dto.getToptracks().getTrack());
    }
}
