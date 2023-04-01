/*========== 自定义专题图-删除模板 ==========*/
/*1.基本信息*/
DELETE FROM SESGIS_CUSTOM_THEMATICS WHERE ID IN (${CUSTOM_THEMATIC_IDS});
/*模块配置*/
DELETE FROM SESGIS_MODULE_CONFIGS WHERE ID IN(SELECT GET_MODULE FROM SESGIS_CUSTOM_THEMATICS WHERE ID IN (${CUSTOM_THEMATIC_IDS}));

/*2.图层属性服务获取*/
DELETE FROM SESGIS_LAYER_PROPERTIES WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});

/*3.点击列表数据展示*/
DELETE FROM SESGIS_LIST_DATA_SHOWS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});

/*4.点击工具栏按钮设置*/
DELETE FROM SESGIS_CLICK_TOOLS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});

/*5.符号配置*/
DELETE FROM SESGIS_SYMBOL_RULESS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});

/*6.扩展功能按钮*/
DELETE FROM SESGIS_EXTENDED_BUTTONS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});

/*7.其他扩展配置*/
DELETE FROM SESGIS_CUSTOM_EXTENDS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});

/*8.专题图插件*/
DELETE FROM SESGIS_CUSTOM_PLUGINS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS});
/*专题图插件表(专题图插件配置 SESGIS_CUS_LAYER_PLUGINS.PLUGIN_ID)*/
DELETE FROM SESGIS_SCREEN_PLUGINSS WHERE ID IN (SELECT PLUGIN_ID FROM SESGIS_CUSTOM_PLUGINS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS})) AND VALID = 1;
DELETE FROM SESGIS_PLUGINS_DETAILSS WHERE PLUGIN_ID IN (SELECT PLUGIN_ID FROM SESGIS_CUSTOM_PLUGINS WHERE BASIC_INFO_ID IN (${CUSTOM_THEMATIC_IDS})) AND VALID = 1;

/*========== 主题配置-删除模板 ==========*/
/*1.主题信息*/
DELETE FROM SESGIS_THEME_CONFIGS WHERE ID IN (${THEME_CONFIG_IDS});

/*2.主图层自定义专题图*/
DELETE FROM SES_MAIN_LAYER_CUSS WHERE THEME_ID IN (${THEME_CONFIG_IDS});

/*3.专题底图*/
DELETE FROM SES_BASE_MAP_LAYERSS WHERE THEME_ID IN (${THEME_CONFIG_IDS});

/*4.全局插件*/
DELETE FROM SESGIS_CONFIG_PLUGIS WHERE THEME_ID IN (${THEME_CONFIG_IDS});
/*全局插件（全局插件配置 SESGIS_CONFIG_PLUGIS.PLUGINS）*/
DELETE FROM SESGIS_CONFIG_PLUGINSS WHERE ID IN (SELECT PLUGINS FROM SESGIS_CONFIG_PLUGIS WHERE THEME_ID IN (${THEME_CONFIG_IDS}));
DELETE FROM SESGIS_CONFIG_PLUG_CONFS WHERE PLUGIN_ID IN (SELECT PLUGINS FROM SESGIS_CONFIG_PLUGIS WHERE THEME_ID IN (${THEME_CONFIG_IDS}));

/*5.全局插件配置*/
DELETE FROM SESGIS_THEME_PLUGINSS WHERE THEME_ID IN (${THEME_CONFIG_IDS});
/*全局插件（全局插件配置 SESGIS_THEME_PLUGINSS.PLUGIN_ID）*/
DELETE FROM SESGIS_CONFIG_PLUGINSS WHERE ID IN (SELECT PLUGIN_ID FROM SESGIS_THEME_PLUGINSS WHERE THEME_ID IN (${THEME_CONFIG_IDS}));
DELETE FROM SESGIS_CONFIG_PLUG_CONFS WHERE PLUGIN_ID IN (SELECT PLUGIN_ID FROM SESGIS_THEME_PLUGINSS WHERE THEME_ID IN (${THEME_CONFIG_IDS}));

/*6.专题图插件配置*/
DELETE FROM SESGIS_CUS_LAYER_PLUGINS WHERE THEME_ID IN (${THEME_CONFIG_IDS});