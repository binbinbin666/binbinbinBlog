package com.chen.communit.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    private long commentId;
    private Long parentId;
    private String content;
    private Integer type;
}
