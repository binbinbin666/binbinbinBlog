/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    
    if (!content){
        alert("不能回复空内容~")
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentId": questionId,
            "content": content,
            "type": 1
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=7eb8077db329c933c502&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closeable",true);
                    }
                } else {
                    alert(response.message);
                }
            }
        }
    })
    console.log(questionId)
    console.log(content)
}

/**
 * 需要回复时的登录
 */
function login() {
    window.open("https://github.com/login/oauth/authorize?client_id=7eb8077db329c933c502&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
    window.localStorage.setItem("closeable",true);
    setTimeout(function () {
        window.location.reload();
    }, 4000);
}

/**
 * 弹出确认框询问是否删除
 * @returns {boolean}
 */
function deleteQuestion() {
    return  confirm("确定要删除该条问题吗");
}

/**
 * 展开二级评论
 */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id)
    //获取二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        //展开二级评论
        comments.addClass("in");
        //标记二级评论为展开状态
        e.setAttribute("data-collapse","in");
        e.classList.add("active");
    }
}


