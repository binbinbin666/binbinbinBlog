package com.chen.communit.dto;

import com.chen.communit.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
