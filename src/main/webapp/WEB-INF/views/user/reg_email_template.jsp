<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

</head>
<body>
<script type="text/javascript">
    function getParameterByName(name, url) {
        if (!url) {
            url = window.location.href;
        }
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    function iOS() {

        var iDevices = [
            'iPad Simulator',
            'iPhone Simulator',
            'iPod Simulator',
            'iPad',
            'iPhone',
            'iPod'
        ];

        if (!!navigator.platform) {
            while (iDevices.length) {
                if (navigator.platform === iDevices.pop()) {
                    return true;
                }
            }
        }

        return false;
    }

    var isRedirect = false;
    if (isRedirect == false) {
        <%@ page language="java" import="java.util.*" %>
        <%@ page import = "java.util.ResourceBundle" %>
        <% ResourceBundle resource = ResourceBundle.getBundle("appEnv");
            String appUrlActivation=resource.getString("app.url.activation"); %>

        isRedirect = true;
        var url = window.location.href;
        var tokenValue = getParameterByName('token');
        var rootUrl = '<%= appUrlActivation %>';

        setTimeout(function () {
            window.location = rootUrl + tokenValue;
        }, 100);
    }
</script>
</body>
</html>
