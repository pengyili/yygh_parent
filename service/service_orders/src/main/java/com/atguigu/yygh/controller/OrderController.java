package com.atguigu.yygh.controller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.service.OrderService;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService ;

    @PostMapping("/submitOrder/{scheduleId}/{patientId}")
    public Result addOrder(@RequestHeader String token , @PathVariable Long patientId, @PathVariable String scheduleId){



        Long orderId = orderService.savaOrder( token, patientId , scheduleId);

        return Result.ok().data("orderId" , orderId);
    }

    @GetMapping("/{page}/{limit}")
    public Result orderList(@PathVariable Integer page, @PathVariable Integer limit , OrderQueryVo orderQueryVo){

        Page<OrderInfo> orderList = orderService.page(
                Page.of(page , limit) ,
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(orderQueryVo.getUserId() != null , OrderInfo::getUserId ,  orderQueryVo.getUserId())
                        .eq(StringUtils.isNotBlank(orderQueryVo.getOutTradeNo()) , OrderInfo::getOutTradeNo , orderQueryVo.getOutTradeNo())
                        .eq(orderQueryVo.getPatientId() != null , OrderInfo::getPatientId , orderQueryVo.getPatientId())
                        .like(StringUtils.isNotBlank(orderQueryVo.getPatientName()) , OrderInfo::getPatientName , orderQueryVo.getPatientName())
                        .like(StringUtils.isNotBlank(orderQueryVo.getKeyword()) , OrderInfo::getHosname , orderQueryVo.getKeyword())
                        .eq(StringUtils.isNotBlank(orderQueryVo.getOrderStatus()) , OrderInfo::getOrderStatus , orderQueryVo.getOrderStatus())
                        .eq(StringUtils.isNotBlank(orderQueryVo.getReserveDate()) ,OrderInfo::getReserveDate , orderQueryVo.getReserveDate())
                        .ge(StringUtils.isNotBlank(orderQueryVo.getCreateTimeBegin()) , OrderInfo::getCreateTime , orderQueryVo.getCreateTimeBegin())
                        .le(StringUtils.isNotBlank(orderQueryVo.getCreateTimeEnd()) , OrderInfo::getCreateTime , orderQueryVo.getCreateTimeEnd())

        );
        orderList.getRecords().forEach(orderService::setOrderStatus);

        return Result.ok().data("page" , orderList);
    }
    @GetMapping("/getStatusList")
    public Result getStatusList(){
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();

        return Result.ok().data("items" , statusList );
    }
    @GetMapping("/getOrder/{orderId}")
    public Result getOrder(@PathVariable Long orderId){
        OrderInfo order = orderService.getById(orderId);
        orderService.setOrderStatus(order);
        return Result.ok().data("item" , order ) ;
    }

    @PutMapping("/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return Result.ok();
    }

}
