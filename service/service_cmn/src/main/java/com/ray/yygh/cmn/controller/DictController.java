package com.ray.yygh.cmn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ray.yygh.cmn.service.DictService;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = "数据字典接口")
//@CrossOrigin
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {
    @Autowired
    private DictService dictService;

    // 根据数据id查询子数据列表
    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> dictList = dictService.findChildData(id);
        return Result.ok(dictList);
    }

    // 导出数据字典接口
    @ApiOperation(value = "导出数据字典")
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
    }


    // 导入数据字典接口
    @ApiOperation(value = "导入数据字典")
    @PostMapping("importData")
    public Result importData(MultipartFile file){
        System.out.println(file);
        dictService.importDictData(file);
        return Result.ok();
    }

    //根据dict_code和 value 查询
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }

    //根据 value 查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName("",value);
        return dictName;
    }

    //根据 dictCode查获取下级节点
    @ApiOperation(value = "根据 dictCode 获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCode(@PathVariable String dictCode){
        List<Dict> dictList = dictService.findByDictCode(dictCode);
        return Result.ok(dictList);
    }
}
