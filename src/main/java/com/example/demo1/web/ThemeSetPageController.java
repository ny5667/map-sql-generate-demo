package com.example.demo1.web;

import com.example.demo1.dao.ThemeSetDao;
import com.example.demo1.dto.ThemeSetDto;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.vo.ThemeSetBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

@Controller
public class ThemeSetPageController {

    Logger logger = LoggerFactory.getLogger(ThemeSetPageController.class);

    private final ThemeSetService themeSetService;

    private final ThemeSetDao themeSetDao;

    public ThemeSetPageController(ThemeSetService themeSetService, ThemeSetDao themeSetDao) {
        this.themeSetService = themeSetService;
        this.themeSetDao = themeSetDao;
    }

    @GetMapping("/theme/list")
    public String listPage(Model model) {
        List<ThemeSetDto> all = themeSetDao.findAll();
        model.addAttribute("sets", all);
        return "theme/list";
    }

    @GetMapping("/theme/edit/{id}")
    public String editPage(Model model, @PathVariable Long id) throws IOException {
        ThemeSetBO boById = themeSetService.getBOByIdAndInit(id);
        model.addAttribute("themeSetName", boById.getThemeSetName());
        model.addAttribute("themeIds", boById.getThemeIds());
        model.addAttribute("themeNames", boById.getThemeNames());
        model.addAttribute("customThematicIds", boById.getCustomThematicIds());
        model.addAttribute("customThematicNames", boById.getCustomThematicNames());
        model.addAttribute("createText", boById.getInit());
        model.addAttribute("insertText", boById.getInsertText());
        model.addAttribute("deleteText", boById.getDelete());
        model.addAttribute("id", id);
        return "theme/edit";
    }

    /*--------------------------------------------------------------------公共方法------------------------------------------------------------------------------*/


}
