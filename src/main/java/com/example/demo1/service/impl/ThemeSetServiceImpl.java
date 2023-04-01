package com.example.demo1.service.impl;

import com.example.demo1.persistence.model.ThemeSet;
import com.example.demo1.persistence.model.ThemeSetItem;
import com.example.demo1.persistence.repo.ThemeSetItemRepository;
import com.example.demo1.persistence.repo.ThemeSetRepository;
import com.example.demo1.service.ThemeSetService;
import com.example.demo1.vo.*;
import com.example.demo1.web.exception.BookNotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
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
     * 删除主题文件路径
     */
    private static final String DELETE_SQLSERVER_THEME_FILE_PATH = "/sqltemplate/delete-sqlserver-theme.sql";

    /**
     * 行
     */
    private static final String ROW_EXPR = "\n";

    /**
     * 插入关键字
     */
    private static final String INSERT = "insert";


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
    private ThemeSetRepository themeSetRepository;

    @Autowired
    private ThemeSetItemRepository themeSetItemRepository;

    @Override
    public ThemeSetBO getBOById(Long id) throws IOException {
        List<ThemeSetItem> byThemeSetId = themeSetItemRepository.findByThemeSetId(id);
        String themeIds_s = byThemeSetId.stream().map(v -> v.getThemeId().getId().toString()).collect(Collectors.joining(","));
        String customIds_s = byThemeSetId.stream()
                .map(v -> v.getThemeId().getCustomThematics().stream().map(k -> k.getId().toString()).collect(Collectors.toList()))
                .flatMap(List::stream).distinct().collect(Collectors.joining(","));

        String init_s = getString(INIT_SQLSERVER_THEME_FILE_PATH);
        String delete_s = getString(DELETE_SQLSERVER_THEME_FILE_PATH);
        init_s = getString(init_s, themeIds_s, customIds_s);
        delete_s = getString(delete_s, themeIds_s, customIds_s);
        return new ThemeSetBO(themeIds_s, customIds_s, init_s, delete_s);
    }

    @Override
    public void handleSet(HttpServletResponse response, SetPostVO vo) throws IOException {
        ThemeSet themeSet = themeSetRepository.findById(vo.getId())
                .orElseThrow(BookNotFoundException::new);

        String insertText = vo.getInsertText();
        List<String> lines = matchInsertSql(insertText);
        List<String> newDeletes = new ArrayList<>();
        List<String> newInserts = new ArrayList<>();
        AdminInfo adminInfo = new AdminInfo();
        String customDir = createCustomDir();

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
                setInsertValue(saveType, rowBO, i, adminInfo);
                String insertSql = getInsertSql(rowBO);
                newInserts.add(insertSql);
            }
            String saveText_d = getSaveText(newDeletes, lines, insertText);
            String saveText_i = getSaveText(newInserts, lines, insertText);

            String fileContent = vo.getDeleteText() + ROW_EXPR + saveText_d + ROW_EXPR + saveText_i;
            saveSQLFile(fileContent, customDir, saveType, listOfFileNames);
        }
        downloadZipFile(response, listOfFileNames, themeSet);
    }

    /*--------------------------------------------------------------------公共方法------------------------------------------------------------------------------*/

    /**
     * 下载压缩包
     * @param response 接口返回
     * @param listOfFileNames 下载的文件
     * @param themeSet 导出的集合名称
     */
    public void downloadZipFile(HttpServletResponse response, List<String> listOfFileNames, ThemeSet themeSet) {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + getDateString() + "_" + themeSet.getName() + "download" + ".zip");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            for (String fileName : listOfFileNames) {
                FileSystemResource fileSystemResource = new FileSystemResource(fileName);
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
        }
    }

    /**
     * 保存文件
     *
     * @param sqlText         要保存的文本
     * @param outputDirectory 保存的文件夹
     * @param dbType          数据库类型
     * @param listOfFileName  保存的文件名
     */
    private void saveSQLFile(String sqlText, String outputDirectory, DELETE_SAVE_TYPE dbType, List<String> listOfFileName) throws IOException {

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-hhmmss");
        String date = dateTimeObj.format(formatter);
        return date;
    }

    /**
     * 处理字段值
     *
     * @param dbtype    数据库类型
     * @param rowBO     一行
     * @param i         第i列
     * @param adminInfo 默认数据
     */
    private void setInsertValue(DELETE_SAVE_TYPE dbtype, RowBO rowBO, int i, AdminInfo adminInfo) {
        if (isDateTime(rowBO.getColumns().get(i), rowBO.getValues().get(i))) {
            rowBO.getValues().set(i, getDateFunction(dbtype));
        }
        if (isCompany(rowBO.getColumns().get(i))) {
            rowBO.getValues().set(i, adminInfo.getADMIN_COMPANY_ID());
        }
        if (isDepartment(rowBO.getColumns().get(i))) {
            rowBO.getValues().set(i, adminInfo.getADMIN_DEPARTMENT_ID());
        }
        if (isPosition(rowBO.getColumns().get(i))) {
            rowBO.getValues().set(i, adminInfo.getADMIN_POSITION_ID());
        }
        if (isStaff(rowBO.getColumns().get(i))) {
            rowBO.getValues().set(i, adminInfo.getADMIN_STAFF_ID());
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


}