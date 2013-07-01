/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.36
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


using System;
using System.Runtime.InteropServices;

public class mapscript {

  internal class mapscriptObject : IDisposable {
	public virtual void Dispose() {
      
    }
  }
  internal static mapscriptObject themapscriptObject = new mapscriptObject();
  protected static object ThisOwn_true() { return null; }
  protected static object ThisOwn_false() { return themapscriptObject; }
  
  [DllImport("mapscript", EntryPoint="SetEnvironmentVariable")]
  public static extern int SetEnvironmentVariable(string envstring);

  public static int msSaveImage(mapObj map, imageObj img, string filename) {
    int ret = mapscriptPINVOKE.msSaveImage(mapObj.getCPtr(map), imageObj.getCPtr(img), filename);
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static void msFreeImage(imageObj img) {
    mapscriptPINVOKE.msFreeImage(imageObj.getCPtr(img));
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static int msSetup() {
    int ret = mapscriptPINVOKE.msSetup();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static void msCleanup() {
    mapscriptPINVOKE.msCleanup();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static mapObj msLoadMapFromString(string buffer, string new_mappath) {
    IntPtr cPtr = mapscriptPINVOKE.msLoadMapFromString(buffer, new_mappath);
    mapObj ret = (cPtr == IntPtr.Zero) ? null : new mapObj(cPtr, false, ThisOwn_false());
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static errorObj msGetErrorObj() {
    IntPtr cPtr = mapscriptPINVOKE.msGetErrorObj();
    errorObj ret = (cPtr == IntPtr.Zero) ? null : new errorObj(cPtr, false, ThisOwn_false());
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static void msResetErrorList() {
    mapscriptPINVOKE.msResetErrorList();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static string msGetVersion() {
    string ret = mapscriptPINVOKE.msGetVersion();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static int msGetVersionInt() {
    int ret = mapscriptPINVOKE.msGetVersionInt();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static string msGetErrorString(string delimiter) {
    string ret = mapscriptPINVOKE.msGetErrorString(delimiter);
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static void msConnPoolCloseUnreferenced() {
    mapscriptPINVOKE.msConnPoolCloseUnreferenced();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static void msIO_resetHandlers() {
    mapscriptPINVOKE.msIO_resetHandlers();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static void msIO_installStdoutToBuffer() {
    mapscriptPINVOKE.msIO_installStdoutToBuffer();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static void msIO_installStdinFromBuffer() {
    mapscriptPINVOKE.msIO_installStdinFromBuffer();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static string msIO_stripStdoutBufferContentType() {
    string ret = mapscriptPINVOKE.msIO_stripStdoutBufferContentType();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static void msIO_stripStdoutBufferContentHeaders() {
    mapscriptPINVOKE.msIO_stripStdoutBufferContentHeaders();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
  }

  public static string msIO_getStdoutBufferString() {
    string ret = mapscriptPINVOKE.msIO_getStdoutBufferString();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return ret;
  }

  public static byte[] msIO_getStdoutBufferBytes() {
    mapscriptPINVOKE.msIO_getStdoutBufferBytes();
    if (mapscriptPINVOKE.SWIGPendingException.Pending) throw mapscriptPINVOKE.SWIGPendingException.Retrieve();
    return mapscriptPINVOKE.GetBytes();
}

  public static readonly int MAX_ERROR_LEN = mapscriptPINVOKE.MAX_ERROR_LEN_get();
  public static readonly string MS_VERSION = mapscriptPINVOKE.MS_VERSION_get();
  public static readonly int MS_VERSION_MAJOR = mapscriptPINVOKE.MS_VERSION_MAJOR_get();
  public static readonly int MS_VERSION_MINOR = mapscriptPINVOKE.MS_VERSION_MINOR_get();
  public static readonly int MS_VERSION_REV = mapscriptPINVOKE.MS_VERSION_REV_get();
  public static readonly int MS_VERSION_NUM = mapscriptPINVOKE.MS_VERSION_NUM_get();
  public static readonly string __FUNCTION__ = mapscriptPINVOKE.__FUNCTION___get();
  public static readonly int MS_TRUE = mapscriptPINVOKE.MS_TRUE_get();
  public static readonly int MS_FALSE = mapscriptPINVOKE.MS_FALSE_get();
  public static readonly int MS_UNKNOWN = mapscriptPINVOKE.MS_UNKNOWN_get();
  public static readonly int MS_ON = mapscriptPINVOKE.MS_ON_get();
  public static readonly int MS_OFF = mapscriptPINVOKE.MS_OFF_get();
  public static readonly int MS_DEFAULT = mapscriptPINVOKE.MS_DEFAULT_get();
  public static readonly int MS_EMBED = mapscriptPINVOKE.MS_EMBED_get();
  public static readonly int MS_DELETE = mapscriptPINVOKE.MS_DELETE_get();
  public static readonly int MS_YES = mapscriptPINVOKE.MS_YES_get();
  public static readonly int MS_NO = mapscriptPINVOKE.MS_NO_get();
  public static readonly int MS_GD_ALPHA = mapscriptPINVOKE.MS_GD_ALPHA_get();
  public static readonly int MS_LAYER_ALLOCSIZE = mapscriptPINVOKE.MS_LAYER_ALLOCSIZE_get();
  public static readonly int MS_CLASS_ALLOCSIZE = mapscriptPINVOKE.MS_CLASS_ALLOCSIZE_get();
  public static readonly int MS_STYLE_ALLOCSIZE = mapscriptPINVOKE.MS_STYLE_ALLOCSIZE_get();
  public static readonly int MS_MAX_LABEL_PRIORITY = mapscriptPINVOKE.MS_MAX_LABEL_PRIORITY_get();
  public static readonly int MS_DEFAULT_LABEL_PRIORITY = mapscriptPINVOKE.MS_DEFAULT_LABEL_PRIORITY_get();
  public static readonly int MS_RENDER_WITH_SWF = mapscriptPINVOKE.MS_RENDER_WITH_SWF_get();
  public static readonly int MS_RENDER_WITH_RAWDATA = mapscriptPINVOKE.MS_RENDER_WITH_RAWDATA_get();
  public static readonly int MS_RENDER_WITH_IMAGEMAP = mapscriptPINVOKE.MS_RENDER_WITH_IMAGEMAP_get();
  public static readonly int MS_RENDER_WITH_TEMPLATE = mapscriptPINVOKE.MS_RENDER_WITH_TEMPLATE_get();
  public static readonly int MS_RENDER_WITH_OGR = mapscriptPINVOKE.MS_RENDER_WITH_OGR_get();
  public static readonly int MS_RENDER_WITH_PLUGIN = mapscriptPINVOKE.MS_RENDER_WITH_PLUGIN_get();
  public static readonly int MS_RENDER_WITH_CAIRO_RASTER = mapscriptPINVOKE.MS_RENDER_WITH_CAIRO_RASTER_get();
  public static readonly int MS_RENDER_WITH_CAIRO_PDF = mapscriptPINVOKE.MS_RENDER_WITH_CAIRO_PDF_get();
  public static readonly int MS_RENDER_WITH_CAIRO_SVG = mapscriptPINVOKE.MS_RENDER_WITH_CAIRO_SVG_get();
  public static readonly int MS_RENDER_WITH_OGL = mapscriptPINVOKE.MS_RENDER_WITH_OGL_get();
  public static readonly int MS_RENDER_WITH_AGG = mapscriptPINVOKE.MS_RENDER_WITH_AGG_get();
  public static readonly int MS_RENDER_WITH_GD = mapscriptPINVOKE.MS_RENDER_WITH_GD_get();
  public static readonly int MS_RENDER_WITH_KML = mapscriptPINVOKE.MS_RENDER_WITH_KML_get();
  public static readonly int MS_POSITIONS_LENGTH = mapscriptPINVOKE.MS_POSITIONS_LENGTH_get();
  public static readonly int MS_SINGLE = mapscriptPINVOKE.MS_SINGLE_get();
  public static readonly int MS_MULTIPLE = mapscriptPINVOKE.MS_MULTIPLE_get();
  public static readonly int MS_CJC_DEFAULT_JOIN_MAXSIZE = mapscriptPINVOKE.MS_CJC_DEFAULT_JOIN_MAXSIZE_get();
  public static readonly int MS_STYLE_BINDING_LENGTH = mapscriptPINVOKE.MS_STYLE_BINDING_LENGTH_get();
  public static readonly int MS_LABEL_BINDING_LENGTH = mapscriptPINVOKE.MS_LABEL_BINDING_LENGTH_get();
  public static readonly int MS_NOOVERRIDE = mapscriptPINVOKE.MS_NOOVERRIDE_get();
  public static readonly int SHX_BUFFER_PAGE = mapscriptPINVOKE.SHX_BUFFER_PAGE_get();
  public static readonly int MS_SHAPEFILE_POINT = mapscriptPINVOKE.MS_SHAPEFILE_POINT_get();
  public static readonly int MS_SHAPEFILE_ARC = mapscriptPINVOKE.MS_SHAPEFILE_ARC_get();
  public static readonly int MS_SHAPEFILE_POLYGON = mapscriptPINVOKE.MS_SHAPEFILE_POLYGON_get();
  public static readonly int MS_SHAPEFILE_MULTIPOINT = mapscriptPINVOKE.MS_SHAPEFILE_MULTIPOINT_get();
  public static readonly int MS_SHP_POINTZ = mapscriptPINVOKE.MS_SHP_POINTZ_get();
  public static readonly int MS_SHP_ARCZ = mapscriptPINVOKE.MS_SHP_ARCZ_get();
  public static readonly int MS_SHP_POLYGONZ = mapscriptPINVOKE.MS_SHP_POLYGONZ_get();
  public static readonly int MS_SHP_MULTIPOINTZ = mapscriptPINVOKE.MS_SHP_MULTIPOINTZ_get();
  public static readonly int MS_SHP_POINTM = mapscriptPINVOKE.MS_SHP_POINTM_get();
  public static readonly int MS_SHP_ARCM = mapscriptPINVOKE.MS_SHP_ARCM_get();
  public static readonly int MS_SHP_POLYGONM = mapscriptPINVOKE.MS_SHP_POLYGONM_get();
  public static readonly int MS_SHP_MULTIPOINTM = mapscriptPINVOKE.MS_SHP_MULTIPOINTM_get();
  public static readonly int MS_SYMBOL_ALLOCSIZE = mapscriptPINVOKE.MS_SYMBOL_ALLOCSIZE_get();
  public static readonly int MS_MAXVECTORPOINTS = mapscriptPINVOKE.MS_MAXVECTORPOINTS_get();
  public static readonly int MS_MAXPATTERNLENGTH = mapscriptPINVOKE.MS_MAXPATTERNLENGTH_get();
  public static readonly int MS_IMAGECACHESIZE = mapscriptPINVOKE.MS_IMAGECACHESIZE_get();
  public static readonly int MS_NOERR = mapscriptPINVOKE.MS_NOERR_get();
  public static readonly int MS_IOERR = mapscriptPINVOKE.MS_IOERR_get();
  public static readonly int MS_MEMERR = mapscriptPINVOKE.MS_MEMERR_get();
  public static readonly int MS_TYPEERR = mapscriptPINVOKE.MS_TYPEERR_get();
  public static readonly int MS_SYMERR = mapscriptPINVOKE.MS_SYMERR_get();
  public static readonly int MS_REGEXERR = mapscriptPINVOKE.MS_REGEXERR_get();
  public static readonly int MS_TTFERR = mapscriptPINVOKE.MS_TTFERR_get();
  public static readonly int MS_DBFERR = mapscriptPINVOKE.MS_DBFERR_get();
  public static readonly int MS_GDERR = mapscriptPINVOKE.MS_GDERR_get();
  public static readonly int MS_IDENTERR = mapscriptPINVOKE.MS_IDENTERR_get();
  public static readonly int MS_EOFERR = mapscriptPINVOKE.MS_EOFERR_get();
  public static readonly int MS_PROJERR = mapscriptPINVOKE.MS_PROJERR_get();
  public static readonly int MS_MISCERR = mapscriptPINVOKE.MS_MISCERR_get();
  public static readonly int MS_CGIERR = mapscriptPINVOKE.MS_CGIERR_get();
  public static readonly int MS_WEBERR = mapscriptPINVOKE.MS_WEBERR_get();
  public static readonly int MS_IMGERR = mapscriptPINVOKE.MS_IMGERR_get();
  public static readonly int MS_HASHERR = mapscriptPINVOKE.MS_HASHERR_get();
  public static readonly int MS_JOINERR = mapscriptPINVOKE.MS_JOINERR_get();
  public static readonly int MS_NOTFOUND = mapscriptPINVOKE.MS_NOTFOUND_get();
  public static readonly int MS_SHPERR = mapscriptPINVOKE.MS_SHPERR_get();
  public static readonly int MS_PARSEERR = mapscriptPINVOKE.MS_PARSEERR_get();
  public static readonly int MS_SDEERR = mapscriptPINVOKE.MS_SDEERR_get();
  public static readonly int MS_OGRERR = mapscriptPINVOKE.MS_OGRERR_get();
  public static readonly int MS_QUERYERR = mapscriptPINVOKE.MS_QUERYERR_get();
  public static readonly int MS_WMSERR = mapscriptPINVOKE.MS_WMSERR_get();
  public static readonly int MS_WMSCONNERR = mapscriptPINVOKE.MS_WMSCONNERR_get();
  public static readonly int MS_ORACLESPATIALERR = mapscriptPINVOKE.MS_ORACLESPATIALERR_get();
  public static readonly int MS_WFSERR = mapscriptPINVOKE.MS_WFSERR_get();
  public static readonly int MS_WFSCONNERR = mapscriptPINVOKE.MS_WFSCONNERR_get();
  public static readonly int MS_MAPCONTEXTERR = mapscriptPINVOKE.MS_MAPCONTEXTERR_get();
  public static readonly int MS_HTTPERR = mapscriptPINVOKE.MS_HTTPERR_get();
  public static readonly int MS_CHILDERR = mapscriptPINVOKE.MS_CHILDERR_get();
  public static readonly int MS_WCSERR = mapscriptPINVOKE.MS_WCSERR_get();
  public static readonly int MS_GEOSERR = mapscriptPINVOKE.MS_GEOSERR_get();
  public static readonly int MS_RECTERR = mapscriptPINVOKE.MS_RECTERR_get();
  public static readonly int MS_TIMEERR = mapscriptPINVOKE.MS_TIMEERR_get();
  public static readonly int MS_GMLERR = mapscriptPINVOKE.MS_GMLERR_get();
  public static readonly int MS_SOSERR = mapscriptPINVOKE.MS_SOSERR_get();
  public static readonly int MS_NULLPARENTERR = mapscriptPINVOKE.MS_NULLPARENTERR_get();
  public static readonly int MS_AGGERR = mapscriptPINVOKE.MS_AGGERR_get();
  public static readonly int MS_OWSERR = mapscriptPINVOKE.MS_OWSERR_get();
  public static readonly int MS_OGLERR = mapscriptPINVOKE.MS_OGLERR_get();
  public static readonly int MS_RENDERERERR = mapscriptPINVOKE.MS_RENDERERERR_get();
  public static readonly int MS_NUMERRORCODES = mapscriptPINVOKE.MS_NUMERRORCODES_get();
  public static readonly int MESSAGELENGTH = mapscriptPINVOKE.MESSAGELENGTH_get();
  public static readonly int ROUTINELENGTH = mapscriptPINVOKE.ROUTINELENGTH_get();
  public static readonly string MS_ERROR_LANGUAGE = mapscriptPINVOKE.MS_ERROR_LANGUAGE_get();
  public static readonly int MS_HASHSIZE = mapscriptPINVOKE.MS_HASHSIZE_get();
  public static readonly int MS_DEFAULT_CGI_PARAMS = mapscriptPINVOKE.MS_DEFAULT_CGI_PARAMS_get();
}
