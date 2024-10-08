package com.service;

import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.ZhuanyeEntity;
import java.util.Map;

/**
 * 专业 服务类
 * @author 
 * @since 2021-02-26
 */
public interface ZhuanyeService extends IService<ZhuanyeEntity> {

     PageUtils queryPage(Map<String, Object> params);

}