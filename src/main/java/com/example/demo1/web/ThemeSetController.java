package com.example.demo1.web;

import com.example.demo1.dao.ThemeSetDao;
import com.example.demo1.dto.ThemeSetDto;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.vo.SetPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/sets")
public class ThemeSetController {

    @Autowired
    ThemeSetService themeSetService;

    @Autowired
    ThemeSetDao themeSetDao;

    @GetMapping
    public List<ThemeSetDto> findAll() {
        return themeSetDao.findAll();
    }

    @PostMapping(
            path = "/handle",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void handleSet(HttpServletResponse response, SetPostVO setPostVO) throws IOException {
        themeSetService.handleSet(response, setPostVO);

    }


}
