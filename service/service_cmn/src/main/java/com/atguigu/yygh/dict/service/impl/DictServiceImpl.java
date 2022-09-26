package com.atguigu.yygh.dict.service.impl;

import com.atguigu.yygh.dict.mapper.DictMapper;
import com.atguigu.yygh.dict.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Override
    @Cacheable(value = "dict")
    public List<Dict> getDictByParentId(Long parentId) {
        List<Dict> dicts = baseMapper.selectList(new LambdaQueryWrapper<Dict>().eq(Dict::getParentId, parentId));
        return dicts.stream()
                .peek(dict ->
                        dict.setHasChildren(
                                baseMapper.selectCount(
                                        new LambdaQueryWrapper<Dict>()
                                                .eq(Dict::getParentId, dict.getId())) > 0))
                .toList();
    }

    public List<Dict> getDictByParentCode(String code ){
        Dict dict = baseMapper.selectOne(new LambdaQueryWrapper<Dict>().select(Dict::getId).eq(Dict::getDictCode, code));
        return getDictByParentId(dict.getId());
    }
}
