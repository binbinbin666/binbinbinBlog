/**
 * 提交对问题的评论
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content)
}

/**
 * 提交对回复的评论
 * @param commentId
 */
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-" + commentId).val();
    comment2target(commentId, 2, content)
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~")
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=7eb8077db329c933c502&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
        }
    })
}

/**
 * 需要回复时的登录
 */
function login() {
    window.open("https://github.com/login/oauth/authorize?client_id=7eb8077db329c933c502&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
    window.localStorage.setItem("closeable", true);
    setTimeout(function () {
        window.location.reload();
    }, 4000);
}

/**
 * 弹出确认框询问是否删除
 * @returns {boolean}
 */
function deleteQuestion() {
    return confirm("确定要删除该条问题吗");
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment2-" + id)
    //获取二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        //折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {


        var subCommentContainer = $("#comment2-" + id);
        //判断是否加载过
        if (subCommentContainer.children().length !== 1){
            //展开二级评论
            comments.addClass("in");
            //标记二级评论为展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        }else {
            $.getJSON("/comment/" + id, function (data) {
                $.each(data.data.reverse(), function (index,comment) {


                    var avatar = $("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    });
                    var mediaLeft = $("<div/>", {
                        "class": "media-left",
                    });
                    mediaLeft.append(avatar);



                    var mediaHead_span = $("<span/>", {
                        "class": "media-heading",
                        "text":comment.user.name
                    });
                    var mediaHead = $("<h5/>", {
                        "class": "media-heading"
                    });
                    mediaHead.append(mediaHead_span);

                    var content = $("<div/>", {
                        "class": "media-body",
                        "text": comment.content
                    });

                    var menu_span = $("<span/>", {
                        "class": "pull-right",
                        "text": new Date(comment.gmtCreate + 8 * 3600 * 1000).toJSON().substr(0, 19)
                            .replace('.',/-/g)
                            .replace('T', ' ')
                    });
                    var menu = $("<div/>", {
                        "class": "menu",
                    });
                    menu.append(menu_span);


                    var mediaBody = $("<div/>", {
                        "class": "media-body",
                    });
                    mediaBody.append(mediaHead);
                    mediaBody.append(content);
                    mediaBody.append(menu);

                    var media = $("<div/>", {
                        "class": "media",
                    });
                    media.append(mediaLeft);
                    media.append(mediaBody);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments",
                    });
                    commentElement.append(media);
                    subCommentContainer.prepend(commentElement);
                });

            })
        }


        //展开二级评论
        comments.addClass("in");
        //标记二级评论为展开状态
        e.setAttribute("data-collapse", "in");
        e.classList.add("active");
    }
}

/**
 * 点赞功能
 */
function thumbsUp(e) {
    var commentId = e.getAttribute("data-id");
    $.ajax({
        type: "POST",
        url: "/thumbsup",
        contentType: "application/json",
        data: JSON.stringify({
            "commentId": commentId,
        }),
        success: function (response) {
            if (response.code == 200) {
                document.getElementById(commentId).innerText = Number(document.getElementById(commentId).innerText)+1;
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=7eb8077db329c933c502&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
        }
    })
}

