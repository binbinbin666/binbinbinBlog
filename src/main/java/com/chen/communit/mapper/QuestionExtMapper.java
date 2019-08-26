package com.chen.communit.mapper;

import com.chen.communit.model.Question;

public interface QuestionExtMapper {

    int incView(Question record);
    int incCommentCount(Question question);
}