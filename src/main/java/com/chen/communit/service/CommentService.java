package com.chen.communit.service;

import com.chen.communit.dto.CommentDTO;
import com.chen.communit.enums.CommentTypeEnum;
import com.chen.communit.exception.CustomizeErrorCode;
import com.chen.communit.exception.CustomizeException;
import com.chen.communit.mapper.CommentMapper;
import com.chen.communit.mapper.QuestionExtMapper;
import com.chen.communit.mapper.QuestionMapper;
import com.chen.communit.mapper.UserMapper;
import com.chen.communit.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARENT_NOT_FOUND);
        }

        if(comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getId());
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            commentMapper.insert(comment);
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    /**
     * 查询问题的评论
     * @param id
     * @return
     */
    public List<CommentDTO> listByQuestionId(long id) {
        CommentExample example = new CommentExample();
        example.createCriteria().andParentIdEqualTo(id)
        .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        List<Comment> comments = commentMapper.selectByExample(example);

        if (comments.size() == 0){
            return new ArrayList<>();
        }

        //获取去重的评论人并存入list
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //获取评论人并转换为map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap(user->user.getId(),user -> user));

        //转换comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
