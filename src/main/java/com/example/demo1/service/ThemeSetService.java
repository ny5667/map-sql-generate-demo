package com.example.demo1.service;


import com.example.demo1.vo.SetPostVO;
import com.example.demo1.vo.ThemeSetBO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ThemeSetService {

    /**
     * 通过语句id获取对应数据
     * @param id
     * @return
     */
    ThemeSetBO getBOById(Long id) throws IOException;

    /**
     * 生成自定义专题图更新语句
     * @param response
     * @param vo
     */
    void handleSet(HttpServletResponse response, SetPostVO vo) throws IOException;

}
