package com.entity.view;

import com.entity.YiqiEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;

/**
 * 仪器
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email
 * @date 2021-02-26
 */
@TableName("yiqi")
public class YiqiView extends YiqiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	public YiqiView() {

	}

	public YiqiView(YiqiEntity yiqiEntity) {
		try {
			BeanUtils.copyProperties(this, yiqiEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
