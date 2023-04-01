package com.example.demo1.vo;

public class ThemeSetBO {

    /**
     * 主题图创建表语句
     */
    private String themeIds;

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

    public ThemeSetBO(String themeIds, String customIds, String init, String delete) {
        this.themeIds = themeIds;
        this.customIds = customIds;
        this.init = init;
        this.delete = delete;
    }

    public String getThemeIds() {
        return themeIds;
    }

    public void setThemeIds(String themeIds) {
        this.themeIds = themeIds;
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
}
