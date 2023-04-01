package com.example.demo1.web;

import com.example.demo1.persistence.model.ThemeSet;
import com.example.demo1.persistence.repo.MainLayerCustomRepository;
import com.example.demo1.persistence.repo.ThemeSetItemRepository;
import com.example.demo1.persistence.repo.ThemeSetRepository;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.vo.ThemeSetBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class ThemeSetPageController {

    Logger logger = LoggerFactory.getLogger(ThemeSetPageController.class);

    /**
     * sql结尾的;符号
     */
    private static final String SQL_COMMA = ";";

    @Autowired
    private ThemeSetService themeSetService;

    @Autowired
    private ThemeSetRepository themeSetRepository;

    @Autowired
    private ThemeSetItemRepository themeSetItemRepository;

    @Autowired
    private MainLayerCustomRepository mainLayerCustomRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public String listPage(Model model) {
        Iterable<ThemeSet> sets = themeSetRepository.findAll();
        model.addAttribute("sets", sets);
        return "set/list";
    }

    @GetMapping("/edit/{id}")
    public String EditPage(Model model, @PathVariable Long id) throws IOException {
        ThemeSetBO boById = themeSetService.getBOById(id);
        executeSql(boById.getInit());
        model.addAttribute("themeIds", boById.getThemeIds());
        model.addAttribute("customIds", boById.getCustomIds());
        model.addAttribute("createText", boById.getInit());
        model.addAttribute("deleteText", boById.getDelete());
        model.addAttribute("id", id);
        return "set/edit";
    }

    /*--------------------------------------------------------------------公共方法------------------------------------------------------------------------------*/

    /**
     * 创建视图
     *
     * @param init_s
     */
    private void executeSql(String init_s) {
        String[] split = init_s.split("\n");
        String newsql = "";
        for (String sqlLine :
                split) {
            newsql += sqlLine;
            if (!endWithSqlComma(sqlLine)) {
                continue;
            }
            try {
                jdbcTemplate.execute(newsql);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 该行是否为;结尾
     *
     * @param sqlLine sql文件中一行文本
     * @return
     */
    private Boolean endWithSqlComma(String sqlLine) {
        String newSql = sqlLine;
        while (newSql != null && !newSql.isEmpty()) {
            String substring = newSql.substring(newSql.length() - 1);
            if (substring.trim().isEmpty()) {
                newSql = newSql.substring(newSql.length() - 1);
            } else if (SQL_COMMA.equals(substring)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


}
