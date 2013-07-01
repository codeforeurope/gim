<%-- 
    Document   : sinottici.jsp
    Created on : 24-ott-2011, 9.54.11
    Author     : Manuel
--%>


<%@page import="sistematica.sinottici.Sinottico"%>
<%@page import="sistematica.sinottici.Street"%>
<%@page import="sistematica.sinottici.Camera"%>
<%@page import="sistematica.sinottici.Sinottico"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <%
                    Sinottico sinottico = new Sinottico();

                    List<Street> listStr = null;
                    List<Camera> listCam = null;
                    //Carico le strade
                    listStr = sinottico.getAllStreet();

                    Camera camDef = sinottico.getListCameras().get(0);
        %>


        <title>Traffico</title>
        <link href="../css/default.css" rel="stylesheet" type="text/css" />
        <link href="../css/map.css" rel="stylesheet" type="text/css" />
        <link href="../css/jq/jquery-ui-1.8.9.custom.css" rel="stylesheet" type="text/css" />

        <script type="text/javascript" src="<%=request.getContextPath()%>/servlet/dwr/engine.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/servlet/dwr/util.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/servlet/dwr/interface/DwrCamera.js"></script>

        <!-- JQuery -->
        <script type="text/javascript" src="../js/jq/jquery-1.6.min.js"></script>
        <script type="text/javascript" src="../js/jq/jquery-ui-1.8.9.custom.min.js"></script>
        <script type="text/javascript" src="../js/jq/jquery.blockUI.js"></script>
        <script type="text/javascript" src="../js/jq/jquery.layout-1.2.0.js"></script>
        <!-- JavaScript Utils -->
        <script type="text/javascript" src="../js/pageManagerLayout.js"></script>
        <script type="text/javascript" src="../js/date.js"></script>
        <script type="text/javascript" src="../js/loading.js"></script>
        
        <style>
		image[id *= "OpenLayers.Geometry.Point_"]:hover { cursor:pointer; }; 
	</style>

        <script  type="text/javascript">
            var secondRefresh = 300;           //Refresh della pagina ogni 5 minuti
            var millSecCamera = 10000;         //Refresh della popUp delle telecamere espresso in millesecondi

            var street = null;
            var timestamp = null;
            var sdfDate = 'd MMM yyyy';
            var sdfTime = 'H:mm';

            var nameLayerMap = new Array();
            var valueLayerMap = new Array();
            var arrayCamere =new Array();

            var idStr = -1;
            
            function documentReady()
            {
                timedRefresh(secondRefresh*1000);
                jQuery.noConflict();

                outerLayout = jQuery('body').layout({
                    center__paneSelector:	".outer-center",
                    applyDefaultStyles: false,
                    east__size: 350,
                    east__resizable: false,
                    north__resizable: false,
                    north__closable: false,
                    north__spacing_open: 0,
                    north__spacing_closed: 0,
                    onopen_end: function (paneName)
                    {
                        if (paneName == 'east')
                        {
                            rightWidth = DEFAULT_RIGHT_WIDTH;
                            resizePage();
                        }
                    },
                    onclose_end: function (paneName)
                    {
                        if (paneName == 'east')
                        {
                            rightWidth = 0;
                            resizePage();
                        }
                    }
                });

                innerLayout = jQuery('div.outer-center').layout({
                    slidable:               false
                    //,	north__paneSelector:	".east-north"
                    ,	center__paneSelector:	".inner-center"
                    ,	south__paneSelector:	".inner-south"
                    ,	south__size:			bottomHeight
                    ,	south__resizable:       false
                    //                    ,   south__initClosed:      true
                    ,   onopen_end: function (name) {
                        if (name == 'south') {
                            dims = getPageDim();
                            mainHeight = dims.height;
                            //document.getElementById('map').style.height=(mainHeight-bottomHeight-_HEADER_HEIGHT-3)+"px";
                            document.getElementById('centerPanel').style.width='100%';
                            document.getElementById('map').style.width='100%';
                        }
                    }
                    ,   onclose_end: function (name) {
                        if (name == 'south') {
                            dims = getPageDim();
                            mainHeight = dims.height;
                            //document.getElementById('map').style.height=(mainHeight-_HEADER_HEIGHT-3)+"px";
                            document.getElementById('centerPanel').style.width='100%';
                            document.getElementById('map').style.width='100%';
                        }
                    }
                });

                jQuery("#eastPanel").tabs({disabled: [1]});

                resizePage();

                document.body.style.visibility = "visible";

                loadMap();

                timestamp = new Date();
                document.getElementById('timestamp').innerHTML = formatDate(timestamp,sdfDate) + '<br>Dati aggiornati alle ' + formatDate(timestamp,sdfTime);


                changeStreet(document.getElementById('street'));
            };

            function resizePage()
            {
                dims = getPageDim();
                mainWidth = dims.width;
                mainHeight = dims.height;

                document.getElementById('centerPanel').style.width='100%';
                document.getElementById('map').style.width='100%';

                if(theMap != null)
                {
                    theMap.updateSize();
                }
                document.body.style.margin="auto";
            }

            function timedRefresh(timeoutPeriod) {
                //setTimeout("location.reload(true);",timeoutPeriod);
                setTimeout("reloadPage();",timeoutPeriod);
            }

            function reloadPage()
            {
                street = document.getElementById('street');
                timedRefresh(secondRefresh*1000);
                timestamp = new Date();
                document.getElementById('timestamp').innerHTML = formatDate(timestamp,sdfDate) + '<br>Dati aggiornati alle ' + formatDate(timestamp,sdfTime);
                changeStreet(street);
            }

            function changeStreet(street)
            {
                var value = street.value;
                var name = street.options[street.selectedIndex].text;

                var split = value.split("_");
                var id_street = split[0];
                idStr = id_street;

                var bBox = new Array();
                var nodePoint = {x:split[2],y:split[1]};
                bBox.push(nodePoint);
                nodePoint = {x:split[4],y:split[3]};
                bBox.push(nodePoint);

                visLoading();

                clearLayerMap();

                if (id_street != -1)
                    addLayerStreet(valueLayerMap,nameLayerMap,name,id_street,bBox);
                else
                    addLayerStr(valueLayerMap,nameLayerMap);

                if (id_street == 1)
                    addLayerExits(valueLayerMap,nameLayerMap);

                showHiddenEvent(document.getElementById("ev").checked);
                showHiddenCamera(document.getElementById("cam").checked);

                endLoading();


            }

            function clearLayerMap()
            {
                if (valueLayerMap != null && valueLayerMap.length > 0) {
                    removeAllLayers(valueLayerMap,theMap);
                    valueLayerMap.splice(0,valueLayerMap.length);
                    nameLayerMap.splice(0,nameLayerMap.length);
                }
            }

            function showHiddenEvent(value) {
                if (value) //Mostro layer eventi
                    addLayerEvent(valueLayerMap,nameLayerMap);
                else //Non mostro layer eventi
                {
                    deleteLayer(valueLayerMap,nameLayerMap,'Event',theMap);

                    for (var i=0; i < nameLayerMap.length; i++)
                        if (nameLayerMap[i] == 'Event') {
                            nameLayerMap.splice(i,1);
                            valueLayerMap.splice(i,1);
                        }
                }

            }

            function showHiddenExit(value) {
                if (value && idStr == 1) //Mostro layer uscite value=true
                    addLayerExits(valueLayerMap,nameLayerMap);
                else //Non mostro layer uscite value=false
                {
                    deleteLayer(valueLayerMap,nameLayerMap,'Uscite GRA',theMap);

                    for (var i=0; i < nameLayerMap.length; i++)
                        if (nameLayerMap[i] == 'Uscite GRA') {
                            nameLayerMap.splice(i,1);
                            valueLayerMap.splice(i,1);
                        }
                }
            }

            //////////////////////////////////////
            //TELECAMERE
            //////////////////////////////////////

            var linkCamera = "<%=camDef.getLink()%>";

            setTimeout("aggiornaCamera();",millSecCamera);
        
            function drawCamera()
            {
                DwrCamera.getListCamera(CBdrawCamera);
            }

            function CBdrawCamera(listCam)
            {
                if (listCam != null && listCam.length > 0)
                {
                    for (var i=0; i < listCam.length; i++)
                    {
                        var id_camera = 'idCamera='+i;
                        arrayCamere.push(id_camera);
                        eval(
                        ' var center = {y: listCam[i].latitude, x: listCam[i].longitude};' +
                            ' var radius = 200;' +
                            ' var link' +i+ ' = listCam[i].link;' +
                            ' var name' +i+ ' = listCam[i].name;' +
                            ' var desc' +i+ ' = listCam[i].description;' +
                            ' var options = {' +
                            '      outStroke: 2,' +
                            '      outOpacity: 0.7,' +
                            '      inOpacity: 1,' +
                            '      inColor: \'#ffff00\'' +
                            '};'+
                            'theMap.drawObject(id_camera, center, null, "http://villalobos.altervista.org/icons/camera.png", function(){switchCamera(link'+i+',name'+i+',desc'+i+'); }, false, options)');
                    }
                }

            }

            
            function switchCamera(link,name,desc)
            {
                //jQuery("#cameraDialog1").dialog({ modal: true, autoOpen: false, height: 500, width: 450,title: 'Camera_'+name,resizable: false,close: function(event, ui) {closedPopUpCamera()}});

                document.getElementById('Camera_IFrame').src = link;
                linkCamera = link;

                document.getElementById('nameCamera').innerHTML = name;
                document.getElementById('descCamera').innerHTML = desc;

                aggiornaCamera()
                
                //jQuery('#cameraDialog').dialog("open");

            }

            function aggiornaCamera()
            {

                var myIFrame = document.getElementById('Camera_IFrame');
                myIFrame.src = linkCamera+Math.floor(Math.random()*11);
                if (millSecCamera > 0)
                    setTimeout('aggiornaCamera();',millSecCamera);
            }

            function showHiddenCamera(value)
            {
                if (value) //Mostro layer eventi
                {
                    drawCamera();
                }else
                {
                    for (var c=0; c < arrayCamere.length; c++)
                    {
                        theMap.removeObject(arrayCamere[c]);
                    }
                }
            }
            //////////////////////////////////////
            //LIVELLO DI ZOOM
            //////////////////////////////////////

            function zoomEndResolution(resolution)
            {
                if(resolution < 153)
                {
                    showIcon();
                }else{
                    hiddenIcon();
                }
            }

            function hiddenIcon()
            {
                if (document.getElementById("ev").checked)
                {
                    document.getElementById("ev").checked=false;
                    showHiddenEvent(document.getElementById("ev").checked);
                }
                if (document.getElementById("cam").checked)
                {
                    document.getElementById("cam").checked=false;
                    showHiddenCamera(document.getElementById("cam").checked);
                }
                showHiddenExit(false);
            }

            function showIcon()
            {
                if (!document.getElementById("ev").checked)
                {
                    document.getElementById("ev").checked=true;
                    showHiddenEvent(document.getElementById("ev").checked);
                }
                if (!document.getElementById("cam").checked)
                {
                    document.getElementById("cam").checked=true;
                    showHiddenCamera(document.getElementById("cam").checked);
                }
                showHiddenExit(true);
            }

        </script>
        <jsp:include page="mapGim.jsp" />
    </head>
    <body onload="documentReady();" onresize="resizePage()" style="visibility:  hidden">
        <div class="ui-layout-north">
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
        </div>

        <div class="outer-center">
            <div id="centerPanel" class="inner-center">
                <div id="map" style="height: 100%; width: 100%; background-color: #ffffff;"></div>
            </div>

            <div id="liaPanel" class="inner-south" style="background-color: #ffffff;">
                <table>
                    <thead>
                    <th align="left" style="padding-left: 7px">Legenda Velocità</th>
                    <!--<th align="right" >Legenda Completa</th>-->
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <table id="legendSpeed" style="padding: 5px">
                                    <thead>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td style="background-color: #000000; width:10px">
                                            </td>
                                            <td>
                                                Fino a 10 km/h
                                            </td>
                                            <td>
                                                &nbsp;&nbsp;
                                            </td>

                                            <td style="background-color: #FF9900; width:10px">
                                            </td>
                                            <td>
                                                Da 30 a 50 Km/h
                                            </td>
                                            <td>
                                                &nbsp;&nbsp;
                                            </td>

                                            <td style="background-color: #00FFFF; width:10px">
                                            </td>
                                            <td>
                                                Da 70 a 90 Km/h
                                            </td>
                                            <td>
                                                &nbsp;&nbsp;
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="background-color: #FF0000; width:10px">
                                            </td>
                                            <td>
                                                Da 10 a 30 Km/h
                                            </td>
                                            <td>
                                                &nbsp;&nbsp;
                                            </td>

                                            <td style="background-color: #FFFF00; width:10px">
                                            </td>
                                            <td>
                                                Da 50 a 70 Km/h
                                            </td>
                                            <td>
                                                &nbsp;&nbsp;
                                            </td>

                                            <td style="background-color: #00FF00; width:10px">
                                            </td>
                                            <td>
                                                Oltre 90 Km/h
                                            </td>
                                            <td>
                                                &nbsp;&nbsp;
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>

                            <td>
                                <table>
                                    <thead></thead>
                                    <tbody>
                                        <tr>
                                            <td>
                                                <!-- value = 0 mostra gli eventi su mappa
                                                     value = 1 non mostra eventi su mappa!-->
                                                <input type=checkbox id ="ev" name="event" value=0 checked="checked" onclick="showHiddenEvent(this.checked)"/>
                                            </td>
                                            <td>
                                                Mostra Eventi
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type=checkbox id ="cam" name="camera" value=0 checked="checked" onclick="showHiddenCamera(this.checked)"/>
                                            </td>
                                            <td>
                                                Mostra Posizioni Telecamere
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div id="eastPanel" class="ui-layout-east"  style="padding: 0">
            <div class="ui-layout-content">
                <div id="online-tab" style="padding: 0; padding-top: 5px; overflow-y: hidden">
                    <table  style="padding: 10px">
                        <thead>

                        </thead>
                        <tbody>
                            <tr>
                                <td colspan="2"><div id="timestamp" style="font: bold 16px arial, sans-serif; color:#000000">dd-MM-yyyy HH24:MI</div></td>
                            </tr>
                            <tr>
                                <td>
                                    &nbsp;&nbsp;
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Strade monitorate
                                </td>
                                <td>
                                    <select id="street" style="width: 190px; cursor: pointer;" onChange="changeStreet(this)">
                                        <optgroup label="PROVINCIA DI ROMA">
                                            <option value=-1 selected=true>Tutte le strade</option>
                                        </optgroup>
                                        <optgroup label="GRA + AUTOSTRADE">
                                            <%
                                                        String valueA = "";
                                                        if (listStr != null)
                                                            for (Street str : listStr) {
                                                                valueA = str.getId() + "_" + str.getLatUp() + "_" + str.getLonUp() + "_" + str.getLatDown() + "_" + str.getLonDown();
                                                                if (str.getTypeStreet().equals("A")) {
                                            %>
                                            <option value=<%=valueA%>><%=str.getName()%></option>
                                            <%}
                                                                                                        }%>
                                        </optgroup>
                                        <optgroup label="CONSOLARI">
                                            <%
                                                        String valueC = "";
                                                        if (listStr != null)
                                                            for (Street str : listStr) {
                                                                valueC = str.getId() + "_" + str.getLatUp() + "_" + str.getLonUp() + "_" + str.getLatDown() + "_" + str.getLonDown();
                                                                if (str.getTypeStreet().equals("C")) {
                                            %>
                                            <option value=<%=valueC%>><%=str.getName()%></option>
                                            <%}
                                                                                                        }%>
                                        </optgroup>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                        </tbody>
                    </table>
                    <div id="cameraDialog" style="padding-left: 10px; z-index: 2000;bottom:0;position:absolute;">
                        <h2 id="nameCamera"><%=camDef.getName()%></h2>
                        <div id="conteinerCamera" style="display: block;" >
                            <img id="Camera_IFrame" src="<%=camDef.getLink()%>" style="width: 320px; height: 240px; ">
                        </div>
                        <p id="descCamera"><%=camDef.getDescription()%></p>
                    </div>
                </div>
            </div>
        </div>

        <div id="loadingBlock" style="display: none;">Caricamento in corso...</div>


    </body>
</html>
