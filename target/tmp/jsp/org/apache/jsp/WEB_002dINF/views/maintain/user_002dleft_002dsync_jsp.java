/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: jetty/9.4.0.v20161208
 * Generated at: 2018-03-27 17:13:56 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.WEB_002dINF.views.maintain;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class user_002dleft_002dsync_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent,
                 org.apache.jasper.runtime.JspSourceImports {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.HashMap<java.lang.String,java.lang.Long>(9);
    _jspx_dependants.put("file:/C:/Users/buixu/.m2/repository/taglibs/standard/1.1.2/standard-1.1.2.jar", Long.valueOf(1499351103894L));
    _jspx_dependants.put("jar:file:/C:/Users/buixu/.m2/repository/org/springframework/spring-webmvc/4.3.6.RELEASE/spring-webmvc-4.3.6.RELEASE.jar!/META-INF/spring-form.tld", Long.valueOf(1485324988000L));
    _jspx_dependants.put("file:/C:/Users/buixu/.m2/repository/net/sargue/java-time-jsptags/1.1.3/java-time-jsptags-1.1.3.jar", Long.valueOf(1499351151017L));
    _jspx_dependants.put("jar:file:/C:/Users/buixu/.m2/repository/net/sargue/java-time-jsptags/1.1.3/java-time-jsptags-1.1.3.jar!/META-INF/javatime.tld", Long.valueOf(1483583992000L));
    _jspx_dependants.put("jar:file:/C:/Users/buixu/.m2/repository/org/springframework/spring-webmvc/4.3.6.RELEASE/spring-webmvc-4.3.6.RELEASE.jar!/META-INF/spring.tld", Long.valueOf(1485324988000L));
    _jspx_dependants.put("file:/C:/Users/buixu/.m2/repository/org/springframework/spring-webmvc/4.3.6.RELEASE/spring-webmvc-4.3.6.RELEASE.jar", Long.valueOf(1499351046052L));
    _jspx_dependants.put("jar:file:/C:/Users/buixu/.m2/repository/org/springframework/security/spring-security-taglibs/4.0.1.RELEASE/spring-security-taglibs-4.0.1.RELEASE.jar!/META-INF/security.tld", Long.valueOf(1429699578000L));
    _jspx_dependants.put("jar:file:/C:/Users/buixu/.m2/repository/taglibs/standard/1.1.2/standard-1.1.2.jar!/META-INF/c.tld", Long.valueOf(1098685890000L));
    _jspx_dependants.put("file:/C:/Users/buixu/.m2/repository/org/springframework/security/spring-security-taglibs/4.0.1.RELEASE/spring-security-taglibs-4.0.1.RELEASE.jar", Long.valueOf(1499351088919L));
  }

  private static final java.util.Set<java.lang.String> _jspx_imports_packages;

  private static final java.util.Set<java.lang.String> _jspx_imports_classes;

  static {
    _jspx_imports_packages = new java.util.HashSet<>();
    _jspx_imports_packages.add("javax.servlet");
    _jspx_imports_packages.add("javax.servlet.http");
    _jspx_imports_packages.add("javax.servlet.jsp");
    _jspx_imports_classes = null;
  }

  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fform_005fform_0026_005fstyle_005frole_005fmethod_005fid_005fclass_005faction;

  private volatile javax.el.ExpressionFactory _el_expressionfactory;
  private volatile org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public java.util.Set<java.lang.String> getPackageImports() {
    return _jspx_imports_packages;
  }

  public java.util.Set<java.lang.String> getClassImports() {
    return _jspx_imports_classes;
  }

  public javax.el.ExpressionFactory _jsp_getExpressionFactory() {
    if (_el_expressionfactory == null) {
      synchronized (this) {
        if (_el_expressionfactory == null) {
          _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
        }
      }
    }
    return _el_expressionfactory;
  }

  public org.apache.tomcat.InstanceManager _jsp_getInstanceManager() {
    if (_jsp_instancemanager == null) {
      synchronized (this) {
        if (_jsp_instancemanager == null) {
          _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
        }
      }
    }
    return _jsp_instancemanager;
  }

  public void _jspInit() {
    _005fjspx_005ftagPool_005fform_005fform_0026_005fstyle_005frole_005fmethod_005fid_005fclass_005faction = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _005fjspx_005ftagPool_005fform_005fform_0026_005fstyle_005frole_005fmethod_005fid_005fclass_005faction.release();
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
      throws java.io.IOException, javax.servlet.ServletException {

    final java.lang.String _jspx_method = request.getMethod();
    if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method) && !javax.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {
      response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "JSPs only permit GET POST or HEAD");
      return;
    }

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<ul class=\"breadcrumb\">\r\n");
      out.write("    <li><a href=\"");
      out.print( request.getContextPath());
      out.write("/backend/\">ダッシュ ボード</a></li>\r\n");
      out.write("    <li><a href=\"");
      out.print( request.getContextPath());
      out.write("/backend/user-left-sync\">手動退会処理</a></li>\r\n");
      out.write("</ul>\r\n");
      out.write("<script>var ctx = \"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${pageContext.request.contextPath}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null));
      out.write("\";</script>\r\n");
      out.write("<div class=\"home-page\" style=\"margin-top: 20px\">\r\n");
      out.write("    <div class=\"search-posts\">\r\n");
      out.write("        ");
      if (_jspx_meth_form_005fform_005f0(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("        <!-- Modal -->\r\n");
      out.write("        <div class=\"modal fade\" id=\"confirmModal\" role=\"dialog\">\r\n");
      out.write("            <div class=\"modal-dialog\">\r\n");
      out.write("                <!-- Modal content-->\r\n");
      out.write("                <div class=\"modal-content\">\r\n");
      out.write("                    <div class=\"modal-header bg-primary\" style=\"border-top-left-radius: 0px; border-top-right-radius: 0px;\">\r\n");
      out.write("                        <h4 class=\"modal-title\"><i class=\"glyphicon glyphicon-question-sign\"></i> 手動退会処理</h4>\r\n");
      out.write("                    </div>\r\n");
      out.write("                    <div class=\"modal-body\">\r\n");
      out.write("                        <p>退会処理を実行しますか？</p>\r\n");
      out.write("                    </div>\r\n");
      out.write("                    <div class=\"modal-footer\">\r\n");
      out.write("                        <button type=\"button\" id=\"buttonContinue\" class=\"btn btn-primary\" data-dismiss=\"modal\" onclick=\"doSyncData()\">\r\n");
      out.write("                            実行する\r\n");
      out.write("                        </button>\r\n");
      out.write("                        <button type=\"button\" id=\"buttonClose\" class=\"btn btn-default\" data-dismiss=\"modal\">閉じる\r\n");
      out.write("                        </button>\r\n");
      out.write("                    </div>\r\n");
      out.write("                </div>\r\n");
      out.write("            </div>\r\n");
      out.write("        </div>\r\n");
      out.write("    </div>\r\n");
      out.write("</div>\r\n");
      out.write("<style>\r\n");
      out.write("    #systemConfigKpiSync{\r\n");
      out.write("        color: white;\r\n");
      out.write("        position: relative;\r\n");
      out.write("        display: block;\r\n");
      out.write("        background-color: transparent;\r\n");
      out.write("        box-sizing: border-box;\r\n");
      out.write("    }\r\n");
      out.write("</style>\r\n");
      out.write("<script>\r\n");
      out.write("    $(document).ready(function() {\r\n");
      out.write("        $(\".subMenu\").toggle(\"fast\");\r\n");
      out.write("    });\r\n");
      out.write("    function doSyncData() {\r\n");
      out.write("                $('#syncModel').submit();\r\n");
      out.write("    }\r\n");
      out.write("</script>\r\n");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_form_005fform_005f0(javax.servlet.jsp.PageContext _jspx_page_context)
          throws java.lang.Throwable {
    javax.servlet.jsp.PageContext pageContext = _jspx_page_context;
    javax.servlet.jsp.JspWriter out = _jspx_page_context.getOut();
    //  form:form
    org.springframework.web.servlet.tags.form.FormTag _jspx_th_form_005fform_005f0 = (org.springframework.web.servlet.tags.form.FormTag) _005fjspx_005ftagPool_005fform_005fform_0026_005fstyle_005frole_005fmethod_005fid_005fclass_005faction.get(org.springframework.web.servlet.tags.form.FormTag.class);
    try {
      _jspx_th_form_005fform_005f0.setPageContext(_jspx_page_context);
      _jspx_th_form_005fform_005f0.setParent(null);
      // /WEB-INF/views/maintain/user-left-sync.jsp(22,8) null
      _jspx_th_form_005fform_005f0.setDynamicAttribute(null, "class", "");
      // /WEB-INF/views/maintain/user-left-sync.jsp(22,8) name = id type = null reqTime = true required = false fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
      _jspx_th_form_005fform_005f0.setId("syncModel");
      // /WEB-INF/views/maintain/user-left-sync.jsp(22,8) null
      _jspx_th_form_005fform_005f0.setDynamicAttribute(null, "role", "form");
      // /WEB-INF/views/maintain/user-left-sync.jsp(22,8) name = action type = null reqTime = true required = false fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
      _jspx_th_form_005fform_005f0.setAction("sync-user-process");
      // /WEB-INF/views/maintain/user-left-sync.jsp(22,8) name = method type = null reqTime = true required = false fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
      _jspx_th_form_005fform_005f0.setMethod("POST");
      // /WEB-INF/views/maintain/user-left-sync.jsp(22,8) null
      _jspx_th_form_005fform_005f0.setDynamicAttribute(null, "style", "margin-top: 20px");
      int[] _jspx_push_body_count_form_005fform_005f0 = new int[] { 0 };
      try {
        int _jspx_eval_form_005fform_005f0 = _jspx_th_form_005fform_005f0.doStartTag();
        if (_jspx_eval_form_005fform_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
          do {
            out.write("\r\n");
            out.write("        <div class=\"row\">\r\n");
            out.write("            <div class=\"col-md-12\" style=\"text-align: left; line-height: 34px; font-weight: bold\">\r\n");
            out.write("                手動退会処理\r\n");
            out.write("            </div>\r\n");
            out.write("        </div>\r\n");
            out.write("            <div class=\"row\">\r\n");
            out.write("                <div class=\"col-md-8\" style=\"color: green\"><b>");
            out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${successMsg}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null));
            out.write("</b></div>\r\n");
            out.write("            </div>\r\n");
            out.write("        <div class=\"row\">\r\n");
            out.write("            <div class=\"col-md-2\" style=\"text-align: center; line-height: 34px; font-weight: bold\">\r\n");
            out.write("                <label></label>\r\n");
            out.write("            </div>\r\n");
            out.write("        </div>\r\n");
            out.write("        <div class=\"row\">\r\n");
            out.write("            <div class=\"col-md-3 \">\r\n");
            out.write("                <input type=\"button\" id=\"syncButton\" class=\"btn btn-primary\" data-toggle=\"modal\"\r\n");
            out.write("                       data-target=\"#confirmModal\" value=\"同期する\">\r\n");
            out.write("                </input>\r\n");
            out.write("            </div>\r\n");
            out.write("        </div>\r\n");
            out.write("        ");
            int evalDoAfterBody = _jspx_th_form_005fform_005f0.doAfterBody();
            if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
              break;
          } while (true);
        }
        if (_jspx_th_form_005fform_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
          return true;
        }
      } catch (java.lang.Throwable _jspx_exception) {
        while (_jspx_push_body_count_form_005fform_005f0[0]-- > 0)
          out = _jspx_page_context.popBody();
        _jspx_th_form_005fform_005f0.doCatch(_jspx_exception);
      } finally {
        _jspx_th_form_005fform_005f0.doFinally();
      }
    } finally {
      _005fjspx_005ftagPool_005fform_005fform_0026_005fstyle_005frole_005fmethod_005fid_005fclass_005faction.reuse(_jspx_th_form_005fform_005f0);
    }
    return false;
  }
}
