package com.chen.communit.controller;

import com.chen.communit.dto.QuestionDTO;
import com.chen.communit.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 查看问题详情
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionDTO questionDTO = questionService.getById(id);
        model.addAttribute("question",questionDTO);
        return "question";
    }

    /**
     * 删除问题
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public String delQuestion(@PathVariable("id") Integer id){
        questionService.delById(id);
        return "redirect:/profile/questions";
    }
}
