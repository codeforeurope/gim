// Default width of the right panel
var DEFAULT_RIGHT_WIDTH = 450;

// Default height of the bottom panel
var DEFAULT_BOTTOM_HEIGHT = 70;

var rightWidth=DEFAULT_RIGHT_WIDTH;
var bottomHeight=DEFAULT_BOTTOM_HEIGHT;

var getPageDim = function()
{
 var dim = {};

 var myWidth = 0;
 var myHeight = 0;
 if( typeof( window.innerWidth ) == 'number' )
 {
  //Non-IE
  myWidth = window.innerWidth;
  myHeight = window.innerHeight;
 }
 else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) )
 {
  //IE 6+ in 'standards compliant mode'
  myWidth = document.documentElement.clientWidth;
  myHeight = document.documentElement.clientHeight;
 }
 else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) )
 {
  //IE 4 compatible
  myWidth = document.body.clientWidth;
  myHeight = document.body.clientHeight;
 }

 dim.height = myHeight;
 dim.width = myWidth;

 return dim;
}

function initPage()
{
    dims = getPageDim();
    mainWidth = dims.width;
    mainHeight = dims.height;

    document.getElementById('map').style.height=(mainHeight-bottomHeight)+"px";
    document.getElementById('map').style.width=(mainWidth-rightWidth-15)+"px";

    document.getElementById('main:mtListPanel').style.height=(mainHeight - HEADER_HEIGHT - bottomHeight)+"px";
    document.getElementById('mtListScroller').style.height=((mainHeight - HEADER_HEIGHT - bottomHeight - 60 - 20))+"px";
    document.getElementById('historyPanelScroller').style.height=((mainHeight - HEADER_HEIGHT - bottomHeight - 230))+"px";
//
//    if (document.getElementById('historyScroller'))
//        document.getElementById('historyScroller').style.height=(mainHeight-200)+"px";
//
//    if (document.getElementById('poiScroller'))
//        document.getElementById('poiScroller').style.height=(mainHeight-100)+"px";

    document.getElementById('footerLeft').style.width=(mainWidth-rightWidth-20)+"px";
}

function collapseLiveInfoArea()
{
    bottomHeight = 23;

    mainHeight = getPageDim().height;
    mainHeight += "px";

    document.getElementById("map").style.display=null
    document.getElementById("map").style.height=mainHeight;
    document.getElementById('footerLeft').style.top=null;
    document.getElementById('footerLeft').style.bottom="0";
    document.getElementById("footerLeft").style.height=bottomHeight+"px";

    document.getElementById("main:liaPanel").display="none";
    document.getElementById("main:liaPanel").style.height=bottomHeight+"px";
    document.getElementById("main:liaPanel_body").style.display="none";

    monitorVehicleDetail = document.getElementById("monitorVehicleDetail");
    if (monitorVehicleDetail)
        monitorVehicleDetail.style.display="none";

    initPage();
}


function expandLiveInfoArea()
{
    bottomHeight = DEFAULT_BOTTOM_HEIGHT;

    mainHeight = getPageDim().height;
    mainHeight += "px";

    document.getElementById("map").style.display=null;
    document.getElementById("map").style.height=mainHeight;
    document.getElementById('footerLeft').style.top=null;
    document.getElementById('footerLeft').style.bottom="0";
    document.getElementById("footerLeft").style.height=bottomHeight+"px";
    document.getElementById("main:liaPanel").style.height=null;
    document.getElementById("main:liaPanel").style.height=bottomHeight+"px";
//    document.getElementById("main:liaTabs").style.height="50px";
    document.getElementById("main:tableToolBar").style.display="none";
    document.getElementById("main:liaPanel_body").style.display=null;

    rule = null;
    for (i = 0; i < document.styleSheets.length; i++)
	{
		if (document.styleSheets[i].title == "Toggable1")
		{
		    if (document.styleSheets[i].cssRules)
			    rule = document.styleSheets[i].cssRules[1];
			else
			    rule = document.styleSheets[i].rules[1] ;
		}
	}
	rule.style.height="160px";

	initPage();
}

function maximizeLiveInfoArea()
{
    bottomHeight = getPageDim().height - 74 - 25;

    document.getElementById("map").style.display="none";
    document.getElementById('footerLeft').style.top=74+28+"px";
    document.getElementById("footerLeft").style.height=bottomHeight+"px";
    document.getElementById("main:liaPanel").style.height=bottomHeight+"px";
    document.getElementById("main:liaPanel_body").style.display=null;
//    document.getElementById("main:liaTabs").style.height=(bottomHeight-120)+"px";
    document.getElementById("main:tableToolBar").style.display=null;

    rule = null;
    for (i = 0; i < document.styleSheets.length; i++)
	{
		if (document.styleSheets[i].title == "Toggable1")
		{
		    if (document.styleSheets[i].cssRules)
			    rule = document.styleSheets[i].cssRules[1];
			else
			    rule = document.styleSheets[i].rules[1] ;
		}
	}
	rule.style.height=(bottomHeight - 74 - 25)+"px";

	initPage();
}

function collapseMTList()
{
    rightWidth = 0;

    document.getElementById("rightPanel").style.display="none";
    document.getElementById("footerRight").style.display="none";

    document.getElementById("map").style.width="100%";
    document.getElementById("footerLeft").style.width="100%";

    initPage();

    document.getElementById("expandSidebarButton").style.display="block";
}

function expandMTList()
{
    rightWidth = DEFAULT_RIGHT_WIDTH;

    document.getElementById("rightPanel").style.display="block";
    document.getElementById("footerRight").style.display="block";
    document.getElementById("expandSidebarButton").style.display="none";

    initPage();
}


