package com.matchgetit.backend.controller;

import com.matchgetit.backend.dto.InquiryCommentDTO;
import com.matchgetit.backend.dto.InquiryDTO;
import com.matchgetit.backend.dto.SearchInquiryDTO;
import com.matchgetit.backend.service.InquiryService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/matchGetIt/admin")
@RequiredArgsConstructor
public class AdminInquiryController {
    private final InquiryService inquiryService;

    private final String path = "admin/pages/Inquiry/";
    private final String alertViewPath = "admin/components/Utils/alert";

    @PostConstruct
    public void createInquiries() {
        inquiryService.createInquiries();
    }


    @GetMapping({"/inquiryBoard", "/inquiryBoard/{page}"})
    public String board(Model model, @PathVariable("page") Optional<Integer> page, SearchInquiryDTO searchInquiryDTO) {
        Integer temp = searchInquiryDTO.getPageSize();
        int pageSize = temp == null ? 5 : temp;

        Pageable pageable = PageRequest.of(page.orElse(0), pageSize);
        Page<InquiryDTO> inquiryList = inquiryService.getPageableInquiryList(searchInquiryDTO, pageable);
        System.out.println(inquiryList.getContent().get(0));

//        List<InquiryDTO> inquiryList = inquiryService.getInquiryList();
        model.addAttribute("inquiryList", inquiryList);
        model.addAttribute("currPageNum", pageable.getPageNumber());
        model.addAttribute("searchInquiryDTO", searchInquiryDTO);
        return path + "InquiryBoard";
    }

    @DeleteMapping("/inquiryBoard")
    @ResponseBody
    public ResponseEntity<String> deleteInquiry(@RequestParam(value = "arr[]") String[] inquiries) {
        for (String inquiryId: inquiries) {
            inquiryService.deleteInquiry(Long.valueOf(inquiryId));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @GetMapping("/inquiry/{inquiryId}")
    public String view(Model model, @PathVariable Long inquiryId) {
        try {
            InquiryDTO inquiryDTO = inquiryService.getInquiry(inquiryId);
            model.addAttribute("post", inquiryDTO);
        }
        catch (EntityNotFoundException e) {
            model.addAttribute("post", new InquiryDTO());
        }
        model.addAttribute("commentDTO", new InquiryCommentDTO());
        return path + "InquiryPost";
    }

    @GetMapping("/deleteInquiry/{inquiryId}")
    public String deleteInquiry(@PathVariable Long inquiryId) {
//        System.out.println(">>>>>>>>>>>>"+inquiryId);
        inquiryService.deleteInquiry(inquiryId);
        return "redirect:/matchGetIt/admin/inquiryBoard";
    }



    @PostMapping("/inquiry/{inquiryId}")
    public String writeComment(InquiryCommentDTO commentDTO, @PathVariable Long inquiryId) {
//        System.out.println(">>>>>>>>>>>>"+commentDTO);
        inquiryService.writeComment(commentDTO, inquiryId);
        return "redirect:/matchGetIt/admin/inquiry/"+inquiryId;
    }

    @PatchMapping("/inquiry/editComment")
    public @ResponseBody ResponseEntity<String> editComment(@RequestParam String content, @RequestParam Long commentId) {
//        System.out.println(">>>>>>>>>>>>"+commentDTO);
//        System.out.println(content);
        inquiryService.updateComment(content, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/inquiry/deleteComment")
    public @ResponseBody ResponseEntity<String> deleteComment(@RequestParam Long commentId) {
        System.out.println(">>>>>>>>>>>>"+commentId);
        inquiryService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test")
    public String test() {
        return "inquiry/temp5";
    }

}
