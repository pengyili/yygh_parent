package com.atguigu.yygh.service;


import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author lyp
* @description 针对表【refund_info(退款信息表)】的数据库操作Service
* @createDate 2022-10-12 16:36:30
*/
public interface RefundInfoService extends IService<RefundInfo> {
    RefundInfo getByOrderId(Long orderId);
}
