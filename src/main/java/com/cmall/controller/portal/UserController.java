package com.cmall.controller.portal;

import com.cmall.common.Const;
import com.cmall.common.ResponseCode;
import com.cmall.common.ServerResponse;
import com.cmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cmall.pojo.User;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        //如果成功就要把用户放入sessions
        if(response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * 用户登出
     * @param session
     * @return
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value="register.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_valid.do",method= RequestMethod.POST)
    @ResponseBody()
    //防止恶意用户通过接口调用注册接口，校验用户名和email是否存在，点击完用户名输入框后调用一个校验接口
    public ServerResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }


    /**
     * 获取用户登录信息
     * @param session
     * @return
     */
    @RequestMapping(value="get_user_info.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    /**
     * 提示问题
     * @param username
     * @return
     */
    @RequestMapping(value="forget_get_question.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    /**
     * 验证答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value="forget_check_answer.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    /**
     * 忘记密码中的重置密码部分功能开发
     */
    @RequestMapping(value="forget_reset_password.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetSetPassword(username,passwordNew,forgetToken);
    }

    /**
     * 登录状态下修改密码
     * @param session
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value="reset_password.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<String> resetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    /**
     * 更新用户个人信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value="update_information.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        //为防止越权，从session里获取登录用户，防止id变化
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 获取用户详细信息
     * @param session
     * @return
     */
    @RequestMapping(value="get_information.do",method= RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<User> getInformation(HttpSession session){
        //如果没有登录要进行强制登录
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登陆status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

}
