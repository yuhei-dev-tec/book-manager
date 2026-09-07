package com.example.bookmanager.web.issue;

import com.example.bookmanager.domain.issue.IssueEntity;
import com.example.bookmanager.domain.issue.IssueService;
import com.example.bookmanager.domain.user.UserAccount;
import com.example.bookmanager.domain.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ListIterator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;
    private final UserAccountRepository userRepository;

    @GetMapping
    public String showList(
            @RequestParam(name = "authorName", required = false) String authorName,
            @RequestParam(name = "rating", required = false) Integer rating,
            @AuthenticationPrincipal UserDetails userDetails,
        Model model){

        UserAccount user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        List<IssueEntity> issueList =issueService.search(authorName, rating, user);

        model.addAttribute("issueList", issueList);
        model.addAttribute("searchAuthorName", authorName);
        model.addAttribute("searchRating", rating);
        return "issues/list";
    }

    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute IssueForm form){
        return "issues/creationForm";
    }

    @PostMapping
    public String create(@Validated IssueForm form, BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetails userDetails, Model model){
        if (bindingResult.hasErrors()){
            return showCreationForm(form);
        }
        UserAccount user = userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow();

        issueService.create(form.getBookTitle(), form.getAuthorName(), form.getRating(), user);
        return "redirect:/issues";
    }

    @GetMapping("/{issueId}")
    public String showDetail(@PathVariable("issueId") long issueId, Model model){
        model.addAttribute("issue", issueService.findById(issueId));
        return "issues/detail";
    }

}
