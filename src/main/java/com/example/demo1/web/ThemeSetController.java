package com.example.demo1.web;

import com.example.demo1.dao.ThemeSetDao;
import com.example.demo1.dto.ThemeSetDto;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.vo.SetPostVO;
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

    private final ThemeSetService themeSetService;

    private final ThemeSetDao themeSetDao;

    public ThemeSetController(ThemeSetService themeSetService, ThemeSetDao themeSetDao) {
        this.themeSetService = themeSetService;
        this.themeSetDao = themeSetDao;
    }

    @GetMapping
    public List<ThemeSetDto> findAll() {
        return themeSetDao.findAll();
    }

    @PostMapping(
            path = "/themeExport",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public void themeExport(HttpServletResponse response, SetPostVO setPostVO) throws IOException {
        themeSetService.themeExport(response, setPostVO);
    }

    @GetMapping(
            path = "/allExport")
    public void allExport(HttpServletResponse response) throws IOException {
        themeSetService.allExport(response);
    }

    @GetMapping(
            path = "/systemCodeExport")
    public void systemCodeExport(HttpServletResponse response) throws IOException {
        themeSetService.systemCodeExport(response);
    }

    @GetMapping(
            path = "/iconLibraryExport")
    public void iconLibraryExport(HttpServletResponse response) throws IOException {
        themeSetService.iconLibraryExport(response);
    }

}
