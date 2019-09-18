package com.chen.communit.controller;

import com.chen.communit.dto.NotificationDTO;
import com.chen.communit.enums.NotificationTypeEnum;
import com.chen.communit.model.User;
import com.chen.communit.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,@PathVariable("id") Long id){

        User user = (User)request.getSession().getAttribute("user");
        if(user == null){
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id,user);
        if(NotificationTypeEnum.REPLY_COMMENT.getType() == notificationDTO.getType()
                || NotificationTypeEnum.REPLY_QUESTION.getType() == notificationDTO.getType()) {
            return "redirect:/question/"+ notificationDTO.getOuterid();
        } else {
            return "redirect:/";
        }
    }
}
