package com.example.demo1.util;

public class Constants {

    /**
     * 通过id查找语句集
     */
    public static final String SQL_THEME_SET_FIND_BY_ID = "select id,name from sesgis_vv_theme_sets where id = :id";

    /**
     * 查询配置集列表
     */
    public static final String SQL_THEME_SET_FIND_ALL = "select id, name from SESGIS_VV_THEME_SETS where valid = 1";

    /**
     * 根据id查找主题图id列表
     */
    public static final String SQL_FIND_THEME_IDS_BY_ID = "select stc.id from sesgis_vv_theme_sets svts left join sesgis_vv_theme_rels svtr on svtr.theme_set_id = svts.id left join sesgis_theme_configs stc on stc.id = svtr.theme_id where svts.valid = 1 and svtr.valid = 1 and stc.valid = 1 and svts.id = :id";

    /**
     * 根据id查找主题图名称列表
     */
    public static final String SQL_FIND_THEME_NAMES_BY_ID = "select stc.name from sesgis_vv_theme_sets svts left join sesgis_vv_theme_rels svtr on svtr.theme_set_id = svts.id left join sesgis_theme_configs stc on stc.id = svtr.theme_id where svts.valid = 1 and svtr.valid = 1 and stc.valid = 1 and svts.id = :id";

    /**
     * 根据id查找主题图名称列表
     */
    public static final String SQL_FIND_CUSTOM_THEMATIC_NAMES_BY_ID = "select sct.name from sesgis_vv_theme_sets svts left join sesgis_vv_theme_rels svtr on svtr.theme_set_id = svts.id left join sesgis_theme_configs stc on stc.id = svtr.theme_id left join ses_main_layer_cuss smlc on smlc.theme_id = stc.id left join sesgis_custom_thematics sct on sct.id = smlc.main_layer_custom_id where svts.valid = 1 and svtr.valid = 1 and stc.valid = 1 and smlc.valid = 1 and sct.valid = 1 and svts.id = :id";

    /**
     * 根据id查找专题图id列表
     */
    public static final String SQL_FIND_CUSTOM_THEMATIC_IDS_BY_ID = "select sct.id from sesgis_vv_theme_sets svts left join sesgis_vv_theme_rels svtr on svtr.theme_set_id = svts.id left join sesgis_theme_configs stc on stc.id = svtr.theme_id left join SES_MAIN_LAYER_CUSS smlc on smlc.THEME_ID = stc.id left join SESGIS_CUSTOM_THEMATICS sct on sct.id = smlc.MAIN_LAYER_CUSTOM_ID where svts.valid = 1 and svtr.valid = 1 and stc.valid = 1 and smlc.valid = 1 and sct.valid = 1 and svts.id = :id";

    /**
     * 视图表前缀
     */
    public static final String[] VIEW_PREFIX_ARRAY = new String[]{"AA_", "AB_", "BB_", "CC_"};

    /**
     * 地图版本
     */
    public static final String MAP_VERSION = "V1.0";

}
