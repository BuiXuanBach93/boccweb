var userMemoRow = $('<div class="row memo-row" style="margin-right:10px; margin-left: 5px; margin-bottom: 10px">' +
    '<div class="direct-chat-info clearfix">' +
    '<span class="direct-chat-name admin-name" style="margin-left: 50px"></span> &nbsp;&nbsp;' +
    '<span class="direct-chat-timestamp mem-time"></span>' +
    '</div>' +
    '<img class="direct-chat-img" src="'+getContextPath()+'/resources/images/admin.png" alt="Admin">' +
    '<div class="direct-chat-text mem-content"; white-space: pre-wrap"></div>' +
    '</div>');

var userReportRow = $('<div class="row memo-row" style="margin-right:10px; margin-left: 5px; margin-bottom: 10px">' +
    '<div class="direct-chat-info clearfix">' +
    '<span class="direct-chat-name nick-name" style="margin-left: 50px"></span> &nbsp;&nbsp;' +
    '<span class="direct-chat-timestamp report-time"></span>' +
    '</div>' +
    '<img class="direct-chat-img" src="'+getContextPath()+'/resources/images/danger-icon.png" alt="Report">' +
    '<div class="label label-warning report-title" style="margin-left: 10px"; white-space: pre-wrap"></div>' +
    '<span class="label label-primary report-status" style="margin-left: 10px"; white-space: pre-wrap"></span>' +
    '<div class="direct-chat-text report-content"; white-space: pre-wrap"></div>' +
    '</div>'+
    '</div>');



