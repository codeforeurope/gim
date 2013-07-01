<%@page import="java.lang.reflect.Field"%>
<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
var WebSettings = new Array();
<%
    Field[] fields = mobiwork.cfg.WebSettings.class.getFields();
    for (Field field : fields)
    {
        if (!field.getName().startsWith("KEY_"))
        {
            String value = null;
            Object o = field.get(null);
            if (o != null)
            {
                value = o.toString();
                Class type = field.getType();
                if (String.class.equals(type))
                    value = "\"" + value + "\"";
            }
        
%>WebSettings["<%=field.getName()%>"] = <%=value%>;<%
        }
    }
%>

var mobiwork_image_base = "../${sessionScope['mobiworkUser'].imagesFolderUri}/";
var application_context_path = "<%=request.getContextPath()%>";
var mobiwork_user_id = ${sessionScope['mobiworkUser'].id};

function SeverityObj(intValue, color, desc)
{
    this.intValue = intValue;
    this.color = color;
    this.desc = desc.toUpperCase();
}

var Severity = new Array();
<%
    sistematica.mobiworkdb.entity.Severity[] severities = sistematica.mobiworkdb.entity.Severity.values();
    for (sistematica.mobiworkdb.entity.Severity severity : severities)
    {
%>Severity['<%=severity%>'] = new SeverityObj(<%=severity.getIntValue()%>,'<%=severity.getColor()%>','<%=severity.getDesc()%>');<%
    }
%>