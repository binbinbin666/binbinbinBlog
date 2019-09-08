package com.chen.communit.mapper;

import com.chen.communit.model.Question;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
    int incCommentCount(Question question);
    List<Question> selectRelated(Question question);
}