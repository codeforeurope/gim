<%@page contentType="text/html" pageEncoding="UTF-8"%>


<script type="text/javascript" src="<%=request.getContextPath()%>/MagicMap2/magicmap.js"></script>
<script type="text/javascript">
    var mapType = 1;
    var theMap = null;

    var options = {
        google: {
            showGoogleStreets: false
        }
        , osm: {
            showOSM: true
        }
    }

document.write(loadLibrary(options));
        
var loadMap = function()
{
    theMap = new MagicMap('map');
    theMap.centerAt(41.97071,12.41998);
    theMap.setZoom(60);
};

//Creo layer delle uscite
var addLayerExits = function (valueLayerMap,nameLayerMap)
{
    var nameExit = 'Uscite GRA';

    if (nameLayerMap.indexOf(nameExit) == -1)
    {
        var layer = theMap.addLayer({
            wmsLABEL : nameExit
            , wmsURL : 'http://172.17.8.12:8080/geoserver/optima/wms'
            , wmsLAYER: 'gim:exits_gra'
        });

        if (layer != null) {
            valueLayerMap.push(layer);
            nameLayerMap.push(nameExit);
        }
    }
}

var addLayerEvent = function (valueLayerMap,nameLayerMap) {

    var nameEvent = 'Event';
    if (nameLayerMap.indexOf(nameEvent) == -1)
    {
        var layer = theMap.addLayer({
            wmsLABEL : nameEvent
            , wmsURL : 'http://172.17.8.12:8080/geoserver/optima/wms'
            , wmsLAYER: 'optima_roma:evnt_sinottico'
        });

        if (layer != null) {
            valueLayerMap.push(layer);
            nameLayerMap.push(nameEvent);
        }
    }
}
//Creo layer con tutte le strade della strt
var addLayerStr = function (valueLayerMap,nameLayerMap)
{
    var name = 'Provincia Roma';
    var layer = theMap.addLayer({
        wmsLABEL : name
        , wmsURL : 'http://172.17.8.12:8080/geoserver/optima/wms'
        , wmsLAYER: 'optima_roma:sinottico_utente_allStrt'
    });

    if (layer != null) {
        valueLayerMap.push(layer);
        nameLayerMap.push(name);
    }
}

//Creo layer strt parametrizzato per la strada scelta
var addLayerStreet = function (valueLayerMap,nameLayerMap,nameStreet,idStreet,bBOx) {
    var layer = theMap.addLayer({
        wmsLABEL : ''+nameStreet
        , wmsURL : 'http://172.17.8.12:8080/geoserver/optima/wms?viewparams=idStreet:'+idStreet+''
        , wmsLAYER: 'optima_roma:dettaglio_sinottico_utente'
    });

    if (bBOx != null)
        theMap.setMapArea(bBOx);

    if (layer != null) {
        valueLayerMap.push(layer);
        nameLayerMap.push(nameStreet);
    }
}

var removeAllLayers = function (valueLayerMap,theMap)
{
    for (var i=0; i < valueLayerMap.length ;i++) {
        theMap.removeLayer(valueLayerMap[i]);
    }
}

var deleteLayer = function (valueLayerMap,nameLayerMap,nameLayer,theMap)
{
    for (var i=0; i < valueLayerMap.length ;i++) {
        if (nameLayerMap[i] == nameLayer){
            theMap.removeLayer(valueLayerMap[i]);
        }
    }
}

</script>
<style type="text/css">
    .olControlEditingToolbar
    {
        opacity: 0.75;
        padding: 3px 0px 3px 3px;
    }
    .measureDiv
    {
        /*    top: 390px; */
        /*    left: 7px; */
    }
    .olControlDragFeatureItemInactive
    {
        display: none;
    }

    .olControlEditingToolbar > div
    {
        opacity: 1;
        margin: 0;
        margin-right: 3px;
    }

    .zoom_history
    {
        position: absolute;
        bottom: 90px;
        left: 15px;
        z-index: 1002;
    }
</style>
