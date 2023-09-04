package com.ray.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ray.yygh.cmn.mapper.DictMapper;
import com.ray.yygh.model.cmn.Dict;
import com.ray.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListener extends AnalysisEventListener<DictEeVo> {
    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }
    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
