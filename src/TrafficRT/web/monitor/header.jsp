<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<style>
    .topmenu_elements {

        vertical-align: middle;
    }
    .topmenu_logo {

        padding-top: 0px;
        vertical-align: middle;

    }
    .selectmenu {
        position: relative;
        top: -2px;
    }

</style>
<script type="text/javascript">

    var _HEADER_HEIGHT = 101;

    function colorMenu(idRif)
    {
        var colore='';
        var pathAct = document.URL.substring(document.URL.lastIndexOf("/") + 1);
        var funzione=idRif;
        funzione = funzione.substring(funzione.lastIndexOf("/") + 1);
        if(pathAct==funzione)
        {
            colore = '#1D186D';
        }
        else
        {
            colore = '#FFFFFF';
        }

        var elemento =document.getElementById(idRif)
        elemento.style.color=colore;
    }
</script>

<div align="left" style="float:left; height: 70px;vertical-align: middle; width: 100%;background: #2977A8;">
    <img id="mainLogo" src="<%=request.getContextPath()%>/images/logo_1.png"/> <!-- style="height: 70px; width: 400px" -->
</div>
<table width="100%" border="0">
    <tr>
        <td class="panelHeader" >
            <table border="0" cellpadding="0" cellspacing="0" style="vertical-align: middle; margin-left: 230px">
                <tr style="width: 100%">
                    <td><span style="font-size: 14pt">TRAFFICO IN TEMPO REALE</span></td>
                </tr>
            </table>
        </td>
    </tr>
</table>