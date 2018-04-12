<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="HandheldFriendly" content="true">
    <script src="${pageContext.request.contextPath}/resources/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <style>
        /* Tablet Landscape */
        @media screen and (max-width: 1060px) {
            #primary { width:67%; }
            #secondary { width:30%; margin-left:3%;}
        }
        /* Tabled Portrait */
        @media screen and (max-width: 768px) {
            #primary { width:100%; }
            #secondary { width:100%; margin:0; border:none; }
        }

        @media (min-width: 640px) { body {font-size:1rem;} }
        @media (min-width:960px) { body {font-size:1.2rem;} }
        @media (min-width:1100px) { body {font-size:1.5rem;} }
    </style>
</head>
<body style="background: ${backgroundColor};">
<div id="titlePage" style="margin: 10px 0px;display: ${titleDisplay}">${title}</div>
<img id="imagePage" style="width: 100%" src="${imageUrl}">
<div id="contentPage" style="margin: 10px 0px;">${content}</div>
<script type="text/javascript">
//    var font = $("#contentPage > font").first().attr('face');
//    $('#titlePage').css('font-family', font);
</script>
</body>
</html>
