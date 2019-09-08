package com.chen.communit.controller;

import com.chen.communit.dto.AccessTokenDTO;
import com.chen.communit.dto.GitHubUser;
import com.chen.communit.model.User;
import com.chen.communit.provider.GitHubProvider;
import com.chen.communit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * GitHub登陆授权
 */
@Controller
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callBack(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        //获取accessToken
        String accessToken = gitHubProvider.getAccessToken(accessTokenDTO);
        //获取gitHubUser
        GitHubUser gitHubUser = gitHubProvider.getUser(accessToken);

        if (gitHubUser != null && gitHubUser.getId() != null){
            User user = new User();
            //自动生成token
            String token = UUID.randomUUID().toString();

            user.setToken(token);
            user.setName(gitHubUser.getName());
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setBio(gitHubUser.getBio());
            user.setAvatarUrl(gitHubUser.getAvatarUrl());
            //插入新用户或者更新用户
            userService.createOrUpdate(user);
            //添加cookie,用来查询新用户信息
            response.addCookie(new Cookie("token",token));
            //登录成功返回首页
            return "redirect:/";
        }else{
            //登陆失败，重新登录
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logOut(HttpServletRequest request,HttpServletResponse response){
        //移除session
        request.getSession().removeAttribute("user");
        //删除cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
