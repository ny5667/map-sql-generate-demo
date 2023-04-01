package com.example.demo1.persistence.model;

import javax.persistence.*;

@Entity(name = "SESGIS_VV_THEME_RELS")
public class ThemeSetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "theme_id", referencedColumnName = "id")
    private ThemeConfig themeId;

    @Column
    private Long themeSetId;

    @Column
    private Integer valid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ThemeConfig getThemeId() {
        return themeId;
    }

    public void setThemeId(ThemeConfig themeId) {
        this.themeId = themeId;
    }

    public Long getThemeSetId() {
        return themeSetId;
    }

    public void setThemeSetId(Long themeSetId) {
        this.themeSetId = themeSetId;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }
}
