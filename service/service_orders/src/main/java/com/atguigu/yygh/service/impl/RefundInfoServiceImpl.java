package com.atguigu.yygh.service.impl;

import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.yygh.service.RefundInfoService;
import com.atguigu.yygh.mapper.RefundInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author lyp
* @description 针对表【refund_info(退款信息表)】的数据库操作Service实现
* @createDate 2022-10-12 16:36:30
*/
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo>
implements RefundInfoService{
    public RefundInfo getByOrderId(Long orderId) {
        return getOne(new LambdaQueryWrapper<RefundInfo>().eq(RefundInfo::getOrderId ,orderId));
    }
}
