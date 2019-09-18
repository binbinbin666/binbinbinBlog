package com.chen.communit.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;

    public void setPagination(Integer totalCount, Integer page, Integer size) {

        //计算总页数
        this.totalPage= totalCount%size==0 ? totalCount/size : totalCount/size+1;
        //控制页数
        if(page<1){
            page = 1;
        }
        if(page>totalPage){
            page = totalPage;
        }
        this.page = page;

        //是否显示上一页
        this.showPrevious = !(page == 1);

        //是否显示下一页
        this.showNext = !(page == totalPage);



        //页数的显示
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if (page - i >= 1) {
                pages.add(0,page - i);
            }

            if (page  + i <= totalPage) {
                pages.add(page + i);
            }
        }
        //是否展示第一页
        this.showFirstPage = !pages.contains(1);

        //是否展示最后一页
        this.showEndPage = !pages.contains(totalPage);
    }
}
