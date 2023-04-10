package com.example.demo1.service.impl;

import com.example.demo1.dao.ThemeSetDao;
import com.example.demo1.dto.ThemeSetDto;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.util.Constants;
import com.example.demo1.vo.*;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ThemeSetServiceImpl implements ThemeSetService {

    Logger logger = LoggerFactory.getLogger(ThemeSetServiceImpl.class);

    /**
     * 自定义专题图的占位符
     */
    private static final String CUSTOM_THEMATIC_IDS = "${CUSTOM_THEMATIC_IDS}";

    /**
     * 主题图的占位符
     */
    private static final String THEME_CONFIG_IDS = "${THEME_CONFIG_IDS}";

    /**
     * 初始化主题文件路径
     */
    private static final String INIT_SQLSERVER_THEME_FILE_PATH = "/sqltemplate/init-sqlserver-theme.sql";

    /**
     * 主题图全部初始化
     */
    private static final String INIT_SQLSERVER_ALL_FILE_PATH = "/sqltemplate/init.sqlserver-all.sql";

    /**
     * 删除主题文件路径
     */
    private static final String DELETE_SQLSERVER_THEME_FILE_PATH = "/sqltemplate/delete-sqlserver-theme.sql";

    /**
     * 行
     */
    private static final String ROW_EXPR = "\n";

    /**
     * 字段空值
     */
    private static final String VALUE_NULL = "NULL";

    /**
     * sql结尾的;符号
     */
    private static final String SQL_COMMA = ";";

    /**
     * 插入关键字
     */
    private static final String INSERT = "insert";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /*
'2021-11-04 18:54:11.03'
@"'\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.\d+'"

TO_DATE\('\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}','yyyy-mm-dd hh24:mi:ss'\)
@"TO_DATE\('\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}','yyyy-mm-dd hh24:mi:ss'\)"

TIMESTAMP '2021-11-02 16:05:33'
TIMESTAMP'2021-12-12 16:58:36.277'
@"TIMESTAMP'\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}.?\d*'"
     */
    /**
     * 匹配时间正则表达式
     */
    private List<String> DATETIME_PATTERN = Arrays.asList(
            "'\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d+'",
            "TO_DATE\\('\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}','yyyy-mm-dd hh24:mi:ss'\\)",
            "TIMESTAMP'\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.?\\d*'",
            "sysdate",
            "getDate\\(\\)",
            "NOW\\(\\)"
    );

    @Autowired
    ThemeSetDao themeSetDao;

    @Override
    public ThemeSetBO getBOById(Long id) throws IOException {

        ThemeSetDto themeSetDto = themeSetDao.findById(id);

        String themeIds_s = themeSetDao.findThemeIdsById(id).stream().map(Object::toString).collect(Collectors.joining(","));
        Assert.hasLength(themeIds_s, "主题图id为空");
        String themeNames_s = String.join(",", themeSetDao.findThemeNamesById(id));
        String customMaticNames = String.join(",", themeSetDao.findCustomThematicNamesById(id));
        String customIds_s = themeSetDao.findCustomIds(id).stream().map(Object::toString).collect(Collectors.joining(","));
        Assert.hasLength(customIds_s, "自定义专题图id为空");
        String init_s = getString(INIT_SQLSERVER_THEME_FILE_PATH);
        String delete_s = getString(DELETE_SQLSERVER_THEME_FILE_PATH);
        init_s = getString(init_s, themeIds_s, customIds_s);
        delete_s = getString(delete_s, themeIds_s, customIds_s);
        String tableInsertString = getTableInsertString(Arrays.asList("BB_", "CC_"));
        return new ThemeSetBO(themeSetDto.getName(), themeIds_s, themeNames_s, customIds_s,customMaticNames, init_s, delete_s, tableInsertString);
    }

    @Override
    public ThemeSetBO getBOByIdAndInit(Long id) throws IOException {
        ThemeSetBO boById = getBOById(id);
        executeSql(boById.getInit());
        return boById;
    }

    @Override
    public void themeExport(HttpServletResponse response, SetPostVO vo) throws IOException {
        ThemeSetBO boById = getBOById(vo.getId());
        ThemeSetDto themeSetDto = themeSetDao.findById(vo.getId());

        String insertText = boById.getInsertText();

        List<String> lines = matchInsertSql(insertText);
        List<String> newDeletes = new ArrayList<>();
        List<String> newInserts = new ArrayList<>();
        AdminInfo adminInfo = new AdminInfo();

        Map<String, RowBO> map = new HashMap<>();
        for (String item :
                lines) {
            map.put(item, getRowBO(item));
        }
        List<String> listOfFileNames = new ArrayList<>();
        for (DELETE_SAVE_TYPE saveType :
                DELETE_SAVE_TYPE.values()) {
            for (int i = 0; i < lines.size(); i++) {
                String rowText = lines.get(i);
                RowBO rowBO = map.get(rowText);
                newDeletes.add(getNewDeleteSql(rowBO));
                //设置字段默认值
                setInsertValue(saveType, rowBO, adminInfo);
                String insertSql = getInsertSql(rowBO);
                newInserts.add(insertSql);
            }
            String saveText_d = getSaveText(newDeletes, lines, insertText);
            String saveText_i = getSaveText(newInserts, lines, insertText);

            String fileContent = boById.getDelete() + ROW_EXPR + saveText_d + ROW_EXPR + saveText_i;
            saveSQLFile(fileContent, saveType, listOfFileNames);
        }
        String outputFileName = getOutputFileName(Constants.MAP_VERSION + "-" + themeSetDto.getName() + "-更新新包的增量.zip");
        downloadZipFile(response, listOfFileNames, outputFileName);
    }

    @Override
    public void allExport(HttpServletResponse response) throws IOException {
        createSqlServerAllView();
        String outputFileName = getOutputFileName(Constants.MAP_VERSION + "-地图模块初始化语句-首次上包执行的全量sql.zip");
        exportInsertTextAndDownload(response, Arrays.asList("AA_"), outputFileName);
    }



    @Override
    public void systemCodeExport(HttpServletResponse response) throws IOException {
        createSqlServerAllView();
        String outputFileName = getOutputFileName(Constants.MAP_VERSION + "-地图图层编码初始化语句sql.zip");
        exportInsertTextAndDownload(response, Arrays.asList("AB_"), outputFileName);
    }

    @Override
    public void iconLibraryExport(HttpServletResponse response) throws IOException {
        createSqlServerAllView();
        String outputFileName = getOutputFileName(Constants.MAP_VERSION + "-地图图标库初始化-更新新包的增量sql.zip");
        exportInsertTextAndDownload(response, Arrays.asList("AA_SESGIS_ICON_LIBRARIES", "AA_SESGIS_LIBRARY_TYPES"), outputFileName);
    }

    /*--------------------------------------------------------------------公共方法------------------------------------------------------------------------------*/

    /**
     * 创建sqlserver所有的视图
     * @throws IOException
     */
    private void createSqlServerAllView() throws IOException {
        String init_s = getString(INIT_SQLSERVER_ALL_FILE_PATH);
        executeSql(init_s);
    }

    /**
     * 获取下载的文件名
     *
     * @param fileNameSuffix 文件名后缀
     * @return
     */
    private String getOutputFileName(String fileNameSuffix) {
        return "" + getDateString() + "-" + fileNameSuffix;
    }

    /**
     * 导出新增的模板并下载
     *
     * @param response     下载返回
     * @param viewPrefixes 视图的前缀
     * @param fileName1    导出的文件名称
     */
    void exportInsertTextAndDownload(HttpServletResponse response, List<String> viewPrefixes, String fileName1) throws IOException {
        String insertText = getTableInsertString(viewPrefixes);
        List<String> lines = matchInsertSql(insertText);
        List<String> newInserts = new ArrayList<>();
        AdminInfo adminInfo = new AdminInfo();

        Map<String, RowBO> map = new HashMap<>();
        for (String item :
                lines) {
            map.put(item, getRowBO(item));
        }
        List<String> listOfFileNames = new ArrayList<>();
        for (DELETE_SAVE_TYPE saveType :
                DELETE_SAVE_TYPE.values()) {
            for (int i = 0; i < lines.size(); i++) {
                String rowText = lines.get(i);
                RowBO rowBO = map.get(rowText);
                //设置字段默认值
                setInsertValue(saveType, rowBO, adminInfo);
                String insertSql = getInsertSql(rowBO);
                newInserts.add(insertSql);
            }
            String saveText_i = getSaveText(newInserts, lines, insertText);
            saveSQLFile(saveText_i, saveType, listOfFileNames);
        }
        downloadZipFile(response, listOfFileNames, fileName1);
    }

    /**
     * 下载压缩包
     *
     * @param response        接口返回
     * @param listOfFileNames 下载的文件
     * @param filename       导出的集合名称
     */
    public void downloadZipFile(HttpServletResponse response, List<String> listOfFileNames, String filename) {
//        String filename = "download11111" + getDateString() + "-" + themeSetDto.getName() + ".zip";
        System.out.println(filename);
        response.setContentType("application/zip");
//        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        try {
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + URLEncoder.encode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
//        response.setHeader("Content-Disposition", "attachment; filename=download11111" + ".zip");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            for (String fileName : listOfFileNames) {
                FileSystemResource fileSystemResource = new FileSystemResource(fileName);

//                FileSystemResource resource = new FileSystemResource(fileName);
//                resource.setCharset(StandardCharsets.UTF_8);

                ZipEntry zipEntry = new ZipEntry(fileSystemResource.getFilename());
                zipEntry.setSize(fileSystemResource.contentLength());
                zipEntry.setTime(System.currentTimeMillis());

                zipOutputStream.putNextEntry(zipEntry);

                StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存文件
     *
     * @param sqlText        要保存的文本
     * @param dbType         数据库类型
     * @param listOfFileName 保存的文件名
     */
    private void saveSQLFile(String sqlText, DELETE_SAVE_TYPE dbType, List<String> listOfFileName) throws IOException {
        String filename = "";
        switch (dbType) {
            case DELETE_AND_ORACLE:
                filename = "oracle.sql";
                break;
            case DELETE_AND_SQLSERVER:
                filename = "sqlserver.sql";
                break;
            case DELETE_AND_MYSQL:
                filename = "mysql.sql";
                break;
            default:
                break;
        }
        //保存的文件夹
        String outputDirectory = createCustomDir();

        String targetFile = outputDirectory + "\\" + filename;
        listOfFileName.add(targetFile);

        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
        writer.write(sqlText);
        writer.close();
    }

    /**
     * 获取临时文件夹路径
     *
     * @return
     */
    private String createCustomDir() {
        String tempDirPath = System.getProperty("java.io.tmpdir") + getDateString() + "\\";
        File directory = new File(tempDirPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        System.out.println("tempDirPath:" + tempDirPath);
        return tempDirPath;
    }

    /**
     * 获取时间字符串
     *
     * @return
     */
    private String getDateString() {
        LocalDateTime dateTimeObj = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = dateTimeObj.format(formatter);
        return date;
    }

    /**
     * 处理字段值
     *
     * @param dbtype    数据库类型
     * @param rowBO     一行
     * @param adminInfo 默认数据
     */
    private void setInsertValue(DELETE_SAVE_TYPE dbtype, RowBO rowBO, AdminInfo adminInfo) {
        for (int c = rowBO.getColumns().size() - 1; c >= 0; c--) {
            if (VALUE_NULL.equals(rowBO.getValues().get(c))) {
                rowBO.getColumns().remove(c);
                rowBO.getValues().remove(c);
                continue;
            }
            if (isDateTime(rowBO.getColumns().get(c), rowBO.getValues().get(c))) {
                rowBO.getValues().set(c, getDateFunction(dbtype));
            }
            if (isCompany(rowBO.getColumns().get(c))) {
                rowBO.getValues().set(c, adminInfo.getADMIN_COMPANY_ID());
            }
            if (isDepartment(rowBO.getColumns().get(c))) {
                rowBO.getValues().set(c, adminInfo.getADMIN_DEPARTMENT_ID());
            }
            if (isPosition(rowBO.getColumns().get(c))) {
                rowBO.getValues().set(c, adminInfo.getADMIN_POSITION_ID());
            }
            if (isStaff(rowBO.getColumns().get(c))) {
                rowBO.getValues().set(c, adminInfo.getADMIN_STAFF_ID());
            }
        }
    }

    /**
     * 判断是否为员工
     *
     * @param c
     * @return
     */
    private Boolean isStaff(String c) {
        return c.equalsIgnoreCase("MODIFY_STAFF_ID") | c.equalsIgnoreCase("CREATE_STAFF_ID") | c.equalsIgnoreCase("OWNER_STAFF_ID");
    }

    /**
     * 判断是否为岗位
     *
     * @param c
     * @return
     */
    private Boolean isPosition(String c) {
        return c.equalsIgnoreCase("OWNER_POSITION_ID") | c.equalsIgnoreCase("CREATE_POSITION_ID");
    }

    /**
     * 判断是否为部门
     *
     * @param c
     * @return
     */
    private Boolean isDepartment(String c) {
        return c.equalsIgnoreCase("OWNER_DEPARTMENT_ID") | c.equalsIgnoreCase("CREATE_DEPARTMENT_ID");
    }

    /**
     * 当前列为公司
     *
     * @param c
     * @return
     */
    private Boolean isCompany(String c) {
        return c.equalsIgnoreCase("CID");
    }

    /**
     * 获取时间值
     *
     * @param dbtype
     * @return
     */
    private String getDateFunction(DELETE_SAVE_TYPE dbtype) {
        String result = "";
        switch (dbtype) {
            case DELETE_AND_ORACLE:
                result = "sysdate";
                break;
            case DELETE_AND_SQLSERVER:
                result = "getDate()";
                break;
            case DELETE_AND_MYSQL:
                result = "NOW()";
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 判断是否是时间类型
     *
     * @param c
     * @param v
     * @return
     */
    private Boolean isDateTime(String c, String v) {
        if (v == null || v.isEmpty()) {
            return false;
        }
        if ("MODIFY_TIME".equals(c) || "CREATE_TIME".equals(c)) {
            return true;
        }
        for (String pattern :
                DATETIME_PATTERN) {
            boolean matches = Pattern.matches(pattern, v);
            if (matches) {
                return true;
            }
        }
        if ("sysdate".equalsIgnoreCase(v)) {
            return true;
        }
        if ("getdate()".equalsIgnoreCase(v)) {
            return true;
        }
        if ("now()".equalsIgnoreCase(v)) {
            return true;
        }
        return false;
    }

    /**
     * 获取处理后要保存的文件
     *
     * @param newSqls  处理后的文本
     * @param inserts  处理前的文本
     * @param sqltexts 原始输入文本
     * @return
     */
    private String getSaveText(List<String> newSqls, List<String> inserts, String sqltexts) {
        for (int i = 0; i < inserts.size(); i++) {
            sqltexts = sqltexts.replace(inserts.get(i), newSqls.get(i));
        }
        return sqltexts;
    }

    /**
     * 获取中间数据类型
     *
     * @param sqlText 一行语句
     * @return
     */
    private RowBO getRowBO(String sqlText) {
        TInsertSqlStatement insert = getSqlStatement(sqlText);
        List<String> clist = getColumnList(insert);
        List<String> vlist = new ArrayList<>();
        getValueListAndAssert(insert, clist, vlist);

        //过滤出值不为空的
        getColumnValueSkipNull(clist, vlist);
        return new RowBO(clist, vlist, insert);
    }

    /**
     * 获取删除语句
     *
     * @param rowBO
     * @return
     */
    private String getNewDeleteSql(RowBO rowBO) {
        String id = getDeleteId(rowBO.getColumns(), rowBO.getValues());
        return "DELETE FROM " + rowBO.getInsertSqlStatement().getTargetTable().getName() + " WHERE ID = " + id + ";";
    }

    /**
     * 获取插入语句
     *
     * @param rowBO
     * @return
     */
    private String getInsertSql(RowBO rowBO) {
        return "INSERT INTO " + rowBO.getInsertSqlStatement().getTargetTable().getName() + "(" + String.join(",", rowBO.getColumns()) + ") VALUES(" + String.join(",", rowBO.getValues()) + ");";
    }

    /**
     * 获取值不为空的列和值
     *
     * @param clist 过滤前的列
     * @param vlist 过滤前的值
     */
    private void getColumnValueSkipNull(List<String> clist, List<String> vlist) {
        for (int i = clist.size() - 1; i >= 0; i--) {
            String value = vlist.get(i);
            if (!"NULL".equals(value)) {
                continue;
            }
            clist.remove(i);
            vlist.remove(i);
        }
    }

    /**
     * 获取主键Id
     *
     * @param clist
     * @param vlist
     * @return
     */
    private String getDeleteId(List<String> clist, List<String> vlist) {
        String id = "";
        for (int i = 0; i < vlist.size(); i++) {
            if (clist.get(i) == null) {
                continue;
            }
            if (!clist.get(i).toLowerCase().equals("id")) {
                continue;
            }
            id = vlist.get(i);
            break;
        }
        return id;
    }

    /**
     * 获取值列表并验证数量相同
     *
     * @param insert
     * @param clist
     * @param vlist
     * @return
     */
    private boolean getValueListAndAssert(TInsertSqlStatement insert, List<String> clist, List<String> vlist) {
        getValueList(insert, vlist);
        Assert.isTrue(clist.size() == vlist.size(), "列的数量和值的数据不一致");
        return true;
    }

    /**
     * 获取值
     *
     * @param insert 执行语句
     * @param vlist  值列表
     */
    private void getValueList(TInsertSqlStatement insert, List<String> vlist) {
        TMultiTarget element = (TMultiTarget) insert.getValues().getElement(0);
        for (TResultColumn o :
                element.getColumnList()) {
            vlist.add(o.toString());
        }
    }

    /// <summary>
    /// 获取插入列名
    /// </summary>
    /// <param name="insert">插入数据</param>
    /// <returns>列名列表</returns>
    private List<String> getColumnList(TInsertSqlStatement insert) {
        List<String> cclist = new ArrayList<>();

        for (TObjectName item :
                insert.getColumnList()) {
            cclist.add(item.getColumnNameOnly());
        }
        return cclist;
    }

    /**
     * 匹配SQL
     *
     * @param sqlText
     * @return
     */
    private TInsertSqlStatement getSqlStatement(String sqlText) {
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlParser.sqltext = sqlText;
        sqlParser.parse();

        TInsertSqlStatement insert = (TInsertSqlStatement) sqlParser.sqlstatements.get(0);
        return insert;
    }

    /**
     * 正则表达式匹配插入行
     *
     * @param sqlTexts 文本内容
     * @return 插入行列表
     */
    private List<String> matchInsertSql(String sqlTexts) {
        List<String> inserts = new ArrayList<>();
        String expr = ROW_EXPR;
        String[] split = sqlTexts.split(expr);
        for (String item :
                split) {
            if (item == null || item.trim().isEmpty()) {
                continue;
            }
            if (!item.toLowerCase().contains(INSERT)) {
                continue;
            }
            inserts.add(item);
        }
        return inserts;
    }

    /**
     * 替换占位符
     *
     * @param string
     * @param themeIds_s
     * @param customIds_s
     * @return
     */
    private String getString(String string, String themeIds_s, String customIds_s) {
        string = string.replace(CUSTOM_THEMATIC_IDS, customIds_s);
        string = string.replace(THEME_CONFIG_IDS, themeIds_s);
        return string;
    }

    /**
     * 获取文件内容
     *
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    private String getString(String fileName) throws IOException {
        Class clazz = ThemeSetServiceImpl.class;
        InputStream inputStream = clazz.getResourceAsStream(fileName);
        String data = readFromInputStream(inputStream);
        return data;
    }

    /**
     * 读取文件
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     * 创建视图
     *
     * @param init_s
     */
    private void executeSql(String init_s) {
        String[] split = init_s.split("\n");
        String newsql = "";
        for (String sqlLine :
                split) {
            newsql += sqlLine;
            if (!endWithSqlComma(sqlLine)) {
                continue;
            }
            jdbcTemplate.execute(newsql);
            newsql = "";
            try {

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 该行是否为;结尾
     *
     * @param sqlLine sql文件中一行文本
     * @return
     */
    private Boolean endWithSqlComma(String sqlLine) {
        String newSql = sqlLine;
        while (newSql != null && !newSql.isEmpty()) {
            String substring = newSql.substring(newSql.length() - 1);
            if (substring.trim().isEmpty()) {
                newSql = newSql.substring(newSql.length() - 1);
            } else if (SQL_COMMA.equals(substring)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取主题图和自定义专题图插入的SQL
     *
     * @param viewPrefixes 视图的前缀
     * @return
     */
    private String getTableInsertString(List<String> viewPrefixes) {
        List<String> bbAndCCViewNames = getViewNames(viewPrefixes);
        StringBuilder sb = new StringBuilder();
        for (String viewName : bbAndCCViewNames) {
//            String querySql = "SELECT * FROM " + viewName;
//            logger.error("querySql:");
//            logger.error(querySql);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + viewName);
            for (Map<String, Object> row : rows) {
                String tableName = row.getOrDefault("TABLE_NAME", viewName).toString();
                if (!tableName.isEmpty()) {
                    sb.append("INSERT INTO ").append(tableName).append("(");

                    int i = 0;
                    for (String columnName : row.keySet()) {
                        sb.append(columnName);

                        if (++i < row.size()) {
                            sb.append(",");
                        }
                    }

                    sb.append(") VALUES(");

                    i = 0;
                    for (Object value : row.values()) {
                        sb.append(formatValue(value));

                        if (++i < row.size()) {
                            sb.append(",");
                        }
                    }

                    sb.append(");\n");
                }
            }
        }

        String outputString = sb.toString();
        outputString = replaceViewPrefix(outputString);
//        System.out.println(outputString);
        return outputString;
    }

    /**
     * 替换掉数据库前缀
     *
     * @param sqltexts 原数据库文本
     * @return 处理后无前缀的文本
     */
    private String replaceViewPrefix(String sqltexts) {
        //替换掉数据库前缀
        for (String s :
                Constants.VIEW_PREFIX_ARRAY) {
            sqltexts = sqltexts.replace("ADP.dbo." + s, "").replace(s, "");
        }
        return sqltexts;
    }

    /**
     * 查询数据库中BB_和CC_开头的视图
     *
     * @param viewPrefix 视图的前缀
     * @return
     */
    public List<String> getViewNames(List<String> viewPrefix) {
        Assert.notEmpty(viewPrefix, "tablePrefix is empty");
//        String sql = "SELECT name FROM sys.objects WHERE type_desc='VIEW' AND (name LIKE 'BB_%' OR name LIKE 'CC_%')";
        String collect = viewPrefix.stream().map(v -> "name LIKE '" + v + "%'").collect(Collectors.joining(" OR "));
        String sql = String.format("SELECT name FROM sys.objects WHERE type_desc='VIEW' AND (%s)", collect);
        return jdbcTemplate.queryForList(sql, String.class);
    }

    /**
     * 转为string
     *
     * @param value
     * @return
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof java.sql.Date
                || value instanceof java.sql.Time || value instanceof java.sql.Timestamp) {
            return "'" + value.toString().replace("'", "''") + "'";
        }else if (value instanceof String) {
            return "N'" + value.toString().replace("'", "''") + "'";
        } else {
            return value.toString();
        }
    }

}