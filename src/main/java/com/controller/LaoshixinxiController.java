package com.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import com.annotation.IgnoreAuth;
import com.entity.LaoshixinxiEntity;
import com.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.entity.LaoshixinxiEntity;

import com.service.LaoshixinxiService;
import com.utils.PageUtils;
import com.utils.R;

/**
 * 
 * 后端接口
 * @author
 * @email
 * @date 2021-02-25
*/
@RestController
@Controller
@RequestMapping("/laoshixinxi")
public class LaoshixinxiController {
    private static final Logger logger = LoggerFactory.getLogger(LaoshixinxiController.class);

    @Autowired
    private LaoshixinxiService laoshixinxiService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录
     */
    @IgnoreAuth
    @PostMapping(value = "/login")
    public R login(String username, String password, String role, HttpServletRequest request) {
        LaoshixinxiEntity user = laoshixinxiService.selectOne(new EntityWrapper<LaoshixinxiEntity>().eq("account", username));
        if(user != null){
            if(!user.getRole().equals(role)){
                return R.error("权限不正常");
            }
            if(user==null || !user.getPassword().equals(password)) {
                return R.error("账号或密码不正确");
            }
            String token = tokenService.generateToken(user.getId(),user.getName(), "users", user.getRole());
            return R.ok().put("token", token);
        }else{
            return R.error("账号或密码或权限不对");
        }

    }

    /**
     * 注册
     */
    @IgnoreAuth
    @PostMapping(value = "/register")
    public R register(@RequestBody LaoshixinxiEntity user){
        if(laoshixinxiService.selectOne(new EntityWrapper<LaoshixinxiEntity>().eq("account", user.getAccount())) !=null) {
            return R.error("老师已存在");
        }
        user.setRole("老师");
        laoshixinxiService.insert(user);
        return R.ok();
    }

    /**
     * 退出
     */
    @GetMapping(value = "logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return R.ok("退出成功");
    }

    /**
     * 密码重置
     */
    @IgnoreAuth
    @RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
        LaoshixinxiEntity user = laoshixinxiService.selectOne(new EntityWrapper<LaoshixinxiEntity>().eq("username", username));
        if(user==null) {
            return R.error("账号不存在");
        }
        user.setPassword("123456");
        laoshixinxiService.update(user,null);
        return R.ok("密码已重置为：123456");
    }

    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
        Integer id = (Integer)request.getSession().getAttribute("userId");
        LaoshixinxiEntity user = laoshixinxiService.selectById(id);
        return R.ok().put("data", user);
    }
    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("Controller:"+this.getClass().getName()+",page方法");
        Object role = request.getSession().getAttribute("role");
        PageUtils page = null;
        if(role.equals("老师")){
            params.put("yh",request.getSession().getAttribute("userId"));
            page = laoshixinxiService.queryPage(params);
        }else{
            page = laoshixinxiService.queryPage(params);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        logger.debug("Controller:"+this.getClass().getName()+",info方法");
        LaoshixinxiEntity laoshixinxi = laoshixinxiService.selectById(id);
        if(laoshixinxi!=null){
            return R.ok().put("data", laoshixinxi);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody LaoshixinxiEntity laoshixinxi, HttpServletRequest request){
        logger.debug("Controller:"+this.getClass().getName()+",save");
        Wrapper<LaoshixinxiEntity> queryWrapper = new EntityWrapper<LaoshixinxiEntity>()
            .eq("name", laoshixinxi.getName())
            .eq("account", laoshixinxi.getAccount())
            .eq("password", laoshixinxi.getPassword())
            .eq("sex_types", laoshixinxi.getSexTypes())
            .eq("role", laoshixinxi.getRole())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        LaoshixinxiEntity laoshixinxiEntity = laoshixinxiService.selectOne(queryWrapper);
        if("".equals(laoshixinxi.getImgPhoto()) || "null".equals(laoshixinxi.getImgPhoto())){
            laoshixinxi.setImgPhoto(null);
        }
        if(laoshixinxiEntity==null){
            laoshixinxi.setRole("老师");
            laoshixinxiService.insert(laoshixinxi);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody LaoshixinxiEntity laoshixinxi, HttpServletRequest request){
        logger.debug("Controller:"+this.getClass().getName()+",update");
        //根据字段查询是否有相同数据
        Wrapper<LaoshixinxiEntity> queryWrapper = new EntityWrapper<LaoshixinxiEntity>()
            .notIn("id",laoshixinxi.getId())
            .eq("name", laoshixinxi.getName())
            .eq("account", laoshixinxi.getAccount())
            .eq("password", laoshixinxi.getPassword())
            .eq("sex_types", laoshixinxi.getSexTypes())
            .eq("role", laoshixinxi.getRole())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        LaoshixinxiEntity laoshixinxiEntity = laoshixinxiService.selectOne(queryWrapper);
        if("".equals(laoshixinxi.getImgPhoto()) || "null".equals(laoshixinxi.getImgPhoto())){
                laoshixinxi.setImgPhoto(null);
        }
        if(laoshixinxiEntity==null){
            laoshixinxiService.updateById(laoshixinxi);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        logger.debug("Controller:"+this.getClass().getName()+",delete");
        laoshixinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}

