package com.example.demo1.web;

import com.example.demo1.persistence.model.ThemeSet;
import com.example.demo1.persistence.repo.ThemeSetRepository;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.vo.SetPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/sets")
public class ThemeSetController {

    @Autowired
    private ThemeSetRepository themeSetRepository;

    @Autowired
    ThemeSetService themeSetService;

    @GetMapping
    public Iterable findAll() {
        Iterable<ThemeSet> all = themeSetRepository.findAll();
        return all;
    }

    @PostMapping(
            path = "/handle",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void handleSet(HttpServletResponse response, SetPostVO setPostVO) throws IOException {
        themeSetService.handleSet(response,setPostVO);

    }


}
