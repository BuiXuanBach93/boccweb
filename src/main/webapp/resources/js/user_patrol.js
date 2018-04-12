/**
 * @author NguyenThuong on 4/18/2017.
 */
var processStatus = '';
jQuery(document).ready(function ($) {

    if (!$('#processStatusNotYet').is(':checked')) {
        $('#usRepairCplDate').show().css("visibility", "visible");
        $('#completedSttCol').hide().css("visibility", "hide");
        $('.compValCol').hide().css("visibility", "hide");
        $('#updatedCheckbox').removeClass('importamtHide');
    } else {
        $('#usRepairCplDate').hide().css("visibility", "hide");
        $('#censoredFromDate').datepicker('setDate', null);
        $('#censoredToDate').datepicker('setDate', null);
        $('#completedSttCol').show().css("visibility", "visible");
        $('.compValCol').show().css("visibility", "visible");
        $('#updatedCheckbox').addClass('importamtHide');

    }

    $('#exportUserPatrolCsvBtn').click(function () {
        if (validateUserPatrolSearchForm()) {
            $('#user_patrol_search_form').attr('action', ctx + '/backend/users-patrol/csv');
            $('#user_patrol_search_form').submit();
            $('#user_patrol_search_form').attr('action', ctx + '/backend/patrol/handle-user/search');
        }
    });

    $('#processStatusNotYet').on('change', function () {
        processStatus = $('#processStatusNotYet').val();
        $('#updatedCheckbox').addClass('importamtHide');
    });
    $('#processStatusDoing').on('change', function () {
        $('#datePatrol').html('対応中変更日時');
        processStatus = $('#processStatusDoing').val();
        $('#updatedCheckbox').removeClass('importamtHide');
    });
    $('#processStatusDone').on('change', function () {
        $('#datePatrol').html('対応完了日時');
        processStatus = $('#processStatusDone').val();
        $('#updatedCheckbox').removeClass('importamtHide');
    });

    ajaxSearchForm(0);

    $("#bth-search").click(function (event) {
        enableSearchButton(false);

        // Prevent the form from submitting via the browser.
        event.preventDefault();

        if (validateUserPatrolSearchForm()) {
            ajaxSearchForm(0);
        }

    });

    $("#userOkBtn").click(function (event) {
        openOkeConfirmModal();
    });

    $("#userOkeConfirmBtn").click(function (event) {
        ajaxUserOk();
    });

    $('#userPendingBtn').click(function () {
        ajaxUserPending();
    });

    $('#userNgBtn').click(function () {
        ajaxUserNg();
    });
    $('#userAllMemoBtn').click(function () {
        ajaxUserAllMemo();
    });

    $("#save-user-memo").click(function () {
        var post_memo = {}
        post_memo["userId"] = $("#userPatrolId").html();
        post_memo["content"] = $("#userMemoCont").val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function (e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: ctx + "/backend/web/process-user-patrol",
            data: JSON.stringify(post_memo),
            timeout: 100000,
            success: function (response) {
                fetchDataIntoMemoModal(response, '#allMemoOfUser', '#memoUserHis');
                document.getElementById("userMemoCont").value = "";
            },
            error: function () {

            }
        });

    });

    $("#save-user-ng").click(function () {
        var post_memo = {}
        post_memo["userId"] = $("#userPatrolId").html();
        post_memo["content"] = $("#userNGMemoCont").val();
        post_memo["notSuitable"] = ($("#userNotSuitable").is(':checked')) ? $("#userNotSuitable").val() : null;
        post_memo["sensitiveInf"] = ($("#userSensitiveInf").is(':checked')) ? $("#userSensitiveInf").val() : null;
        post_memo["slander"] = ($("#userSlander").is(':checked')) ? $("#userSlander").val() : null;
        post_memo["cheat"] = ($("#userCheat").is(':checked')) ? $("#userCheat").val() : null;
        post_memo["other"] = ($("#userOtherReason").is(':checked')) ? $("#userOtherReason").val() : null;
        post_memo["difReason"] = $("#userDifReason").val();
        post_memo["ctrlStatus"] = $('#ngPopupUser').attr('data-user-patrol-type');
        post_memo["sequentNumber"] = $('#userCurrentPatrolNumber').val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function (e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: ctx + "/backend/web/process-user-patrol",
            data: JSON.stringify(post_memo),
            timeout: 100000,
            success: function (response) {
                fetchDataIntoMemoModal(response, '#ngPopupUser', '#userPatrolMemoId');
                document.getElementById("userNGMemoCont").value = "";
                var isPatrolSequent = $('#isUserPatrolSequent').val();
                if (isPatrolSequent == 0) {
                    window.location = window.location.pathname.substring(0, window.location.pathname.indexOf("patrol/handle-user")) + "patrol/handle-user";
                }
                if (isPatrolSequent == 1) {
                    if ($('#userCurrentPatrolNumber').val() == $('#userMaxSequentNumber').val()) {
                        var url = ctx + "/backend/patrol/handle-user";
                        $(location).attr('href', url);
                    } else {
                        var url = document.URL;
                        var nextSequent = parseInt($('#userCurrentPatrolNumber').val()) + parseInt("1");
                        window.location = url.substring(0, url.indexOf("sequentNumber=")) + "sequentNumber=" + nextSequent;
                    }
                }
            },
            error: function () {

            }
        });

    });

});

