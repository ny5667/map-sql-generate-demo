package com.example.demo1.persistence.model;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "SESGIS_VV_THEME_SETS")
public class ThemeSet {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private String name;

    @Column
    private Integer valid;

    @OneToMany(mappedBy="themeSetId")
    private Set<ThemeSetItem> setItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
