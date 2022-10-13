package com.atguigu.yygh.service.impl;


import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.service.PaymentInfoService;
import com.atguigu.yygh.mapper.PaymentInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author lyp
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2022-10-11 00:04:09
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
implements PaymentInfoService{

}