function submitBtnSearch() {
    if (validateUserPatrolSearchForm()) {
        currentPage = 0;
        ajaxSearchForm(0);
    }
}

function validateUserPatrolSearchForm() {
    var reg = /^\d{4}[\/](0?[1-9]|1[012])[\/](0?[1-9]|[12][0-9]|3[01])$/
    var flag = true;
    if(!isEmpty($('#updatedAtFrom').val()) && !reg.test($('#updatedAtFrom').val())) {
        $('#updatedAtFromErr').html("入力フォーマットが違います");
        flag = false;
    } else {
        $('#updatedAtFromErr').html("");
    }
    if(!isEmpty($("#updatedAtTo").val()) && !reg.test($('#updatedAtTo').val())) {
        $('#updatedAtToErr').html("入力フォーマットが違います");
        flag = false;
    } else {
        $('#updatedAtToErr').html("");
    }
    if(!isEmpty($("#censoredFrom").val()) && !reg.test($('#censoredFrom').val())) {
        $('#censoredFromErr').html("入力フォーマットが違います");
        flag = false;
    } else {
        $('#censoredFromErr').html("");
    }
    if(!isEmpty($("#censoredTo").val()) && !reg.test($('#censoredTo').val())) {
        $('#censoredToErr').html("入力フォーマットが違います");
        flag = false;
    } else {
        $('#censoredToErr').html("");
    }
    return flag;
}

function validateSearch(startDate, endDate) {
    var flag = true;
    if (isEmpty(startDate)) {
        $('#errorMsg').html("");
    }
    if (!isEmpty(startDate) && !validateDateFormat(startDate)) {
        $('#errorMsg').html("入力フォーマットが違います")
        flag = false;
    }

    var endDate = $('#updatedAtTo').val();
    if (isEmpty(endDate)) {
        $('#errUpdatedAtToDate').html("");
    }
    if (!isEmpty(endDate) && !validateDateFormat(endDate)) {
        $('#errorMsg').html("入力フォーマットが違います")
        flag = false;
    }

    if (!isEmpty(startDate) && !isEmpty(endDate) && startDate > endDate) {
        $('#errorMsg').html("開始日を終了日の後に設定することはできません。")
        flag = false;
    } else {
        $('#errorMsg').html("");
    }

    return flag;
}

function validateSearch2(startDate, endDate) {
    var flag = true;

    if (isEmpty(startDate)) {
        $('#errorMsg2').html("");
    }
    if (!isEmpty(startDate) && !validateDateFormat(startDate)) {
        $('#errorMsg2').html("入力フォーマットが違います")
        flag = false;
    }

    var endDate = $('#censoredTo').val();
    if (isEmpty(endDate)) {
        $('#errorMsg2').html("");
    }
    if (!isEmpty(endDate) && !validateDateFormat(endDate)) {
        $('#errorMsg2').html("入力フォーマットが違います")
        flag = false;
    }

    if (!isEmpty(startDate) && !isEmpty(endDate) && startDate > endDate) {
        $('#errorMsg2').html("開始日を終了日の後に設定することはできません。")
        flag = false;
    } else {
        $('#errorMsg2').html("");
    }

    return flag;
}


function ajaxUserAllMemo() {
    var url = ctx + "/backend/patrol/handle-user/user-memmo-all";
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        data: 'userId=' + $("#userPatrolId").html(),
        success: function (response) {
            $("#allMemoOfUser .direct-chat-msg").html("");

            for (var i = 0; i < response.length; i++) {
                fetchDataIntoMemoModal(response[i], '#allMemoOfUser', '#memoUserHis');
            }
            $('#memContErr').html("");
            $('#patrolUser #allMemoOfUser #userMemoCont').val('');
            $("#allMemoOfUser").modal("show");
        },
        error: function () {
            console.log('Error while request..');
        }
    });
}

