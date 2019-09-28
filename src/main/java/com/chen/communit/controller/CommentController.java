package com.chen.communit.controller;

import com.chen.communit.dto.CommentCreateDTO;
import com.chen.communit.dto.CommentDTO;
import com.chen.communit.dto.ResultDTO;
import com.chen.communit.enums.CommentTypeEnum;
import com.chen.communit.exception.CustomizeErrorCode;
import com.chen.communit.model.Comment;
import com.chen.communit.model.User;
import com.chen.communit.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 提交评论
     * @param commentCreateDTO
     * @param httpServletRequest
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest httpServletRequest){
        //判断是否登录
        User user = (User)httpServletRequest.getSession().getAttribute("user");
        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        //判断输入是否为空
        if(commentCreateDTO == null || StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }

        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setLikeCount(0L);
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setCommentCount(0);

        commentService.insert(comment,user);

        return ResultDTO.okOf();
    }

    /**
     * 查看二级评论
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO comments(@PathVariable("id") Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }

    /**
     * 点赞功能
     * @param commentCreateDTO
     * @param httpServletRequest
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/thumbsup",method = RequestMethod.POST)
    public Object thumbsUp(@RequestBody CommentCreateDTO commentCreateDTO,
                         HttpServletRequest httpServletRequest){
        //判断是否登录
        User user = (User)httpServletRequest.getSession().getAttribute("user");
        if (user == null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }

        commentService.thumbsUp(commentCreateDTO.getCommentId());
        return ResultDTO.okOf();
    }
}
