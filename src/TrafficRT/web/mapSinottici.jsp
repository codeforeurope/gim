<%@page contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript" src="MagicMap2/magicmap.js"></script>
<script type="text/javascript">
    var mapType = 1;
    var theMap = null;

    var options = {
        osm: {
            showOSM: true
        }
    }

    document.write(loadLibrary(options));
        
    var loadMap = function() {
        theMap = new MagicMap('map');
        theMap.centerAt(41.97071, 12.41998); <%-- Centra su Roma --%>
        theMap.setZoom(60);                  <%-- Questo livello di zoom fa vedere tutta la provincia di Roma --%>
    };
</script>