function ajaxUserNg() {
    var url = ctx + "/backend/patrol/handle-user/user-memmo";
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        data: 'userId=' + $("#userPatrolId").html(),
        success: function (response) {
            document.getElementById("userNgPopTitle").innerHTML = "<b>【NG 対応】</b>";
            document.getElementById("userFooterWarningTxt").innerHTML = "ユーザー に 通知 が 送信 さ れ ます";
            $('#ngPopupUser').attr('data-user-patrol-type', 'SUSPENDED');
            $("#ngPopupUser #userPatrolMemoId").html("");
            for (var i = 0; i < response.length; i++) {
                fetchDataIntoMemoModal(response[i], '#ngPopupUser', '#userPatrolMemoId');
            }

            $("#ngPopupUser").modal("show");
            $('#userBodyConfModal').html("違反理由を通報しますか？");
        },
        error: function () {
            console.log("fail");
        }
    });
}

function ajaxUserPending() {
    var url = ctx + "/backend/patrol/handle-user/user-memmo";
    $.ajax({
        type: "GET",
        url: url,
        cache: false,
        data: 'userId=' + $("#userPatrolId").html(),
        success: function (response) {
            document.getElementById("userNgPopTitle").innerHTML = "<b>【保留対応】</b>";
            document.getElementById("userFooterWarningTxt").innerHTML = "";
            $('#ngPopupUser').attr('data-user-patrol-type', 'PENDING');
            $("#ngPopupUser #userPatrolMemoId").html("");
            for (var i = 0; i < response.length; i++) {
                fetchDataIntoMemoModal(response[i], '#ngPopupUser', '#userPatrolMemoId');
            }
            $("#ngPopupUser").modal("show");
            $('#userBodyConfModal').html("保留状態としますか？");
        },
        error: function (e) {
            console.log("fail" + e);
        }
    });
}

function fetchDataIntoMemoModal(response, el_parent, el_child) {
    if (response.content != null) {
        var userRow = $(userMemoRow);
        var content = response.content;
        var userName = response.shmAdminName;

        var createAt = response.createdAt;

        $(userRow).find(".admin-name").text(userName);
        $(userRow).find(".mem-time").text(createAt[0] + '/' + createAt[1] + '/' + createAt[2] + '   ' + createAt[3] + ':' + createAt[4] + ':' + '00');
        $(userRow).find(".mem-content").text(content);
        var el = el_parent + ' ' + el_child;
        $(el).append($(userRow).clone());
    }
}

function openOkeConfirmModal() {
    $('#userOkeConfirmModal').modal("show");
}

function ajaxUserOk() {
    var userId = $("#userPatrolId").html();
    var userOkPath = ctx + "/backend/patrol/handle-user/user-ok";
    var sequentNumber = parseInt($('#userCurrentPatrolNumber').val());
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: userOkPath +"?userId=" + userId + "&sequentNumber=" + sequentNumber,
        cache: false,
        timeout: 100000,
        success: function (data) {
            var isPatrolSequent = $('#isUserPatrolSequent').val();
            if (isPatrolSequent == 0) {
                var url = ctx + "/backend/patrol/handle-user";
                $(location).attr('href', url);
            }
            if (isPatrolSequent == 1) {
                if ($('#userCurrentPatrolNumber').val() == $('#userMaxSequentNumber').val()) {
                    var url = ctx + "/backend/patrol/handle-user";
                    $(location).attr('href', url);
                } else {
                    var url = document.URL;
                    var nextSequent = sequentNumber + parseInt("1");
                    window.location = url.substring(0, url.indexOf("sequentNumber=")) + "sequentNumber=" + nextSequent;
                }
            }
        },
        error: function (xhr, statusText, err) {
            console.log("Loi 409 " + xhr.status);
            if (xhr.status == 409) {
                $("#userConflictProcess").modal("show");
            }
        },
        done: function () {
            console.log("DONE");
        }
    });
}

function ajaxGetNextUserNotOk(currentUserId) {
    var userIdNotOk = '';
    var userNotOk = ctx + "/backend/patrol/handle-user/user-not-ok?currentUserId=" + currentUserId;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: userNotOk,
        dataType: 'json',
        timeout: 100000,
        success: function (data) {
            console.log("SUCCESS: ", data);
            userNotOk = data;
        },
        error: function (e) {
            console.log("ERROR: ", e);
        },
        done: function (e) {
            console.log("DONE");
            enableSearchButton(true);
        }
    });
    return userIdNotOk;
}

