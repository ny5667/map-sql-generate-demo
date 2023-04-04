package com.example.demo1.dao;

import com.example.demo1.dto.ThemeSetDto;
import com.example.demo1.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class ThemeSetDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * 通过id找到语句集
     * @param id
     * @return
     */
    public ThemeSetDto findById(Long id){
        Map<String, Long> id1 = Collections.singletonMap("id", id);
        return namedParameterJdbcTemplate.queryForObject(Constants.SQL_THEME_SET_FIND_BY_ID, id1, new BeanPropertyRowMapper<>(ThemeSetDto.class));
    }

    /**
     * 获取所有的导出集
     * @return
     */
    public List<ThemeSetDto> findAll(){
        return jdbcTemplate.query(Constants.SQL_THEME_SET_FIND_ALL, new BeanPropertyRowMapper<>(ThemeSetDto.class));
    }

    /**
     * 获取关联的主题图id集合
     * @param id
     * @return 主题图id列表
     */
    public List<Long> findThemeIdsById(Long id){
        Map<String, Long> id1 = Collections.singletonMap("id", id);
        return namedParameterJdbcTemplate.queryForList(Constants.SQL_FIND_THEME_IDS_BY_ID, id1, Long.class);
    }

    /**
     * 获取关联的主题图名称集合
     * @param id
     * @return
     */
    public List<String> findThemeNamesById(Long id){
        Map<String, Long> id1 = Collections.singletonMap("id", id);
        return namedParameterJdbcTemplate.queryForList(Constants.SQL_FIND_THEME_NAMES_BY_ID, id1, String.class);
    }

    /**
     * 获取关联的自定义专题图id集合
     * @param id
     * @return 自定义专题图id列表
     */
    public List<Long> findCustomIds(Long id){
        Map<String, Long> id1 = Collections.singletonMap("id", id);
        return namedParameterJdbcTemplate.queryForList(Constants.SQL_FIND_CUSTOM_THEMATIC_IDS_BY_ID,id1,Long.class);
    }

}
