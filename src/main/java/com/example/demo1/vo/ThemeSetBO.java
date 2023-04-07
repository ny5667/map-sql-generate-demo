package com.example.demo1.vo;

public class ThemeSetBO {

    /**
     * 导出集合名称
     */
    private String themeSetName;

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
    private String customThematicIds;

    /**
     * 专题图名称
     */
    private String customThematicNames;

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

    public ThemeSetBO(String themeSetName,String themeIds,String themeNames, String customIds,String customThematicNames, String init, String delete,String insertText) {
        this.themeSetName = themeSetName;
        this.themeIds = themeIds;
        this.themeNames = themeNames;
        this.customThematicIds = customIds;
        this.customThematicNames = customThematicNames;
        this.init = init;
        this.delete = delete;
        this.insertText = insertText;
    }

    public String getThemeSetName() {
        return themeSetName;
    }

    public void setThemeSetName(String themeSetName) {
        this.themeSetName = themeSetName;
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

    public String getCustomThematicIds() {
        return customThematicIds;
    }

    public void setCustomThematicIds(String customThematicIds) {
        this.customThematicIds = customThematicIds;
    }

    public String getCustomThematicNames() {
        return customThematicNames;
    }

    public void setCustomThematicNames(String customThematicNames) {
        this.customThematicNames = customThematicNames;
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