function handleUserDetail(userId) {
    var userDetailurl = ctx + "/backend/patrol/handle-user/";
    var url = userDetailurl + userId;
    window.location = url;
}

function enableSearchButton(flag) {
    $("#btn-search").prop("disabled", flag);
}

function buildUserPtrlStatus(userPtrlStatus) {
    var result = '';
    if (userPtrlStatus == 0) {
        result = '未対応';
    }
    else if (userPtrlStatus == 1) {
        result = '対応中';
    } else if (userPtrlStatus == 2) {
        result = '対応完了';
    }
    return result;

}

function buildUserPtrlStatusCensor(userPtrlStatus,userCtrlStatus) {
    var result = '';
    if (userCtrlStatus == 3) {
        result = '修正完了';
    }
    return result;
}

function buildUserCtrlStatus(userCtrlStatus) {
    var result1 = '';
    if (userCtrlStatus == 1) {
        result1 = '保留中';
    }
    return result1;

}

function rejectNullValue1(userNickName) {
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

    return $.text(userNickName);
}
var currentPage = 0;
var totalPages = 1;
function nextPage($this) {
    currentPage++;
    if(currentPage >= totalPages) {
        currentPage = totalPages - 1;
    } else {
        ajaxSearchForm();
    }
}
function prevPage($this) {
    currentPage--;
    if(currentPage < 0) {
        currentPage = 0;
    } else {
        ajaxSearchForm();
    }
}
function ajaxSearchForm() {
    var i = currentPage;
    var processStatus = $("input[name='processStatus']:checked").val();
    processStatus = isEmpty(processStatus) ? '' : processStatus;
    var updatedAtFrom = $("#updatedAtFrom").val();
    updatedAtFrom = isEmpty(updatedAtFrom) ? '' : updatedAtFrom;
    var updatedAtTo = $("#updatedAtTo").val();
    updatedAtTo = isEmpty(updatedAtTo) ? '' : updatedAtTo;
    var censoredFrom = $("#censoredFrom").val();
    censoredFrom = isEmpty(censoredFrom) ? '' : censoredFrom;
    var censoredTo = $("#censoredTo").val();
    censoredTo = isEmpty(censoredTo) ? '' : censoredTo;
    var patrolAdminNames = $("#patrolAdminNames").val();
    patrolAdminNames = isEmpty(patrolAdminNames) ? '' : patrolAdminNames;

    var pending = '';
    if ($("#pending").is(':checked'))
        pending = $("#pending").val();
    var ng = '';
    if ($("#ng").is(':checked'))
        ng = $("#ng").val();
    var userUpdatedAt = '';
    if ($("#userUpdatedAt").is(':checked'))
        userUpdatedAt = $("#userUpdatedAt").val();
    if(!validateSearch(updatedAtFrom, updatedAtTo) || !validateSearch2(censoredFrom, censoredTo)){
    return;
    }

    var url = $("#user_patrol_search_form").attr("action");
    var requestUrl = url + '/' + i + '?processStatus=' + processStatus + '&updatedAtFrom='
        + updatedAtFrom + '&updatedAtTo=' + updatedAtTo + '&censoredFrom=' + censoredFrom
        + '&censoredTo=' + censoredTo + '&patrolAdminNames=' + encodeURIComponent(patrolAdminNames) + '&ng=' + ng
        + '&pending=' + pending
        + '&userUpdatedAt=' + userUpdatedAt;
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: requestUrl,
        dataType: 'json',
        timeout: 100000,
        success: function (data, i) {
            console.log("SUCCESS: ", data);
            if (processStatus == 'UNCENSORED')
                display(data);
            else
                displayWithUpdateAfterCensored(data);
            $('#pageIndex').attr("value", data.number);
            if (parseInt(data.totalElements) == 0) {
                $('#paginationUserPatrol').hide().css('visibility', 'hidden');
            } else {
                $('#paginationUserPatrol').show().css('visibility', 'visible');
            }
        },
        error: function (e) {
            console.log("ERROR: ", e);
        },
        done: function (e) {
            console.log("DONE");
            enableSearchButton(true);
        }
    });
    function cleanData() {
        $('#tableBody tr + td').remove();
    };

    function display(data) {
        var listItems = [];
        // alert(i);
        $.each(data.content, function (name, value) {
            var trElem = "<tr id='tableContent'>"
                + "<td class='textCenter'> <a class='showImageAvatarClick' data-src-link='"+value.userAvatarPath+"' data-link-index='0' data-list-link='["+value.userAvatarPath+"]'> <img src='" + value.userAvatarPath + "' onerror='imgError(this);'  width='40' height='40'/>  </a></td>"
                + "<td class='textCenter'> <a onclick='handleUserDetail(" + value.userId + ")' id='userIdInList' userId='" + value.userId + "' >" + escapeHtml(value.userNickName) + "</a> </td>"
                + "<td class='textCenter'>" + buildUserPtrlStatus(value.userPtrlStatus) + "</td>"
                + "<td class='textCenter'>" + formatDateTime(value.userUpdateAt) + " </td>"
                /*+ "<td class='textCenter compValCol'>" + buildUserPtrlStatusCensor(value.userPtrlStatus,value.userCtrlStatus) + " </td>"*/
                + "<td class='textCenter pendValCol'>" + value.pendingStatusPatrolSite + " </td>"
                + "<td class='textCenter'>" + rejectNullValue(value.patrolAdminName) + " </td></tr>";
            listItems.push(trElem);
        })
        $('thead').nextAll().remove();
        var html = listItems.join();
        $(html).appendTo('#tableBody');
        var maxRecordsInPage = data.size;
        var startElement = data.number*maxRecordsInPage + 1;
        if ((data.number + 1) == data.totalPages) {
            var curElement = data.totalElements;
        } else {
            var curElement = (data.number + 1) *maxRecordsInPage;
        }
        $('#totalElements').text(startElement + ' - ' + curElement + ' の ' + data.totalElements);
        // buildPage(data.totalPages);
        totalPages = data.totalPages;
        checkStt(processStatus);

        if (startElement == 1) {
            $('#prevPageButton').addClass("disabled");
        } else {
            $('#prevPageButton').removeClass("disabled");
        }

        if (curElement == data.totalElements) {
            $('#nextPageButton').addClass("disabled");
        } else {
            $('#nextPageButton').removeClass("disabled");
        }
    }

    function displayWithUpdateAfterCensored(data) {
        var listItems = [];
        $.each(data.content, function (name, value) {
            console.log(processStatus);
            var trElem = "<tr id='tableContent'><td class='textCenter'> <img src='" + value.userAvatarPath + "' onerror='imgError(this);'  width='40' height='40'/> </td>"
                + "<td class='textCenter' > <a onclick='handleUserDetail(" + value.userId + ")' id='userIdInList' userId='" + value.userId + "' >" + escapeHtml(value.userNickName) + "</a> </td>"
                + "<td class='textCenter'>" + buildUserPtrlStatus(value.userPtrlStatus) + "</td>"
                + "<td class='textCenter'>" + formatDateTime(value.userUpdateAt) + " </td>"
                + "<td class='textCenter compValCol'>" + value.userUpdateAfterSuspended + " </td>"
                + "<td class='textCenter pendValCol'>" + value.pendingStatusPatrolSite + " </td>"
                + "<td class='textCenter'>" + rejectNullValue(value.patrolAdminName) + " </td></tr>";
            listItems.push(trElem);
        })
        $('thead').nextAll().remove();
        var html = listItems.join();
        $(html).appendTo('#tableBody');
        var maxRecordsInPage = data.size;
        var startElement = data.number*maxRecordsInPage + 1;
        if ((data.number + 1) == data.totalPages) {
            var curElement = data.totalElements;
        } else {
            var curElement = (data.number + 1) *maxRecordsInPage;
        }
        $('#totalElements').text(startElement + ' - ' + curElement + ' の ' + data.totalElements);
        // buildPage(data.totalPages);
        totalPages = data.totalPages;
        checkStt(processStatus);

        if (startElement == 1) {
            $('#prevPageButton').addClass("disabled");
        } else {
            $('#prevPageButton').removeClass("disabled");
        }

        if (curElement == data.totalElements) {
            $('#nextPageButton').addClass("disabled");
        } else {
            $('#nextPageButton').removeClass("disabled");
        }
    }

    function checkStt(processStatus) {
        if (processStatus == 'UNCENSORED') {
            $('#completedSttCol').hide().css("visibility", "hide");
            $('.compValCol').hide().css("visibility", "hide");
        } else {
            $('#completedSttCol').show().css("visibility", "visible");
            $('.compValCol').show().css("visibility", "visible");
        }
    }

    function formatDateTime(array) {
        if (!isEmpty(array))
            return array[0] + '年' + array[1] + '月' + array[2] + '日 ' + array[3] + '時' + array[4] + '分';
        else
            return '';
    }
}
