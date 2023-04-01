package com.example.demo1.persistence.model;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "SESGIS_THEME_CONFIGS")
public class ThemeConfig {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer valid;

    @OneToMany(mappedBy="id")
    private Set<CustomThematic> customThematics;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Set<CustomThematic> getCustomThematics() {
        return customThematics;
    }

    public void setCustomThematics(Set<CustomThematic> customThematics) {
        this.customThematics = customThematics;
    }

}
