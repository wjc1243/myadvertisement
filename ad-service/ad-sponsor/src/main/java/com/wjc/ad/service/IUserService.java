package com.wjc.ad.service;

import com.wjc.ad.exception.AdException;
import com.wjc.ad.vo.CreateUserRequest;
import com.wjc.ad.vo.CreateUserResponse;

public interface IUserService{
    CreateUserResponse createUser(CreateUserRequest createUserRequest) throws AdException;
}
