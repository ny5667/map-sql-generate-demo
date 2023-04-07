package com.example.demo1.service;


import com.example.demo1.vo.SetPostVO;
import com.example.demo1.vo.ThemeSetBO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ThemeSetService {

    /**
     * 通过语句id获取对应数据
     *
     * @param id
     * @return
     */
    ThemeSetBO getBOById(Long id) throws IOException;

    /**
     * 根据语句id获取对应数据，并初始化
     *
     * @param id
     * @return
     * @throws IOException
     */
    ThemeSetBO getBOByIdAndInit(Long id) throws IOException;

    /**
     * 生成自定义专题图更新语句
     *
     * @param response
     * @param vo
     */
    void themeExport(HttpServletResponse response, SetPostVO vo) throws IOException;

    /**
     * 所有地图表导出语句
     *
     * @param response
     * @throws IOException
     */
    void allExport(HttpServletResponse response) throws IOException;

    /**
     * 地图图标库导出
     * @param response
     * @throws IOException
     */
    void systemCodeExport(HttpServletResponse response) throws IOException;

    /**
     * 图标库导出
     * @param response
     * @throws IOException
     */
    void iconLibraryExport(HttpServletResponse response) throws IOException;

}
