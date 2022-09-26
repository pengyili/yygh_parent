package com.atguigu.yygh.vo.cmn;

import com.alibaba.excel.annotation.ExcelProperty;

import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * <p>
 * Dict
 * </p>
 *
 * @author qy
 */
@Data
public class DictEeVo {

	@ColumnWidth(10)
	@ExcelProperty(value = "id" ,index = 0)
	private Long id;
	@ColumnWidth(10)
	@ExcelProperty(value = "上级id" ,index = 1)
	private Long parentId;
	@ColumnWidth(15)
	@ExcelProperty(value = "名称" ,index = 2)
	private String name;
	@ColumnWidth(10)
	@ExcelProperty(value = "值" ,index = 3)
	private String value;
	@ColumnWidth(10)
	@ExcelProperty(value = "编码" ,index = 4)
	private String dictCode;

}

