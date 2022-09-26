package com.atguigu.yygh.dict.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DictUpdateListener implements ReadListener<DictEeVo> {

    private final BaseMapper<Dict> baseMapper;
    private Map<Long , DictEeVo> cacheData ;

    public DictUpdateListener(BaseMapper<Dict> baseMapper) {
        this.baseMapper = baseMapper ;
    }
    public DictUpdateListener(BaseMapper<Dict> baseMapper ,Map<Long , DictEeVo> cacheData) {
        this.baseMapper = baseMapper ;
        this.cacheData = cacheData ;
    }


    @Override
    public void invoke(DictEeVo data, AnalysisContext context) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(data , dict);
        if(cacheData.containsKey(data.getId())){
            if(!cacheData.get(data.getId()).equals(data)) {
                log.debug("源数据{} , 新数据{}" ,  cacheData.get(data.getId()) , data );
                baseMapper.update(dict ,
                        new LambdaUpdateWrapper<Dict>().set(Dict::getParentId , data.getParentId())
                                .set(Dict::getName , data.getName())
                                .set(Dict::getValue ,data.getValue())
                                .set(Dict::getDictCode ,data.getDictCode())
                                .eq(Dict::getId , data.getId())
                );
                cacheData.put(data.getId(), data);
            }
        }else{
            Dict dict1 = baseMapper.selectOne(new LambdaQueryWrapper<Dict>().or( s -> s.eq(Dict::getId, data.getId())));
            if(dict1 != null ) {
                baseMapper.updateById(dict);
            }
            else {
                baseMapper.insert(dict);
            }
            cacheData.put(data.getId(), data);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }
}
