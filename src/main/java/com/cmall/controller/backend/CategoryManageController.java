package com.cmall.controller.backend;


import com.cmall.common.Const;
import com.cmall.common.ResponseCode;
import com.cmall.common.ServerResponse;
import com.cmall.service.ICategoryService;
import com.cmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cmall.pojo.User;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加分类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value="parentId",defaultValue="0") int parentId){
        //校验登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，增加处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 更新分类名字
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId,String categoryName){
        //校验登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //更新categoryName
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 查询子节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){
        //校验登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 递归查询子节点
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_recursion_category.do")
    @ResponseBody
    public ServerResponse getRecursionChildrenCategory(HttpSession session, @RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){
        //校验登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        //校验是否是管理员
        if(iUserService.checkAdminRole(user).isSuccess()){
        //查询当前节点的Id和递归子节点的Id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }
}
