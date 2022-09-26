package com.atguigu.yygh.dict.service;


import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DictService extends IService<Dict> {
    List<Dict> getDictByParentId(Long parentId);

    List<Dict> getDictByParentCode(String code );
}
