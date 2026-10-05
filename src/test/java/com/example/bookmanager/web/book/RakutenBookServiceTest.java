package com.example.bookmanager.web.book;

import com.example.bookmanager.web.dto.RakutenBookDto;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RakutenBookServiceTest {

    private HttpClient httpClientMock;
    private HttpResponse<String> httpResponseMock;
    private RakutenBookService service;

    @BeforeEach
    void setUp(){
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = mock(HttpResponse.class);

        service = new RakutenBookService(
                "dummyAppId",
                "dummyAccessKey",
                httpClientMock,
                new ObjectMapper()
        );
    }

    @Nested
    @DisplayName("SearchByIsbnメソッドのテスト")
    class SearchByIsbnTest{

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void testSearchByIsbn_NullOrBlank_ReturnNull(String invalidIsbn){
            RakutenBookDto.BookItem result = service.searchByIsbn(invalidIsbn);

            assertNull(result);
            verifyNoInteractions(httpClientMock);
        }

        @Test
        void testSearchByIsbn_Success() throws Exception {
            String mockJsonResponse = """
                    {
                     "Items":[
                      {
                       "Item":{
                        "title": "テスト書籍",
                        "isbn": "1234567891234"
                        }
                       }
                      ]
                    }
                    """;
            when(httpResponseMock.statusCode()).thenReturn(200);
            when(httpResponseMock.body()).thenReturn(mockJsonResponse);
            when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                    .thenReturn(httpResponseMock);

            RakutenBookDto.BookItem result = service.searchByIsbn("123-456789123");

            assertNotNull(result);
        }

        @Test
        void testSearchByIsbn_HttpError_ReturnsNull() throws Exception{
            when(httpResponseMock.statusCode()).thenReturn(404);
            when(httpResponseMock.body()).thenReturn("NOT FOUND");
            when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                    .thenReturn(httpResponseMock);

            RakutenBookDto.BookItem result = service.searchByIsbn("1234567891234");

            assertNull(result);
        }

        @Test
        void testSearchByIsbn_Exception_ReturnsNull() throws Exception{
            when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                    .thenThrow(new RuntimeException("ネットワークエラー"));

            RakutenBookDto.BookItem result = service.searchByIsbn("1234567891234");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("SearchByKeywordメソッドのテスト")
    class SearchByKeywordTest{

        @Test
        @DisplayName("titleがnullの場合、nullを返す")
        void testSearchByKeyword_whenTitleIsNull_ReturnNull(){
            RakutenBookDto.BookItem result = service.searchByKeyword(null, "著者");

            assertNull(result);
            verifyNoInteractions(httpClientMock);
        }

        @Test
        @DisplayName("titleが空文字または空白の場合、nullを返す")
        void testSearchByKeyword_whenTitleIsBlank_ReturnNull(){
            assertNull(service.searchByKeyword("", "著者"));
            assertNull(service.searchByKeyword("　", "著者"));

            verifyNoInteractions(httpClientMock);
        }

        @Test
        @DisplayName("正常系：APIから該当するデータを取得し、返却する")
        void searchByKeyword_Success() throws Exception{
            String dummyJson = """
                    {
                     "Items":[
                      {
                       "Item":{
                        "title": "テスト書籍",
                        "author": "テスト一郎"
                        }
                       }
                      ]
                    }
                    """;

            when(httpResponseMock.statusCode()).thenReturn(200);
            when(httpResponseMock.body()).thenReturn(dummyJson);
            when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponseMock);

            RakutenBookDto.BookItem result = service.searchByKeyword("テスト書籍", "テスト一郎");

            assertNotNull(result);
            verify(httpClientMock, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        }

        @Test
        @DisplayName("ステータスコードが200以外の場合nullを返す")
        void testSearchByKeyword_HttpError_ReturnsNull() throws Exception{
            when(httpResponseMock.statusCode()).thenReturn(400);
            when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(httpResponseMock);

            RakutenBookDto.BookItem result = service.searchByKeyword("テスト書籍", "テスト一郎");

            assertNull(result);
        }

        @Test
        @DisplayName("Exceptionが発生した場合、nullを返す")
        void testSearchByKeyword_Exception_ReturnsNull() throws Exception{
            when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new RuntimeException("通信エラー"));

            RakutenBookDto.BookItem result = service.searchByKeyword("テスト書籍", "テスト一郎");

            assertNull(result);
        }

    }
}