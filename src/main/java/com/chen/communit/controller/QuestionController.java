package com.chen.communit.controller;

import com.chen.communit.dto.CommentDTO;
import com.chen.communit.dto.QuestionDTO;
import com.chen.communit.enums.CommentTypeEnum;
import com.chen.communit.service.CommentService;
import com.chen.communit.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;
    /**
     * 查看问题详情
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") long id, Model model){
        QuestionDTO questionDTO = questionService.getById(id);

        //查询评论
        List<CommentDTO> comments = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        //累加阅读数
        questionService.inView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        return "question";
    }

    /**
     * 删除问题
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    public String delQuestion(@PathVariable("id") long id){
        questionService.delById(id);
        return "redirect:/profile/questions";
    }
}
