package com.example.demo1.vo;

public class ThemeSetBO {

    /**
     * 主题图创建表语句
     */
    private String themeIds;

    /**
     * 主题图名称集合
     */
    private String themeNames;

    /**
     * 专题图创建表语句
     */
    private String customIds;

    /**
     * 创建视图语句
     */
    private String init;

    /**
     * 删除数据语句
     */
    private String delete;

    /**
     * 数据库中插入的脚本
     */
    private String insertText;

    public ThemeSetBO(String themeIds,String themeNames, String customIds, String init, String delete,String insertText) {
        this.themeIds = themeIds;
        this.themeNames = themeNames;
        this.customIds = customIds;
        this.init = init;
        this.delete = delete;
        this.insertText = insertText;
    }

    public String getThemeIds() {
        return themeIds;
    }

    public void setThemeIds(String themeIds) {
        this.themeIds = themeIds;
    }

    public String getThemeNames() {
        return themeNames;
    }

    public void setThemeNames(String themeNames) {
        this.themeNames = themeNames;
    }

    public String getCustomIds() {
        return customIds;
    }

    public void setCustomIds(String customIds) {
        this.customIds = customIds;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getInsertText() {
        return insertText;
    }

    public void setInsertText(String insertText) {
        this.insertText = insertText;
    }
}
