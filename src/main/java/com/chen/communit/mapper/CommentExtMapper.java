package com.chen.communit.mapper;

import com.chen.communit.model.Comment;

public interface CommentExtMapper {

    int incCommentCount(Comment comment);

    int incthumbsUpCount(Comment comment);
}