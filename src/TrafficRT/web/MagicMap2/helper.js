// determinate if browser is IE7
var isIE7 = false;

var feature_arr = new Array();

if(navigator.appVersion != null)
{
	if(navigator.appVersion.indexOf('MSIE 7.0') != -1)
		isIE7 = true;
}

var removeElementByValue = function(array, value)
{
	var arrayRes = new Array();
	for(var i=0;i<array.length;i++)
	{
		var item = array[i];
		if(item == value)
		{
			
		}
		else
			{
				arrayRes.push(item);
			}
		}
	return arrayRes;
};


//Array.prototype.removeElementByValueG = function(value)
var removeElementByValueG = function(array, value)
{
	var arrayRes = new Array();
	for(var i=0;i<array.length;i++)
	{
		var item = array[i];
		if((item.id == value.id) && (item.o == value.o))
		{
			
		}
		else
			{
				arrayRes.push(item);
			}
		}
	return arrayRes;
};

var exists = function(array, value)
{
	var result = false;
	for(var i=0;i<array.length;i++)
	{
		var item = array[i];
		if(item == value)
		{
			result = true;
		}
	}
	return result;
};

/**
 * Estensione dell'oggetto Array
 * getElementByValue(value)
 * input: il valore key
 * output: l'oggetto associato alla key inserita
 * 				dell' array. Se la key non esiste ritorna null.
 */
var getElementByValue = function(array, key)
{
	var objResult = null;
	for(var i=0;i<array.length;i++)
	{
		var item = array[i];
		if((item.id == key))
		{
			objResult = item.o;
			break;
		}
	}
	return objResult;
};

var vis_proprieta = function(oggetto)
{
	var msg = '';
	
	for(var i in oggetto)
	{
		msg += i + ' --> ' + oggetto[i] + '\n';
	}
	
	alert(msg);
};


function RGBtoHex(R,G,B) 
{
	return toHex(R)+toHex(G)+toHex(B);
}

function toHex(N)
{
 if (N==null) return "00";
 N=parseInt(N); 
 if (N==0 || isNaN(N)) return "00";
 N=Math.max(0,N); 
 N=Math.min(N,255);
 N=Math.round(N);
 return "0123456789ABCDEF".charAt((N-N%16)/16)
      + "0123456789ABCDEF".charAt(N%16);
}

var HexToR = function(h)
{
	return parseInt((cutHex(h)).substring(0,2),16);
};

var HexToG = function(h)
{
	return parseInt((cutHex(h)).substring(2,4),16);
};

var HexToB = function(h)
{
	return parseInt((cutHex(h)).substring(4,6),16);
};

var cutHex = function(h)
{
	return (h.charAt(0)=="#") ? h.substring(1,7):h
};

var OverlayObj = function(id, overlay, drag)
{
	this.id = id;
	this.o = overlay;
	this.drag = drag;
};

var createCircle = function(center, radius, unit)
{
	var d;
	if (unit == 1) // meters
	{
		d = (radius)/6372795;
	}
	else // miles
	{
		d = radius/3963.189; // radians
	}
		
	var coords = new Array();
	
	with(Math)
	{
		var lat1 = (PI/180)* center.y; // radians
		var lng1 = (PI/180)* center.x; // radians
		
		var coords = new Array();
		
		for (var a = 0 ; a < 361 ; a++ )
		{
			var tc = (PI/180)*a;
			var y = asin(sin(lat1)*cos(d)+cos(lat1)*sin(d)*cos(tc));
			var dlng = atan2(sin(tc)*sin(d)*cos(lat1),cos(d)-sin(lat1)*sin(y));
			var x = ((lng1-dlng+PI) % (2*PI)) - PI; // MOD function
			var point = {x: parseFloat(x*(180/PI), 10), y: parseFloat(y*(180/PI), 10)};
			
			coords.push(point);
		}
	}
	
	return coords;
};

var convertGoogle2LL = function(x, y)
{
	var lonlat = new OpenLayers.LonLat(x, y);
	lonlat.transform(new OpenLayers.Projection("EPSG:900913"), new OpenLayers.Projection("EPSG:4326"));
	
	var coord = {x: lonlat.lon, y: lonlat.lat};
	return coord;
};

function convertLL2Google(lat, lon)
{
	var lonlat = new OpenLayers.LonLat(lon, lat);
	lonlat.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
	
	var coord = {x: lonlat.lon, y: lonlat.lat};
	return coord;
}

var olCaptureClick = function(e, callback)
{
	var lonlat = olmap.getLonLatFromViewPortPx(e.xy);
	var lat = lonlat.lat;
	var lon = lonlat.lon;
	
	var coord = convertGoogle2LL(lon, lat);
	
	if(callback)
		callback(coord.x, coord.y);
};

// PER IL SALVATAGGIO DELLE AREE
function olSaveEditPolygon(cbfunction)
{
	feature_arr = new Array();
	
	for(var i=0; i<editlayer.features.length; i++)
	{
		var feature = editlayer.features[i];

		var id_feature = feature.id;
		var geom_feature = feature.geometry;

		var points_arr = new Array();
		var points = geom_feature.getVertices();

		for(var t=0; t<points.length; t++)
		{
			var ll = convertGoogle2LL(points[t].x, points[t].y);
			points_arr.push({
				x: ll.x
				, y: ll.y
			});
		}

		feature_arr.push({
			feature: feature
			, id: id_feature
			, points: points_arr
			});
	}
	
	if(cbfunction != null)
	{
		cbfunction(feature_arr);
	}
}

function olRemoveEditPolygon(array)
{
	if(editlayer != null)
	{
		if(array != null)
		{
			for(var i=0; i<array.length; i++)
			{
				editlayer.removeFeatures(array[i].feature);
			}
		}
	}
}

