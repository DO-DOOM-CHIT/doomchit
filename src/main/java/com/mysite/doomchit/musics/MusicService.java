package com.mysite.doomchit.musics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MusicService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private final WebClient webClient = WebClient.builder().build();

    // 1. 토큰 발급
    public String getAccessToken() {
        return webClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> response.get("access_token").toString())
                .block();
    }

    // 2. 차트 데이터 가져오기 및 파싱
    // 2. 차트 데이터 가져오기 (Playlist ID 오류 방지를 위해 Search API로 변경)
    public List<Music> getSpotifyChart() {
        try {
            String token = getAccessToken();
            // 한국 노래(K-Pop) 위주로, 최신(2025~2026) 인기곡 50개를 가져오도록 검색어 수정
            // q=genre:k-pop year:2025-2026
            String searchUrl = "https://api.spotify.com/v1/search?q=genre:k-pop%20year:2025-2026&type=track&limit=50&market=KR";

            String response = webClient.get()
                    .uri(searchUrl)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseJsonToMusic(response);
        } catch (Exception e) {
            System.err.println("Spotify API Error: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    // 3. JSON 파싱 (Search API 구조에 맞게 수정)
    private List<Music> parseJsonToMusic(String json) {
        List<Music> musicList = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            // Search API 구조: tracks -> items
            JsonNode items = root.path("tracks").path("items");

            for (int i = 0; i < items.size(); i++) {
                JsonNode track = items.get(i);
                Music music = new Music();
                music.setTitle(track.path("name").asText());
                music.setArtist(track.path("artists").get(0).path("name").asText());
                music.setAlbumName(track.path("album").path("name").asText());
                // 이미지: 0번은 640x640, 1번은 300x300, 2번은 64x64 (보통)
                JsonNode images = track.path("album").path("images");
                if (images.size() > 0) {
                    music.setImageUrl(images.get(0).path("url").asText());
                } else {
                    music.setImageUrl(""); // 이미지가 없을 경우
                }
                music.setRank(i + 1);
                musicList.add(music);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicList;
    }
}