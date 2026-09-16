package com.example.bookmanager.web.book;

import com.example.bookmanager.web.dto.RakutenBookDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookSearchController {

    private final RakutenBookService rakutenBookService;

    public BookSearchController(RakutenBookService rakutenBookService){
        this.rakutenBookService = rakutenBookService;
    }

    @GetMapping("/api/search")
    public RakutenBookDto.BookItem searchBook(@RequestParam("isbn") String isbn){
        return rakutenBookService.searchByIsbn(isbn);
    }

}
