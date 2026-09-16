package com.example.bookmanager.web.book;

import com.example.bookmanager.web.dto.RakutenBookDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class RakutenBookService {

    private final String applicationId;
    private final String accessKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RakutenBookService(@Value("${rakuten.api.application-id}") String applicationId,
                              @Value("${rakuten.api.access-key}") String accessKey) {
        this.applicationId = applicationId;
        this.accessKey = accessKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public RakutenBookDto.BookItem searchByIsbn(String isbn){
        if (isbn == null || isbn.isBlank()){
            return null;
        }

        String cleanIsbn = isbn.replace("-","").trim();

        String url = "https://openapi.rakuten.co.jp/services/api/BooksBook/Search/20170404"
                + "?applicationId=" + applicationId
                + "&accessKey=" + accessKey
                + "&format=json"
                + "&isbn=" + cleanIsbn;

        System.out.println("===送信URL:" + url);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Referer","http://example.com")
                    .header("Origin","http://example.com")
                    .header("User-Agent","Mozilla/5.0(Windows NT 10.0; Win64; x64)")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
               RakutenBookDto dto = objectMapper.readValue(response.body(),RakutenBookDto.class);
               if (dto != null && dto.getItems() != null && !dto.getItems().isEmpty()){
                   return dto.getItems().get(0).getItem();
               }
            } else {
                System.out.println("==エラーレスポンス: Status " + response.statusCode() + " /Body: " + response.body());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public RakutenBookDto.BookItem searchByKeyword(String title, String author){
        if (title == null || title.isBlank()){
            return null;
        }

        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String encodedAuthor = (author != null && !author.isBlank())
                ? URLEncoder.encode(author, StandardCharsets.UTF_8)
                : "";

        String url = "https://openapi.rakuten.co.jp/services/api/BooksBook/Search/20170404"
                + "?applicationId=" + applicationId
                + "&accessKey=" + accessKey
                + "&format=json"
                + "&title=" + encodedTitle
                + "&author" + encodedAuthor;


        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Referer","http://example.com")
                    .header("Origin","http://example.com")
                    .header("User-Agent","Mozilla/5.0(Windows NT 10.0; Win64; x64)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                RakutenBookDto dto = objectMapper.readValue(response.body(), RakutenBookDto.class);
                if (dto != null && dto.getItems() != null && !dto.getItems().isEmpty()){
                    return dto.getItems().get(0).getItem();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
