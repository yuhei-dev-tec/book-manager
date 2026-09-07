package com.example.bookmanager.web.issue;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class IssueForm {

    @NotBlank
    @Size(max = 256)
    private String bookTitle;

    @NotBlank
    @Size(max = 256)
    private String authorName;

    @NotNull(message = "評価を入力してください")
    @Min(1)
    @Max(5)
    private int rating;
}
