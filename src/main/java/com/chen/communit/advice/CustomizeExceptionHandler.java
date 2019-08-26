package com.chen.communit.advice;

import com.alibaba.fastjson.JSON;
import com.chen.communit.dto.ResultDTO;
import com.chen.communit.exception.CustomizeErrorCode;
import com.chen.communit.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler({Exception.class})
    ModelAndView handler(Throwable e, Model model,
                         HttpServletRequest request,
                         HttpServletResponse response) {

        String contentType = request.getContentType();

        if ("application/json".equals(contentType)) {
            //返回json

            ResultDTO resultDTO = null;
            if (e instanceof CustomizeException) {
                resultDTO = ResultDTO.errorOf((CustomizeException) e);
            } else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            PrintWriter writer = null;
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ex) {
            }

            return null;
        } else {
            //错误页面跳转
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}
