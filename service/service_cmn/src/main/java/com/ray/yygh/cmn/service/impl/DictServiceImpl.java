package com.ray.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ray.yygh.cmn.listener.DictListener;
import com.ray.yygh.cmn.mapper.DictMapper;
import com.ray.yygh.cmn.service.DictService;
import com.ray.yygh.model.cmn.Dict;
import com.ray.yygh.vo.cmn.DictEeVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    private DictMapper dictMapper;

    // 导出数据字典
    @Override
    public void exportDictData(HttpServletResponse response) {
        // 设置下载信息
        response.setContentType("application/ynd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = "dict";
        response.setHeader("Content-dispositon","attachment;fileName=" + fileName + ".xlsx");
        // 查询数据库
        List<Dict> dictList = dictMapper.selectList(null);
        List<DictEeVo> dictEeVoList = new ArrayList<>();
        for(Dict dict : dictList){
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoList.add(dictEeVo);
        }
        // 调用方法进行 Excel 的写操作
        try{
            EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVoList);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // 根据数据字典 id 查询子列表
    @Override
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",id);
        List<Dict> dictList = dictMapper.selectList(dictQueryWrapper);
        for(Dict dict : dictList){
            dict.setHasChildren(isChildren(dict.getId()));
        }
        return dictList;
    }

    // 导入数据字典
    @Override
    public void importDictData(MultipartFile multipartFile) {
        try{
            System.out.println(multipartFile.toString());
            EasyExcel.read(multipartFile.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 判断 id 下是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",id);
        return dictMapper.selectCount(dictQueryWrapper) > 0;
    }
}
