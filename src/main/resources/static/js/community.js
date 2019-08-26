function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
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
                $("#comment_section").hide();
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

function login() {
    window.open("https://github.com/login/oauth/authorize?client_id=7eb8077db329c933c502&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
    window.localStorage.setItem("closeable",true);
    setTimeout(function () {
        window.location.reload();
    }, 3000);
}

function deleteQuestion() {
    return  confirm("确定要删除该条问题吗");
}
// $(function () {
//
//     if ($("#user_name").val() == null || $("#user_name").val() == "") {
//         $("#comment_section").remove();
//     }
// });

