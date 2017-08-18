package com.cmall.controller.backend;

import com.cmall.common.Const;
import com.cmall.common.ResponseCode;
import com.cmall.common.ServerResponse;
import com.cmall.pojo.Product;
import com.cmall.pojo.User;
import com.cmall.service.IFileService;
import com.cmall.service.IProductService;
import com.cmall.service.IUserService;
import com.cmall.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 后台商品新增或更新
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 商品上下架即更新商品销售状态功能
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //增加产品的业务逻辑
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台获取商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.manageDetail(productId);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台商品列表动态分页
     * @param session
     * @param
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value="pageNum",defaultValue="1") int pageNum, @RequestParam(value="pageSize",defaultValue="10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * 后台商品搜索功能
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName,Integer productId,@RequestParam(value="pageNum",defaultValue="1") int pageNum, @RequestParam(value="pageSize",defaultValue="10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    //ftp的启动是通过vsftpd这个软件。service vsftpd start这个命令就可以启动了。这次代码步骤就是上传文件。
    // 主要看上传图片文件这个部分。这块和反向代理无关。
    //简单理解就是:上传文件到tomcat服务器->从tomcat服务器上传到ftp服务器->生成http连接->返回http连接->访问图片。
    /**
     * 后台商品图片的springmvc上传
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value="upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){

            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess(fileMap);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作");
        }

    }

    /**
     * 富文本上传
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value="upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求，因为使用的是simditor所以按照simditor的要求返回
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");

                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }

    }



}
