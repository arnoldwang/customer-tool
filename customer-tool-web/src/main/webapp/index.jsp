<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.dianping.customer.tool.utils.ConfigUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%

    String[] jsArray={
            "node_modules"
            ,"jquery-vendors"
            ,"services"
            ,"modules"
            ,"index"};
    String[] cssArray={
            "/vendor/bootstrap/css/bootstrap.min.css",
            "/vendor/bootstrap/css/bootstrap-theme.min.css",
            "/vendor/toastr/toastr.css","/asset/index.css"};

    String prefix = "";
    if(System.getProperty("isLocal")!=null){
        prefix ="http://localhost:3002/customer-tool-static/dist";
    } else{
        prefix = ConfigUtils.getJsPath()+ConfigUtils.getJsVersion();
    }
%>
<head>
    <meta charset="utf8">
    <link rel="Shortcut Icon" href="http://j1.s2.dpfile.com/s/res/favicon.5ff777c11d7833e57e01c9d192b7e427.ico" type="image/x-icon">
    <%for(String css:cssArray){%>
    <link href="<%=prefix+css%>" media="all" rel="stylesheet" type="text/css" />
    <%}%>

</head>
<body>

<script>
    var ENV = {
        mock: false,
        path: 'tool'
    }

</script>

<%for(String js:jsArray){%>
<script src="<%=prefix+"/"+js+".js"%>"></script>
<%}%>


</body>
</html>