$(document).ready(function() {
    $.datepicker.setDefaults($.datepicker.regional['ja']);
    $(".datepicker").datepicker({ dateFormat: 'yy/mm/dd',changeMonth: true,
        changeYear: true,
        yearRange: '1900:2099'

    });

    $("#wrongAdmInf").show().delay(5000).fadeOut();

    $(document).ready(function(){
        // document.getElementById("childCat").value = localStorage.getItem("child");
    });
    $(window).on('beforeunload', function() {
        // localStorage.setItem("child",document.getElementById("childCat").value);
    });

    $('.search-paging').click(function (events) {

        events.preventDefault();
        var page = $(this).data('page');
        var searching = document.URL.indexOf("&page=");
        var url = document.URL;
        var result = document.URL;

        if (searching > 0) {
            result = url.substring(0, searching) + "&page=" + page;
        }
        if (url.indexOf("?page=") >= 0) {
            result = url.substring(0, url.indexOf("?page=")) + "?page=" + page;
        }

        if (url.indexOf("page=") == -1) {
            result = url + "&page=" + page;
        }

        window.location = result;
    });

    $('.search-paging2').click(function (events) {

        events.preventDefault();
        var pageNumber = $(this).data('page');
        var searchQuestionSymbolAdminListLast = document.URL.lastIndexOf("list-admin-user");
        var searchQuestionSymbolNgListLast = document.URL.lastIndexOf("/");
        var searching = document.URL.indexOf("&pageNumber=");
        var url = document.URL;
        if (searchQuestionSymbolAdminListLast > 0 ) {
                url = url + "?";
        }

        if (url.substr(searchQuestionSymbolNgListLast+1) === "list-admin-ng" ) {
            url = url + "?";
        }
        var result = document.URL;
        if (searching > 0) {
            result = url.substring(0, searching) + "&pageNumber=" + pageNumber;
        }

        if (url.includes("?pageNumber=")) {
            result = url.substring(0, url.indexOf("?pageNumber=")) + "?pageNumber=" + pageNumber;
        }

        if (!url.includes("pageNumber=")) {
            result = url + "&pageNumber=" + pageNumber;
        }

        window.location = result;
    });

    $('#post-oke').click(function (events) {
        $('#postOkeConfirmModal').modal("show");
    });

    $('#postOkeConfirmBtn').click(function (events) {
        var postId = $('#postPatrolId').val();
        var sequentNumber = parseInt($('#currentPatrolNumber').val());

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        console.log("PostId: " + postId);
        console.log("SequentNumber: " + sequentNumber);
        $.ajax({
            type: "PUT",
            contentType : "application/json",
            url: ctx + "/backend/post-patrol/ok?postId=" + postId + "&sequentNumber=" + sequentNumber,
            cache: false,
            success: function(response) {
                var isPatrolSequent = $('#isPatrolSequent').val();
                if (isPatrolSequent == 0) {
                    window.location = window.location.pathname.substring(0, window.location.pathname.indexOf("post-patrol-detail")) + "post-patrol?page=0";
                }
                if (isPatrolSequent == 1) {
                    if ($('#currentPatrolNumber').val() == $('#maxSequentNumber').val()) {
                        window.location = window.location.pathname.substring(0, window.location.pathname.indexOf("post-patrol-detail")) + "post-patrol?page=0";
                    } else {
                        var url = document.URL;
                        var nextSequent = sequentNumber + parseInt("1");
                        window.location = url.substring(0, url.indexOf("sequentNumber=")) + "sequentNumber=" + nextSequent;
                    }
                }
            },
            error: function (xhr, statusText, err) {
                if (xhr.status == 409) {
                    $("#conflictProcess").modal("show");
                }
            }
        });

    });

    $('.get-child-categories').change(function () {
        var parentVal = $("#parentCat").val();
        if (parentVal.length === 0) {
            $('#childCat').html("");
            $('#childCat').prop('disabled', true);
            return false;
        }else {
            $('#childCat').prop('disabled', false);
        }

        $.ajax({
            type: "GET",
            url: "web/categories",
            cache: false,
            data:'parentId=' + $("#parentCat").val(),
            success: function(response){
                $('#childCat').html("");
                $("#childCat").append("<option value=''>すべて</option>")
                for (var i = 0; i < response.length; i++){
                    $("#childCat").append('<option value='+response[i].categoryId+'>'+response[i].categoryName+'</option>');
                }
            },
            error: function(){
                console.log("error");
            }
        });
    });

    $('.pending-pop').click(function () {
        $.ajax({
            type: "GET",
            url: ctx + "/backend/web/memo-post",
            cache: false,
            data:'postId=' + $("#postPatrolId").val(),
           success: function (response) {
               document.getElementById("ngPopTitle").innerHTML = "<b>【保留対応】</b>";
               document.getElementById("footerWarningTxt").innerHTML = "";
               $('#ngPopup').attr('data-patrol-type', 1);
               $("#ngPopup #postPatrolMemoId").html("");
               for (var i = 0; i < response.length; i++) {
                   fetchDataIntoMemoModal(response[i], '#ngPopup', '#postPatrolMemoId');
               }

               $("#difReasonErr").html("");
               $("#ngPopup").modal("show");
               $('#postBodyConfModal').html("保留状態としますか？")
           },
            error: function () {
                console.log("fail");
            }
        });
    });

    $('.ng-pop').click(function () {
        $.ajax({
            type: "GET",
            url: ctx + "/backend/web/memo-post",
            cache: false,
            data:'postId=' + $("#postPatrolId").val(),
            success: function (response) {
                document.getElementById("ngPopTitle").innerHTML = "<b>【NG 対応】</b>";
                document.getElementById("footerWarningTxt").innerHTML = "ユーザー に 通知 が 送信 さ れ ます";
                $('#ngPopup').attr('data-patrol-type', 2);
                $("#ngPopup #postPatrolMemoId").html("");
                for (var i = 0; i < response.length; i++) {
                    fetchDataIntoMemoModal(response[i], '#ngPopup', '#postPatrolMemoId');
                }

                $("#difReasonErr").html("");
                $("#ngPopup").modal("show");
                $('#postBodyConfModal').html("違反理由を通報しますか？")
            },
            error: function () {
                console.log("fail");
            }
        });
    });

    $(".save-post-memo").click(function() {
        var post_memo = {}
        post_memo["postId"] = $("#postPatrolId").val();
        post_memo["content"] = $("#memoCont").val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/web/process-post-patrol",
            data : JSON.stringify(post_memo),
            timeout : 100000,
            success: function(response) {
                fetchDataIntoMemoModal(response, '#allMemPostOfUser', '#memoPostHis');
                document.getElementById("memoCont").value = "";
            },
            error: function(e){
                console.log("ERROR: " + e);
            }
        });

    });

    $(".save-ng-memo").click(function() {
        var post_memo = {}
        post_memo["postId"] = $("#postPatrolId").val();
        post_memo["content"] = $("#postMemCont").val();
        post_memo["notSuitable"] = ($("#notSuitable").is(':checked')) ? $("#notSuitable").val() : null;
        post_memo["sensitiveInf"] = ($("#sensitiveInf").is(':checked')) ? $("#sensitiveInf").val() : null;;
        post_memo["slander"] = ($("#slander").is(':checked')) ? $("#slander").val() : null;;
        post_memo["cheat"] = ($("#cheat").is(':checked')) ? $("#cheat").val() : null;;
        post_memo["other"] = ($("#other").is(':checked')) ? $("#other").val() : null;;
        post_memo["difReason"] = $("#difReason").val();
        post_memo["patrolType"] = $('#ngPopup').attr('data-patrol-type');
        post_memo["sequentNumber"] = $('#currentPatrolNumber').val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/web/process-post-patrol",
            data : JSON.stringify(post_memo),
            timeout : 100000,
            success: function(response) {
                fetchDataIntoMemoModal(response, '#ngPopup', '#postPatrolMemoId');
                document.getElementById("postMemCont").value = "";
                var isPatrolSequent = $('#isPatrolSequent').val();
                if (isPatrolSequent == 0) {
                    window.location = window.location.pathname.substring(0, window.location.pathname.indexOf("post-patrol-detail")) + "post-patrol?page=0";
                }
                if (isPatrolSequent == 1) {
                    if ($('#currentPatrolNumber').val() == $('#maxSequentNumber').val()) {
                        window.location = window.location.pathname.substring(0, window.location.pathname.indexOf("post-patrol-detail")) + "post-patrol?page=0";
                    } else {
                        var url = document.URL;
                        var nextSequent = parseInt($('#currentPatrolNumber').val()) + parseInt("1");
                        window.location = url.substring(0, url.indexOf("sequentNumber=")) + "sequentNumber=" + nextSequent;
                    }
                }
            },
            error: function(){

            }
        });

    });

    $('.all-mem-pop').click(function () {
        $.ajax({
            type: "GET",
            url: ctx + "/backend/web/post/patrol-history",
            cache: false,
            data:'postId=' + $("#postPatrolId").val(),
            success: function(response){
                $("#allMemPostOfUser .direct-chat-msg").html("");

                for (var i = 0; i < response.length; i++) {
                    fetchDataIntoMemoModal(response[i], '#allMemPostOfUser', '#memoPostHis');
                }
                $('#memContErr').html("");
                $('#patrolPostDetail #allMemPostOfUser #memoCont').val('');
                $("#allMemPostOfUser").modal("show");
            },
            error: function(e){
                console.log('Error while request..' + e);
            }
        });
    });

    $('#memoQaBtn').click(function () {
        $.ajax({
            type: "GET",
            url: ctx + "/backend/web/memo-qa",
            cache: false,
            data:'qaId=' + $('#qaDetailId').val(),
            success: function(response){
                $("#qaMemoModal .direct-chat-msg").html("");

                for (var i = 0; i < response.length; i++) {
                    fetchDataIntoMemoModal(response[i], '#qaMemoModal', '.direct-chat-msg');
                }
                $('#qaMemoModal').modal("show");
            },
            error: function(e){
                console.log('Error while request..'+ e);
            }
        });
    });

    $(".save-qa-memo").click(function() {
        var qa_memo = {}
        qa_memo["qaId"] = $('#qaDetailId').val();
        qa_memo["content"] = $("#qaMemCont").val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/web/save-qa-memo",
            data : JSON.stringify(qa_memo),
            timeout : 100000,
            success: function(response){
                fetchDataIntoMemoModal(response, '#qaMemoModal', '.direct-chat-msg');
                document.getElementById("qaMemCont").value = "";
            },
            error: function(xhr, status, error) {
                var err = eval("(" + xhr.responseText + ")");
                console.log(err);
            }
        });

    });

    $('.get-user-memo').click(function () {
        $.ajax({
            type: "GET",
            url: ctx + "/backend/web/memo-user",
            cache: false,
            data:'userId=' + $("#userDetailId").val(),
            success: function(response){
                $("#memoModal .direct-chat-msg").html("");

                for (var i = 0; i < response.length; i++) {
                    fetchDataIntoMemoModal(response[i], '#memoModal', '.direct-chat-msg');
                }
                $("#memoModal").modal("show");
            },
            error: function(){
                console.log('Error while request..' + e);
            }
        });
    });

    $('.divImgUserProfile').click(function () {
        var srcImg = $(this).find('img').attr('src');
        $('#imgUser').attr("src", srcImg);
    })

    $(".save-user-memo").click(function() {
        var user_memo = {}
        user_memo["userId"] = $(this).attr('data-userid');
        user_memo["content"] = $("#usMemCont").val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/web/save-user-memo",
            data : JSON.stringify(user_memo),
            timeout : 100000,
            success: function(response){
                fetchDataIntoMemoModal(response, '#memoModal', '.direct-chat-msg');
                document.getElementById("usMemCont").value = "";
            },
            error: function(xhr, status, error) {
                var err = eval("(" + xhr.responseText + ")");
                console.log(err);
            }
        });

    });

    function fetchDataIntoMemoModal(response, el_parent, el_child) {
        if (response.content != null) {
            var userRow = $(userMemoRow);
            var content = response.content.replace(/\\n/g, "<br />");
            var userName = response.shmAdminName;

            var createAt = response.createdAt;

            $(userRow).find(".admin-name").text(userName);
            $(userRow).find(".mem-time").text(createAt[0] + '年' + createAt[1] + '月' + createAt[2] + '日   ' + createAt[3]  + ':' + createAt[4] );
            $(userRow).find(".mem-content").text(content);
            var el = el_parent +  ' ' + el_child;
            $(el).append($(userRow).clone());
            $("div#errorUserMemo").text('');
        } else {
            $("div#errorUserMemo").text('メモは入力必須です。');
        }
    }

    function fetchDataIntoReportModal(response, el_parent, el_child) {
        if (response.content != null) {
            var userRow = $(userMemoRow);
            var content = response.content.replace(/\\n/g, "<br />");
            var userName = response.shmAdminName;

            var createAt = response.createdAt;

            $(userRow).find(".admin-name").text(userName);
            $(userRow).find(".mem-time").text(createAt[0] + '年' + createAt[1] + '月' + createAt[2] + '日   ' + createAt[3]  + ':' + createAt[4] );
            $(userRow).find(".mem-content").text(content);
            var el = el_parent +  ' ' + el_child;
            $(el).append($(userRow).clone());
            $("div#errorUserMemo").text('');
        } else {
            $("div#errorUserMemo").text('メモは入力必須です。');
        }
    }

    $(document).on('click', '.btn-modal', function() {
        $(".btn-modal-confirm").attr("href", ($(this).attr("data-href")));
    });

    $(document).on('click', '.showImageClick', function() {
        var imgLink = $(this).attr("data-src-link");
        var postName = $(this).attr("data-text-1");
        var postUserName = $(this).attr("data-text-2");
        var listLink = $(this).attr("data-list-link").replace("[","").replace("]","");
        var imageIndex = parseInt($(this).attr("data-link-index"));
        var listImage = listLink.split(",");
        var length = listImage.length;

        if(imageIndex == 0){
            document.getElementById("imageFooter_BackImage").style.visibility = "hidden";
        }else{
            document.getElementById("imageFooter_BackImage").style.visibility = "visible";
        }

        if(imageIndex == length - 1){
            document.getElementById("imageFooter_NextImage").style.visibility = "hidden";
        }else {
            document.getElementById("imageFooter_NextImage").style.visibility = "visible";
        }

        $("#imageFooter_PostName").text(postName);
        $("#imageFooter_PostUserName").text(postUserName);

        if(imageIndex >= 0 && listLink != null && imageIndex < length){
            imgLink = listImage[imageIndex];
        }

        document.getElementById("imageFooter_PostImageList").value = $(this).attr("data-list-link");
        document.getElementById("imageFooter_ImageIndex").value = $(this).attr("data-link-index");

        $("#postImgModel").find("img").attr("src", imgLink);
        if(imgLink && imgLink.indexOf('/img/null') !== -1){
            return;
        }
        $("#postImgModel").modal("show");

    });

    $(document).on('click', '.showImageAvatarClick', function() {
        var imgLink = $(this).attr("data-src-link");
        var listLink = $(this).attr("data-list-link").replace("[","").replace("]","");
        var imageIndex = parseInt($(this).attr("data-link-index"));
        var listImage = listLink.split(",");
        var length = listImage.length;
        if(imageIndex >= 0 && listLink != null && imageIndex < length){
            imgLink = listImage[imageIndex];
        }
        $("#imgAvartarModel").find("img").attr("src", imgLink);
        if(imgLink && imgLink.indexOf('/img/null') !== -1){
            return;
        }
        $("#imgAvartarModel").modal("show");
    });

    $(document).on('click', '#imageFooter_BackImage', function() {
        var listLink = document.getElementById("imageFooter_PostImageList").value.replace("[","").replace("]","");
        var imageIndex = parseInt(document.getElementById("imageFooter_ImageIndex").value);
        if(listLink != null && imageIndex > 0){
            var listImage = listLink.split(",");
            $("#postImgModel").find("img").attr("src", listImage[imageIndex - 1]);
            document.getElementById("imageFooter_ImageIndex").value = imageIndex - 1;
            document.getElementById("imageFooter_NextImage").style.visibility = "visible";
            imageIndex = imageIndex - 1;
        }
        if(imageIndex == 0){
            document.getElementById("imageFooter_BackImage").style.visibility = "hidden";
        }
    });

    $(document).on('click', '#imageFooter_NextImage', function() {
        var listLink = document.getElementById("imageFooter_PostImageList").value.replace("[","").replace("]","");
        var imageIndex = parseInt(document.getElementById("imageFooter_ImageIndex").value);
        if(listLink != null && imageIndex >= 0){
            var listImage = listLink.split(",");
            var length = listImage.length;
            if(imageIndex < length - 1){
                $("#postImgModel").find("img").attr("src", listImage[imageIndex+1]);
                document.getElementById("imageFooter_ImageIndex").value = imageIndex+1;
                document.getElementById("imageFooter_BackImage").style.visibility = "visible";
                imageIndex = imageIndex + 1;
            }
            if(imageIndex == (length - 1)) {
                document.getElementById("imageFooter_NextImage").style.visibility = "hidden";
            }
        }
    });

});
function getContextPath() {
    return window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
}
function isEmpty(str) {
    return str === undefined || str === null || str === '';
}

function rejectNullValue(str){
    if (str === undefined || str === null || str === '')
        return '';
    else
        return str;
}

function validateDateFormat(date) {
    var dateRegex = /^\d{4}[\/](0?[1-9]|1[012])[\/](0?[1-9]|[12][0-9]|3[01])$/;
    return dateRegex.test(date);
}

function validDateWithTime(str) {
    return moment(str,'YYYY/MM/DD H:i').isValid();
}

function isNumberInt(str) {
    var intRegex = /^\d+$/;
    if(str === null || str === '' || intRegex.test(str)){
        return true;
    }
    return false;
}

var entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
    '`': '&#x60;',
    '=': '&#x3D;'
};

function escapeHtml (string) {
    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });
}

function loadingContent() {
    $("div#divLoading").addClass('show');
}
function removeLoadingContent() {
    $("div#divLoading").removeClass('show');
}


