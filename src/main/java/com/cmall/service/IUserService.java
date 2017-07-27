package com.cmall.service;

import com.cmall.common.ServerResponse;
import com.cmall.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
}
