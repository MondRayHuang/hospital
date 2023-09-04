package com.ray.yygh.cmn.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ray.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    void exportDictData(HttpServletResponse response);

    //根据数据字典id查询子列表
    List<Dict> findChildData(Long id);

    void importDictData(MultipartFile multipartFile);
}
