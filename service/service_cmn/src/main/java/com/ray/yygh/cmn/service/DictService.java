package com.ray.yygh.cmn.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ray.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    //导出数据字典
    void exportDictData(HttpServletResponse response);

    //根据数据字典id查询子列表
    List<Dict> findChildData(Long id);

    //导入数据字典
    void importDictData(MultipartFile multipartFile);

    //根据 dictCode 和 value 查询数据字典名称
    String getDictName(String dictCode, String value);

    //根据 dictCode 获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
