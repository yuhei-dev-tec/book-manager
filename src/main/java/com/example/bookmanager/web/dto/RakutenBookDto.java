package com.example.bookmanager.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RakutenBookDto {
    @JsonProperty("Items")
    private List<ItemWrapper> items;

    public List<ItemWrapper> getItems(){
        return items;
    }

    public void setItems(List<ItemWrapper> items){
        this.items = items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemWrapper {
        @JsonProperty("Item")
        private BookItem item;

        public BookItem getItem() {
            return item;
        }

        public void setItem(BookItem item) {
            this.item = item;
        }
    }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BookItem {
            private String title;
            private String author;
            private String publisherName;
            private Integer itemPrice;
            private String largeImageUrl;
            private String isbn;
            private String itemCaption;

            @JsonProperty("itemUrl")
            private String itemUrl;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getAuthor() {
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getPublisherName() {
                return publisherName;
            }

            public void setPublisherName(String publisherName) {
                this.publisherName = publisherName;
            }

            public Integer getItemPrice() {
                return itemPrice;
            }

            public void setItemPrice(Integer itemPrice) {
                this.itemPrice = itemPrice;
            }

            public String getLargeImageUrl() {
                return largeImageUrl;
            }

            public void setLargeImageUrl(String largeImageUrl) {
                this.largeImageUrl = largeImageUrl;
            }

            public String getIsbn() {
                return isbn;
            }

            public void setIsbn(String isbn) {
                this.isbn = isbn;
            }

            public String getItemCaption() {
                return itemCaption;
            }

            public void setItemCaption(String itemCaption) {
                this.itemCaption = itemCaption;
            }

            public String getItemUrl() {
                return itemUrl;
            }

            public void setItemUrl(String itemUrl) {
                this.itemUrl = itemUrl;
            }
        }

}
