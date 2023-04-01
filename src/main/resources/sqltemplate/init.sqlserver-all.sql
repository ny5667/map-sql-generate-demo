/*自定义专题图模板*/
/*1.自定义专题图模板-基本信息*/
DROP VIEW IF EXISTS "AA_SESGIS_CUSTOM_THEMATICS";
CREATE VIEW "AA_SESGIS_CUSTOM_THEMATICS" AS (SELECT * FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1);

/*2.自定义专题图模板-图层属性服务获取*/
DROP VIEW IF EXISTS "AA_SESGIS_LAYER_PROPERTIES";
CREATE VIEW "AA_SESGIS_LAYER_PROPERTIES" AS (SELECT * FROM SESGIS_LAYER_PROPERTIES WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*3.自定义专题图模板-点击列表数据展示*/
DROP VIEW IF EXISTS "AA_SESGIS_LIST_DATA_SHOWS";
CREATE VIEW "AA_SESGIS_LIST_DATA_SHOWS" AS (SELECT * FROM SESGIS_LIST_DATA_SHOWS WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*4.自定义专题图模板-点击工具栏按钮设置*/
DROP VIEW IF EXISTS "AA_SESGIS_CLICK_TOOLS";
CREATE VIEW "AA_SESGIS_CLICK_TOOLS" AS (SELECT * FROM SESGIS_CLICK_TOOLS WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*5.自定义专题图模板-符号配置*/
DROP VIEW IF EXISTS "AA_SESGIS_SYMBOL_RULESS";
CREATE VIEW "AA_SESGIS_SYMBOL_RULESS" AS (SELECT * FROM SESGIS_SYMBOL_RULESS WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*6.自定义专题图模板-扩展功能按钮*/
DROP VIEW IF EXISTS "AA_SESGIS_EXTENDED_BUTTONS";
CREATE VIEW "AA_SESGIS_EXTENDED_BUTTONS" AS (SELECT * FROM SESGIS_EXTENDED_BUTTONS WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*7.自定义专题图模板-其他扩展配置*/
DROP VIEW IF EXISTS "AA_SESGIS_CUSTOM_EXTENDS";
CREATE VIEW "AA_SESGIS_CUSTOM_EXTENDS" AS (SELECT * FROM SESGIS_CUSTOM_EXTENDS WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*8.自定义专题图模板-专题图插件*/
DROP VIEW IF EXISTS "AA_SESGIS_CUSTOM_PLUGINS";
CREATE VIEW "AA_SESGIS_CUSTOM_PLUGINS" AS (SELECT * FROM SESGIS_CUSTOM_PLUGINS WHERE BASIC_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1 AND PLUGIN_ID IN (SELECT ID FROM SESGIS_SCREEN_PLUGINSS WHERE VALID = 1));

/*9.自定义专题图模板-自定义专题图引导*/
DROP VIEW IF EXISTS "AA_SESGISCONFIG_CUSTOM_GUIDES";
CREATE VIEW "AA_SESGISCONFIG_CUSTOM_GUIDES" AS (SELECT * FROM SESGISCONFIG_CUSTOM_GUIDES WHERE BASE_INFO_ID IN (SELECT ID FROM SESGIS_CUSTOM_THEMATICS WHERE VALID = 1) AND VALID = 1);

/*主题配置模板*/
/*1.主题配置模板-主题信息*/
DROP VIEW IF EXISTS "AA_SESGIS_THEME_CONFIGS";
CREATE VIEW "AA_SESGIS_THEME_CONFIGS" AS (SELECT * FROM SESGIS_THEME_CONFIGS WHERE VALID = 1 and THEME_TYPE = 'SESGISConfig_customLayerType/001');

/*2.主题配置模板-主图层自定义专题图*/
DROP VIEW IF EXISTS "AA_SES_MAIN_LAYER_CUSS";
CREATE VIEW "AA_SES_MAIN_LAYER_CUSS" AS (SELECT * FROM SES_MAIN_LAYER_CUSS WHERE VALID = 1 AND THEME_ID IN (SELECT ID FROM SESGIS_THEME_CONFIGS WHERE VALID = 1 and THEME_TYPE = 'SESGISConfig_customLayerType/001'));

/*3.主题配置模板-专题底图*/
DROP VIEW IF EXISTS "AA_SES_BASE_MAP_LAYERSS";
CREATE VIEW "AA_SES_BASE_MAP_LAYERSS" AS (SELECT * FROM SES_BASE_MAP_LAYERSS WHERE VALID = 1 AND THEME_ID IN (SELECT ID FROM SESGIS_THEME_CONFIGS WHERE VALID = 1 and THEME_TYPE = 'SESGISConfig_customLayerType/001'));

/*4.主题配置模板-全局插件*/
UPDATE SESGIS_CONFIG_PLUGIS SET VALID = 0 WHERE VALID = 1 AND PLUGINS IN (SELECT ID FROM SESGIS_CONFIG_PLUGINSS WHERE VALID = 0);
DROP VIEW IF EXISTS "AA_SESGIS_CONFIG_PLUGIS";
CREATE VIEW "AA_SESGIS_CONFIG_PLUGIS" AS (SELECT * FROM SESGIS_CONFIG_PLUGIS WHERE VALID = 1 AND THEME_ID IN (SELECT ID FROM SESGIS_THEME_CONFIGS WHERE VALID = 1 and THEME_TYPE = 'SESGISConfig_customLayerType/001') AND PLUGINS IN (SELECT ID FROM SESGIS_CONFIG_PLUGINSS WHERE VALID = 1));

/*5.主题配置模板-全局插件配置*/
UPDATE SESGIS_THEME_PLUGINSS SET VALID = 0 WHERE VALID = 1 AND PLUGIN_ID IN (SELECT ID FROM SESGIS_CONFIG_PLUGINSS WHERE VALID = 0);
DROP VIEW IF EXISTS "AA_SESGIS_THEME_PLUGINSS";
CREATE VIEW "AA_SESGIS_THEME_PLUGINSS" AS (SELECT * FROM SESGIS_THEME_PLUGINSS WHERE VALID = 1 AND THEME_ID IN (SELECT ID FROM SESGIS_THEME_CONFIGS WHERE VALID = 1 and THEME_TYPE = 'SESGISConfig_customLayerType/001') AND PLUGIN_ID IN (SELECT ID FROM SESGIS_CONFIG_PLUGINSS WHERE VALID = 1));

/*6.主题配置模板-专题图插件配置*/
DROP VIEW IF EXISTS "AA_SESGIS_CUS_LAYER_PLUGINS";
CREATE VIEW "AA_SESGIS_CUS_LAYER_PLUGINS" AS (SELECT * FROM SESGIS_CUS_LAYER_PLUGINS WHERE VALID = 1 AND THEME_ID IN (SELECT ID FROM SESGIS_THEME_CONFIGS WHERE VALID = 1 and THEME_TYPE = 'SESGISConfig_customLayerType/001'));

/*全局插件*/
DROP VIEW IF EXISTS "AA_SESGIS_CONFIG_PLUGINSS";
CREATE VIEW "AA_SESGIS_CONFIG_PLUGINSS" AS (SELECT * FROM SESGIS_CONFIG_PLUGINSS WHERE VALID = 1);
DROP VIEW IF EXISTS "AA_SESGIS_CONFIG_PLUG_CONFS";
CREATE VIEW "AA_SESGIS_CONFIG_PLUG_CONFS" AS (SELECT * FROM SESGIS_CONFIG_PLUG_CONFS WHERE VALID = 1 AND PLUGIN_ID IN(SELECT ID FROM SESGIS_CONFIG_PLUGINSS WHERE VALID = 1));

/*专题图插件*/
DROP VIEW IF EXISTS "AA_SESGIS_SCREEN_PLUGINSS";
CREATE VIEW "AA_SESGIS_SCREEN_PLUGINSS" AS (SELECT * FROM SESGIS_SCREEN_PLUGINSS WHERE VALID = 1);
DROP VIEW IF EXISTS "AA_SESGIS_PLUGINS_DETAILSS";
CREATE VIEW "AA_SESGIS_PLUGINS_DETAILSS" AS (SELECT * FROM SESGIS_PLUGINS_DETAILSS WHERE PLUGIN_ID IN (SELECT ID FROM SESGIS_SCREEN_PLUGINSS WHERE VALID = 1) AND VALID = 1);

/*模块配置*/
DROP VIEW IF EXISTS "AA_SESGIS_MODULE_CONFIGS";
CREATE VIEW "AA_SESGIS_MODULE_CONFIGS" AS (SELECT * FROM SESGIS_MODULE_CONFIGS WHERE VALID = 1);

/*图标库*/
DROP VIEW IF EXISTS "AA_SESGIS_LIBRARY_TYPES";
CREATE VIEW "AA_SESGIS_LIBRARY_TYPES" AS (SELECT * FROM SESGIS_LIBRARY_TYPES WHERE VALID = 1);
DROP VIEW IF EXISTS "AA_SESGIS_ICON_LIBRARIES";
CREATE VIEW "AA_SESGIS_ICON_LIBRARIES" AS (SELECT * FROM SESGIS_ICON_LIBRARIES WHERE VALID = 1 AND ICON_TYPE IN(SELECT ID FROM SESGIS_LIBRARY_TYPES WHERE VALID = 1));

/* ~~ 基础相关配置 ~~ */
/*已发布图层*/
DROP VIEW IF EXISTS "AA_SESGIS_TRUE_LAYERSS";
CREATE VIEW "AA_SESGIS_TRUE_LAYERSS" AS (SELECT * FROM SESGIS_TRUE_LAYERSS WHERE VALID = 1 AND DATA_RELEASE_ID IN (SELECT ID FROM SESGIS_DATA_RELEASES WHERE VALID = 1));

/*数据源发布*/
DROP VIEW IF EXISTS "AA_SESGIS_DATA_RELEASES";
CREATE VIEW "AA_SESGIS_DATA_RELEASES" AS (SELECT * FROM SESGIS_DATA_RELEASES WHERE VALID = 1);

/*全局配置*/
/*全局配置-基础信息设置*/
DROP VIEW IF EXISTS "AA_SESGIS_BASIC_INFO_SETS";
CREATE VIEW "AA_SESGIS_BASIC_INFO_SETS" AS (SELECT * FROM SESGIS_BASIC_INFO_SETS WHERE VALID = 1);

/*全局配置 - 首页信息配置*/
DROP VIEW IF EXISTS "AA_SESGISCONFIG_HOME_PAGES";
CREATE VIEW "AA_SESGISCONFIG_HOME_PAGES" AS (SELECT * FROM SESGISCONFIG_HOME_PAGES WHERE VALID = 1 AND BASIC_INFO_SET_ID IN (SELECT ID FROM SESGIS_BASIC_INFO_SETS WHERE VALID = 1));

/*全局配置-基础底图配置*/
DROP VIEW IF EXISTS "AA_SESGIS_BASE_LAYERSS";
CREATE VIEW "AA_SESGIS_BASE_LAYERSS" AS (SELECT * FROM SESGIS_BASE_LAYERSS WHERE VALID = 1 AND BASIC_INFO_SET_ID IN (SELECT ID FROM SESGIS_BASIC_INFO_SETS WHERE VALID = 1));

/*全局配置-热门视野配置*/
DROP VIEW IF EXISTS "AA_SESGIS_HOT_VIEWS";
CREATE VIEW "AA_SESGIS_HOT_VIEWS" AS (SELECT * FROM SESGIS_HOT_VIEWS WHERE VALID = 1 AND BASIC_INFO_SET_ID IN (SELECT ID FROM SESGIS_BASIC_INFO_SETS WHERE VALID = 1));

/*全局配置-路网数据配置*/
DROP VIEW IF EXISTS "AA_SESGISCONFIG_ROADNETS";
CREATE VIEW "AA_SESGISCONFIG_ROADNETS" AS (SELECT * FROM SESGISCONFIG_ROADNETS WHERE VALID = 1 AND BASIC_INFO_SET_ID IN (SELECT ID FROM SESGIS_BASIC_INFO_SETS WHERE VALID = 1));

/*全局配置 - 其他配置设置*/
DROP VIEW IF EXISTS "AA_SESGIS_OTHER_CONFIGS";
CREATE VIEW "AA_SESGIS_OTHER_CONFIGS" AS (SELECT * FROM SESGIS_OTHER_CONFIGS WHERE VALID = 1 AND BASIC_INFO_SET_ID IN (SELECT ID FROM SESGIS_BASIC_INFO_SETS WHERE VALID = 1));

/*全局配置插件*/
DROP VIEW IF EXISTS "AA_SESGIS_GLOBAL_PLUGINSS";
CREATE VIEW "AA_SESGIS_GLOBAL_PLUGINSS" AS (SELECT * FROM SESGIS_GLOBAL_PLUGINSS WHERE VALID = 1);
DROP VIEW IF EXISTS "AA_SESGIS_GLOBAL_PLU_ITEMS";
CREATE VIEW "AA_SESGIS_GLOBAL_PLU_ITEMS" AS (SELECT * FROM SESGIS_GLOBAL_PLU_ITEMS WHERE GLOBAL_PLUGINS IN (SELECT ID FROM SESGIS_GLOBAL_PLUGINSS WHERE VALID = 1) AND VALID = 1);

/*报警声音*/
DROP VIEW IF EXISTS "AA_SESGIS_ALARM_SOUNDS";
CREATE VIEW "AA_SESGIS_ALARM_SOUNDS" AS (SELECT * FROM SESGIS_ALARM_SOUNDS WHERE VALID = 1);

/*主题语句*/
DROP VIEW IF EXISTS "AA_SESGIS_VV_THEME_SETS";
CREATE VIEW "AA_SESGIS_VV_THEME_SETS" AS (SELECT * FROM SESGIS_VV_THEME_SETS WHERE VALID = 1);
DROP VIEW IF EXISTS "AA_SESGIS_VV_THEME_RELS";
CREATE VIEW "AA_SESGIS_VV_THEME_RELS" AS (SELECT * FROM SESGIS_VV_THEME_RELS WHERE VALID = 1 AND THEME_SET_ID IN(SELECT ID FROM SESGIS_VV_THEME_SETS WHERE VALID = 1));

/*地图系统编码*/
DROP VIEW IF EXISTS "AB_sys_code";
CREATE VIEW "AB_sys_code" AS (SELECT * FROM sys_code WHERE entity_code = 'BASE_CONFIG_LAYERS' and valid = 1);