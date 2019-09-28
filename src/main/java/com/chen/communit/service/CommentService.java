package com.chen.communit.service;

import com.chen.communit.dto.CommentDTO;
import com.chen.communit.enums.CommentTypeEnum;
import com.chen.communit.enums.NotificationTypeEnum;
import com.chen.communit.enums.NotificationStatusEnum;
import com.chen.communit.exception.CustomizeErrorCode;
import com.chen.communit.exception.CustomizeException;
import com.chen.communit.mapper.*;
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
    private CommentExtMapper commentExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 新增对问题或者对回复的评论
     *
     * @param comment
     * @param commentator
     */
    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARENT_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);
            //增加评论数
            Comment comment1 = new Comment();
            comment1.setCommentCount(1);
            comment1.setId(comment.getParentId());
            commentExtMapper.incCommentCount(comment1);
            //回复通知
            createNotify(comment, dbComment.getCommentator(), question.getTitle(), commentator.getName(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加问题回复数
            Question question1 = new Question();
            question1.setCommentCount(1);
            question1.setId(question.getId());
            questionExtMapper.incCommentCount(question1);

            createNotify(comment, question.getCreator(), question.getTitle(), commentator.getName(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }

    /**
     * 对问题或者评论回复的通知
     *
     * @param comment
     * @param receiver
     * @param outerTitle
     * @param notifierName
     * @param notificationTypeEnum
     */
    private void createNotify(Comment comment, Long receiver, String outerTitle, String notifierName, NotificationTypeEnum notificationTypeEnum, Long outerId) {
        if (receiver == comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();

        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationTypeEnum.getType());
        notification.setNotifier(comment.getCommentator());
        notification.setOuterid(outerId);
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());

        notificationMapper.insert(notification);
    }

    /**
     * 查询问题的评论
     *
     * @param id
     * @param type
     * @return
     */
    public List<CommentDTO> listByTargetId(long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria().andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(example);

        if (comments.size() == 0) {
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
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment 为 commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

    /**
     * 点赞功能
     *
     * @param id
     */
    public void thumbsUp(long id) {
        Comment comment = new Comment();
        comment.setLikeCount(1l);
        comment.setId(id);
        commentExtMapper.incthumbsUpCount(comment);
    }
}
