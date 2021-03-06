/******************************************************************************
 * $Id: mapows.c 11582 2011-04-15 09:55:46Z tbonfort $
 *
 * Project:  MapServer
 * Purpose:  OGC Web Services (WMS, WFS) support functions
 * Author:   Daniel Morissette, DM Solutions Group (morissette@dmsolutions.ca)
 *
 ******************************************************************************
 * Copyright (c) 1996-2005 Regents of the University of Minnesota.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies of this Software or works derived from this Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************************************************/

#include "mapserver.h"
#include "maptime.h"
#include "maptemplate.h"

#include <ctype.h> /* isalnum() */
#include <stdarg.h> 
#include <assert.h>

MS_CVSID("$Id: mapows.c 11582 2011-04-15 09:55:46Z tbonfort $")

/*
** msOWSDispatch() is the entry point for any OWS request (WMS, WFS, ...)
** - If this is a valid request then it is processed and MS_SUCCESS is returned
**   on success, or MS_FAILURE on failure.
** - If force_ows_mode is true then an exception will be produced if the
**   request is not recognized as a valid request.
** - If force_ows_mode is false and this does not appear to be a valid OWS 
**   request then MS_DONE is returned and MapServer is expected to process 
**   this as a regular MapServer (traditional CGI) request.
*/
int msOWSDispatch(mapObj *map, cgiRequestObj *request, int ows_mode)
{
    int i, status = MS_DONE;
    const char *service=NULL;
    int force_ows_mode = 0;
    owsRequestObj ows_request;

    if (!request)
      return status;

    if (ows_mode == OWS || ows_mode == WFS)
      force_ows_mode = 1;
    
    ows_request.numlayers = 0;
    ows_request.enabled_layers = NULL;
    
    for( i=0; i<request->NumParams; i++ ) 
    {
        if(strcasecmp(request->ParamNames[i], "SERVICE") == 0)
            service = request->ParamValues[i];
    }

#ifdef USE_WMS_SVR
    /* Note: SERVICE param did not exist in WMS 1.0.0, it was added only in WMS 1.1.0,
     * so we need to let msWMSDispatch check for known REQUESTs even if SERVICE is not set.
     */
    if ((status = msWMSDispatch(map, request, &ows_request, MS_FALSE)) != MS_DONE )
    {
        msFree(ows_request.enabled_layers);
        return status;
    }
#else
    if( service != NULL && strcasecmp(service,"WMS") == 0 )
        msSetError( MS_WMSERR, 
                    "SERVICE=WMS requested, but WMS support not configured in MapServer.", 
                    "msOWSDispatch()" );
#endif

#ifdef USE_WFS_SVR
    /* Note: WFS supports POST requests, so the SERVICE param may only be in the post data
     * and not be present in the GET URL
     */
    if ((status = msWFSDispatch(map, request, &ows_request, (ows_mode == WFS))) != MS_DONE )
    {
        msFree(ows_request.enabled_layers);
        return status;
    }

#else
    if( service != NULL && strcasecmp(service,"WFS") == 0 )
        msSetError( MS_WFSERR, 
                    "SERVICE=WFS requested, but WFS support not configured in MapServer.", 
                    "msOWSDispatch()" );
#endif

#ifdef USE_WCS_SVR
    if ((status = msWCSDispatch(map, request, &ows_request)) != MS_DONE )
    {
        msFree(ows_request.enabled_layers);
        return status;
    }
#else
    if( service != NULL && strcasecmp(service,"WCS") == 0 )
        msSetError( MS_WCSERR, 
                    "SERVICE=WCS requested, but WCS support not configured in MapServer.", 
                    "msOWSDispatch()" );
#endif

#ifdef USE_SOS_SVR
    if ((status = msSOSDispatch(map, request, &ows_request)) != MS_DONE )
    {
        msFree(ows_request.enabled_layers);
        return status;
    }
#else
    if( service != NULL && strcasecmp(service,"SOS") == 0 )
        msSetError( MS_SOSERR, 
                    "SERVICE=SOS requested, but SOS support not configured in MapServer.", 
                    "msOWSDispatch()" );
#endif

    if (force_ows_mode) {
        if (service == NULL)
            /* Here we should return real OWS Common exceptions... once 
             * we have a proper exception handler in mapowscommon.c 
             */
            msSetError( MS_MISCERR,
                        "OWS Common exception: exceptionCode=MissingParameterValue, locator=SERVICE, ExceptionText=SERVICE parameter missing.", 
                        "msOWSDispatch()");
        else
            /* Here we should return real OWS Common exceptions... once 
             * we have a proper exception handler in mapowscommon.c 
             */
            msSetError( MS_MISCERR,
                        "OWS Common exception: exceptionCode=InvalidParameterValue, locator=SERVICE, ExceptionText=SERVICE parameter value invalid.", 
                        "msOWSDispatch()");
        
        /* Force mapserv to report the error by returning MS_FAILURE. 
         * The day we have a proper exception handler here then we should 
         * return MS_SUCCESS since the exception will have been processed 
         * the OWS way, which is a success as far as mapserv is concerned 
         */
        msFree(ows_request.enabled_layers);
        return MS_FAILURE; 
    }

    msFree(ows_request.enabled_layers);
    return MS_DONE;  /* Not a WMS/WFS request... let MapServer handle it 
                      * since we're not in force_ows_mode*/
}


/*
** msOWSRequestIsEnabled()
**
** Check if a layer is visible for a specific OWS request.
**
** 'namespaces' is a string with a letter for each namespace to lookup in
** the order they should be looked up. e.g. "MO" to lookup wms_ and ows_ If
** namespaces is NULL then this function just does a regular metadata
** lookup. If check_all_layers is set to MS_TRUE, the function will check
** all layers to see if the request is enable. (returns as soon as one is found) */
int msOWSRequestIsEnabled(mapObj *map, layerObj *layer, 
                          const char *namespaces, const char *request, int check_all_layers)
{
    int disabled = MS_FALSE; /* explicitly disabled flag */
    const char *enable_request;
    
    if (request == NULL)
        return MS_FALSE;

    /* First, we check in the layer metadata */
    if (layer && check_all_layers == MS_FALSE) 
    {
        enable_request = msOWSLookupMetadata(&layer->metadata, namespaces, "enable_request");
        if (msOWSParseRequestMetadata(enable_request, request, &disabled))
            return MS_TRUE;
        if (disabled) return MS_FALSE;
        
        enable_request = msOWSLookupMetadata(&layer->metadata, "O", "enable_request");
        if (msOWSParseRequestMetadata(enable_request, request, &disabled))
            return MS_TRUE;
        if (disabled) return MS_FALSE;
    }

    if (map && check_all_layers == MS_FALSE)
    {
        /* then we check in the map metadata */
        enable_request = msOWSLookupMetadata(&map->web.metadata, namespaces, "enable_request");
        if (msOWSParseRequestMetadata(enable_request, request, &disabled))
            return MS_TRUE;
        if (disabled) return MS_FALSE;
        
        enable_request = msOWSLookupMetadata(&map->web.metadata, "O", "enable_request");
        if (msOWSParseRequestMetadata(enable_request, request, &disabled))
            return MS_TRUE;
        if (disabled) return MS_FALSE;
    }

    if (map && (map->numlayers > 0) && check_all_layers == MS_TRUE)
    {
        int i, globally_enabled = MS_FALSE;
        enable_request = msOWSLookupMetadata(&map->web.metadata, namespaces, "enable_request");
        globally_enabled = msOWSParseRequestMetadata(enable_request, request, &disabled);
        
        if (!globally_enabled && !disabled)
        {
            enable_request = msOWSLookupMetadata(&map->web.metadata, "O", "enable_request");
            globally_enabled = msOWSParseRequestMetadata(enable_request, request, &disabled);
        }

        /* Check all layers */
        for(i=0; i<map->numlayers; i++)
        {
            int result = MS_FALSE;
            layerObj *lp;
            lp = (GET_LAYER(map, i));

            enable_request = msOWSLookupMetadata(&lp->metadata, namespaces, "enable_request");
            result = msOWSParseRequestMetadata(enable_request, request, &disabled);
            if (!result && disabled) continue; /* if the request has been explicitly set to disabled, continue */
            
            if (!result && !disabled) /* if the request has not been found in the wms metadata, */
            {                         /* check the ows namespace  */
            
                enable_request = msOWSLookupMetadata(&lp->metadata, "O", "enable_request");
                result = msOWSParseRequestMetadata(enable_request, request, &disabled);
                if (!result && disabled) continue;
            }
            
            if (result || (!disabled && globally_enabled))
                return MS_TRUE;
        }
    }

    return MS_FALSE;
}

/*
** msOWSRequestLayersEnabled()
**
** Check if the layers are visible for a specific OWS request.
**
** 'namespaces' is a string with a letter for each namespace to lookup 
** in the order they should be looked up. e.g. "MO" to lookup wms_ and ows_
** If namespaces is NULL then this function just does a regular metadata
** lookup.
**
** Generates an array of the layer ids enabled.
*/
void msOWSRequestLayersEnabled(mapObj *map, const char *namespaces, 
                               const char *request, owsRequestObj *ows_request)
{
    int disabled = MS_FALSE; /* explicitly disabled flag */
    int globally_enabled = MS_FALSE;
    const char *enable_request;

    if (ows_request->numlayers > 0) 
        msFree(ows_request->enabled_layers);
    
    ows_request->numlayers = 0;
    ows_request->enabled_layers = NULL;

    if (request == NULL || (map == NULL) || (map->numlayers <= 0))
        return;

    enable_request = msOWSLookupMetadata(&map->web.metadata, namespaces, "enable_request");
    globally_enabled = msOWSParseRequestMetadata(enable_request, request, &disabled);

    if (!globally_enabled && !disabled)
    {
        enable_request = msOWSLookupMetadata(&map->web.metadata, "O", "enable_request");
        globally_enabled = msOWSParseRequestMetadata(enable_request, request, &disabled);
    }

    if (map->numlayers)
    {
        int i, layers_size = map->numlayers; //for most of cases, this will be relatively small

        ows_request->enabled_layers = (int*)msSmallMalloc(sizeof(int)*layers_size);

        for(i=0; i<map->numlayers; i++)
        {
            int result = MS_FALSE;
            layerObj *lp;
            lp = (GET_LAYER(map, i));

            enable_request = msOWSLookupMetadata(&lp->metadata, namespaces, "enable_request");
            result = msOWSParseRequestMetadata(enable_request, request, &disabled);
            if (!result && disabled) continue; /* if the request has been explicitly set to disabled, continue */
            
            if (!result && !disabled) /* if the request has not been found in the wms metadata, */
            {                         /* check the ows namespace  */
            
                enable_request = msOWSLookupMetadata(&lp->metadata, "O", "enable_request");
                result = msOWSParseRequestMetadata(enable_request, request, &disabled);
                if (!result && disabled) continue;
            }
            
            if (result || (!disabled && globally_enabled))
            {
                ows_request->enabled_layers[ows_request->numlayers] = lp->index;
                ows_request->numlayers++;
            }
        }

        if (ows_request->numlayers == 0) 
        {
            msFree(ows_request->enabled_layers);
            ows_request->enabled_layers = NULL;
        }
    }
}

/* msOWSParseRequestMetadata 
 *  
 * This function parse a enable_request metadata string and check if the
 * given request is present and enabled.
 */
int msOWSParseRequestMetadata(const char *metadata, const char *request, int *disabled)
{
    char requestBuffer[32];
    int wordFlag = MS_FALSE;
    int disableFlag = MS_FALSE;
    int allFlag = MS_FALSE;
    char *bufferPtr, *ptr = NULL;
    int i;
    size_t len = 0;

    *disabled = MS_FALSE;
    
    if (metadata == NULL)
        return MS_FALSE;

    ptr = (char*)metadata;
    len = strlen(ptr);
    requestBuffer[0] = '\0';
    bufferPtr = requestBuffer;
    
    for (i=0; i<=len;++i,++ptr) {
        
        if (!wordFlag && isspace(*ptr))
            continue;
        
        wordFlag = MS_TRUE;
        
        if (*ptr == '!') {
            disableFlag = MS_TRUE;
            continue;
        }
        else if ( (*ptr == ' ') || (*ptr != '\0' && ptr[1] == '\0')) { /* end of word */
            if (ptr[1] == '\0' && *ptr != ' ') {
                *bufferPtr = *ptr;
                ++bufferPtr;
            }
            
            *bufferPtr = '\0'; 
            if (strcasecmp(request, requestBuffer) == 0) {
                *disabled =  MS_TRUE; /* explicitly found, will stop the process in msOWSRequestIsEnabled() */
                return (disableFlag ? MS_FALSE:MS_TRUE);
            }
            else {
                if (strcmp("*", requestBuffer) == 0) { /* check if we read the all flag */
                    if (disableFlag)
                        *disabled =  MS_TRUE;
                    allFlag = disableFlag ? MS_FALSE:MS_TRUE;
                }
                /* reset flags */
                wordFlag = MS_FALSE;
                disableFlag = MS_FALSE;
                bufferPtr = requestBuffer;
            }
        }
        else {
            *bufferPtr = *ptr;
            ++bufferPtr;
        } 
    }
    
    return allFlag;
}

/*
** msOWSLookupMetadata()
**
** Attempts to lookup a given metadata name in multiple OWS namespaces.
**
** 'namespaces' is a string with a letter for each namespace to lookup 
** in the order they should be looked up. e.g. "MO" to lookup wms_ and ows_
** If namespaces is NULL then this function just does a regular metadata
** lookup.
*/
const char *msOWSLookupMetadata(hashTableObj *metadata, 
                                const char *namespaces, const char *name)
{
    const char *value = NULL;

    if (namespaces == NULL)
    {
        value = msLookupHashTable(metadata, (char*)name);
    }
    else
    {
        char buf[100] = "ows_";

        strlcpy(buf+4, name, 96);

        while (value == NULL && *namespaces != '\0')
        {
            switch (*namespaces)
            {
              case 'O':         /* ows_... */
                buf[0] = 'o';
                buf[1] = 'w';
                buf[2] = 's';
                break;
              case 'M':         /* wms_... */
                buf[0] = 'w';
                buf[1] = 'm';
                buf[2] = 's';
                break;
              case 'F':         /* wfs_... */
                buf[0] = 'w';
                buf[1] = 'f';
                buf[2] = 's';
                break;
              case 'C':         /* wcs_... */
                buf[0] = 'w';
                buf[1] = 'c';
                buf[2] = 's';
                break;
              case 'G':         /* gml_... */
                buf[0] = 'g';
                buf[1] = 'm';
                buf[2] = 'l';
                break;
                case 'S':         /* sos_... */
                buf[0] = 's';
                buf[1] = 'o';
                buf[2] = 's';
                break;
              default:
                /* We should never get here unless an invalid code (typo) is */
                /* present in the code, but since this happened before... */
                msSetError(MS_WMSERR, 
                           "Unsupported metadata namespace code (%c).",
                           "msOWSLookupMetadata()", *namespaces );
                assert(MS_FALSE);
                return NULL;
            }

            value = msLookupHashTable(metadata, buf);
            namespaces++;
        }
    }

    return value;
}

/*
** msOWSLookupMetadata2()
**
** Attempts to lookup a given metadata name in multiple hashTables, and
** in multiple OWS namespaces within each. First searches the primary
** table and if no result is found, attempts the search using the 
** secondary (fallback) table.
**
** 'namespaces' is a string with a letter for each namespace to lookup 
** in the order they should be looked up. e.g. "MO" to lookup wms_ and ows_
** If namespaces is NULL then this function just does a regular metadata
** lookup.
*/
const char *msOWSLookupMetadata2(hashTableObj *pri,
                                        hashTableObj *sec,
                                        const char *namespaces,
                                        const char *name)
{
    const char *result;
    
    if ((result = msOWSLookupMetadata(pri, namespaces, name)) == NULL)
    {
        /* Try the secondary table */
        result = msOWSLookupMetadata(sec, namespaces, name);
    }

    return result;
}


/* msOWSParseVersionString()
**
** Parse a version string in the format "a.b.c" or "a.b" and return an
** integer in the format 0x0a0b0c suitable for regular integer comparisons.
**
** Returns one of OWS_VERSION_NOTSET or OWS_VERSION_BADFORMAT if version 
** could not be parsed.
*/
int msOWSParseVersionString(const char *pszVersion)
{
    char **digits = NULL;
    int numDigits = 0;

    if (pszVersion)
    {
        int nVersion = 0;
        digits = msStringSplit(pszVersion, '.', &numDigits);
        if (digits == NULL || numDigits < 2 || numDigits > 3)
        {
            msSetError(MS_OWSERR, 
                       "Invalid version (%s). Version must be in the "
                       "format 'x.y' or 'x.y.z'",
                       "msOWSParseVersionString()", pszVersion);
            if (digits)
                msFreeCharArray(digits, numDigits);
            return OWS_VERSION_BADFORMAT;
        }

        nVersion = atoi(digits[0])*0x010000;
        nVersion += atoi(digits[1])*0x0100;
        if (numDigits > 2)
            nVersion += atoi(digits[2]);

        msFreeCharArray(digits, numDigits);

        return nVersion;
    }

    return OWS_VERSION_NOTSET;
}

/* msOWSGetVersionString()
**
** Returns a OWS version string in the format a.b.c from the integer
** version value passed as argument (0x0a0b0c)
**
** Fills in the pszBuffer and returns a reference to it. Recommended buffer
** size is OWS_VERSION_MAXLEN chars.
*/
const char *msOWSGetVersionString(int nVersion, char *pszBuffer)
{

    if (pszBuffer)
        snprintf(pszBuffer, OWS_VERSION_MAXLEN-1, "%d.%d.%d", 
            (nVersion/0x10000)%0x100, (nVersion/0x100)%0x100, nVersion%0x100);

    return pszBuffer;
}


#if defined(USE_WMS_SVR) || defined (USE_WFS_SVR) || defined (USE_WCS_SVR) || defined(USE_SOS_SVR) || defined(USE_WMS_LYR) || defined(USE_WFS_LYR)

#if !defined(USE_PROJ)
#error "PROJ.4 is required for WMS, WFS, WCS and SOS Server Support."
#endif

/*
** msRenameLayer()
*/
static int msRenameLayer(layerObj *lp, int count)
{
    char *newname;
    newname = (char*)malloc((strlen(lp->name)+5)*sizeof(char));
    if (!newname) 
    {
        msSetError(MS_MEMERR, NULL, "msRenameLayer()");
        return MS_FAILURE;
    }
    sprintf(newname, "%s_%2.2d", lp->name, count);
    free(lp->name);
    lp->name = newname;
    
    return MS_SUCCESS;
}

/*
** msOWSMakeAllLayersUnique()
*/
int msOWSMakeAllLayersUnique(mapObj *map)
{
  int i, j;

  /* Make sure all layers in the map file have valid and unique names */
  for(i=0; i<map->numlayers; i++)
  {
      int count=1;
      for(j=i+1; j<map->numlayers; j++)
      {
          if (GET_LAYER(map, i)->name == NULL || GET_LAYER(map, j)->name == NULL)
          {
              continue;
          }
          if (strcasecmp(GET_LAYER(map, i)->name, GET_LAYER(map, j)->name) == 0 &&
              msRenameLayer((GET_LAYER(map, j)), ++count) != MS_SUCCESS)
          {
              return MS_FAILURE;
          }
      }

      /* Don't forget to rename the first layer if duplicates were found */
      if (count > 1 && msRenameLayer((GET_LAYER(map, i)), 1) != MS_SUCCESS)
      {
          return MS_FAILURE;
      }
  }
  return MS_SUCCESS;
}

/*
** msOWSNegotiateVersion()
**
** returns the most suitable version an OWS is to support given a client
** version parameter.
**
** supported_versions must be ordered from highest to lowest
**
** Returns a version integer of the supported version
**
*/

int msOWSNegotiateVersion(int requested_version, int supported_versions[], int num_supported_versions) {
  int i;

  /* if version is not set return highest version */
  if (! requested_version)
    return supported_versions[0];

  /* if the requested version is lower than the lowest version return the lowest version  */
  if (requested_version < supported_versions[num_supported_versions-1])
    return supported_versions[num_supported_versions-1];

  /* return the first entry that's lower than or equal to the requested version */
  for (i = 0; i < num_supported_versions; i++) {
    if (supported_versions[i] <= requested_version)
      return supported_versions[i];
  }

  return requested_version;
}

/*
** msOWSTerminateOnlineResource()
**
** Append trailing "?" or "&" to an onlineresource URL if it doesn't have
** one already. The returned string is then ready to append GET parameters
** to it.
**
** Returns a newly allocated string that should be freed by the caller or
** NULL in case of error.
*/
char * msOWSTerminateOnlineResource(const char *src_url)
{
    char *online_resource = NULL;
    size_t buffer_size = 0;

    if (src_url == NULL) 
        return NULL;
    
    buffer_size = strlen(src_url)+2;
    online_resource = (char*) malloc(buffer_size);

    if (online_resource == NULL)
    {
        msSetError(MS_MEMERR, NULL, "msOWSTerminateOnlineResource()");
        return NULL;
    }

    strlcpy(online_resource, src_url, buffer_size);

    /* Append trailing '?' or '&' if missing. */
    if (strchr(online_resource, '?') == NULL)
        strlcat(online_resource, "?", buffer_size);
    else
    {
        char *c;
        c = online_resource+strlen(online_resource)-1;
        if (*c != '?' && *c != '&') 
            strlcpy(c+1, "&", buffer_size-strlen(online_resource));
    }

    return online_resource;
}

/*
** msOWSGetOnlineResource()
**
** Return the online resource for this service.  First try to lookup 
** specified metadata, and if not found then try to build the URL ourselves.
**
** Returns a newly allocated string that should be freed by the caller or
** NULL in case of error.
*/
char * msOWSGetOnlineResource(mapObj *map, const char *namespaces, const char *metadata_name, 
                              cgiRequestObj *req)
{
    const char *value;
    char *online_resource = NULL;

    /* We need this script's URL, including hostname. */
    /* Default to use the value of the "onlineresource" metadata, and if not */
    /* set then build it: "http://$(SERVER_NAME):$(SERVER_PORT)$(SCRIPT_NAME)?" */
    /* (+append the map=... param if it was explicitly passed in QUERY_STRING) */
    /*  */
    if ((value = msOWSLookupMetadata(&(map->web.metadata), namespaces, metadata_name))) 
    {
        online_resource = msOWSTerminateOnlineResource(value);
    }
    else 
    {
        if ((online_resource = msBuildOnlineResource(map, req)) == NULL)
        {
            msSetError(MS_CGIERR, "Impossible to establish server URL.  Please set \"%s\" metadata.", "msOWSGetOnlineResource()", metadata_name);
            return NULL;
        }
    }

    return online_resource;
}


/* msOWSGetSchemasLocation()
**
** schemas location is the root of the web tree where all WFS-related 
** schemas can be found on this server.  These URLs must exist in order 
** to validate xml.
**
** Use value of "ows_schemas_location" metadata, if not set then
** return ".." as a default
*/
const char *msOWSGetSchemasLocation(mapObj *map)
{
    const char *schemas_location;

    schemas_location = msLookupHashTable(&(map->web.metadata), 
                                         "ows_schemas_location");
    if (schemas_location == NULL)
      schemas_location = OWS_DEFAULT_SCHEMAS_LOCATION;

    return schemas_location;
}

/* msOWSGetLanguage()
**
** returns the language via MAP/WEB/METADATA/ows_language
**
** Use value of "ows_language" metadata, if not set then
** return "undefined" as a default
*/
const char *msOWSGetLanguage(mapObj *map, const char *context)
{
    const char *language;

    /* if this is an exception, MapServer always returns Exception
       messages in en-US
    */
    if (strcmp(context,"exception") == 0) {
      language = MS_ERROR_LANGUAGE;
    }
    /* if not, fetch language from mapfile metadata */
    else {
      language = msLookupHashTable(&(map->web.metadata), "ows_language");

      if (language == NULL) {
        language = "undefined";
      }
    }
    return language;
}

/*
** msOWSPrintMetadata()
**
** Attempt to output a capability item.  If corresponding metadata is not 
** found then one of a number of predefined actions will be taken. 
** If a default value is provided and metadata is absent then the 
** default will be used.
*/

int msOWSPrintMetadata(FILE *stream, hashTableObj *metadata, 
                       const char *namespaces, const char *name, 
                       int action_if_not_found, const char *format, 
                       const char *default_value) 
{
    const char *value = NULL;
    int status = MS_NOERR;

    if((value = msOWSLookupMetadata(metadata, namespaces, name)) != NULL)
    { 
        msIO_fprintf(stream, format, value);
    }
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory metadata '%s%s' was missing in this context. -->\n", (namespaces?"..._":""), name);
            status = action_if_not_found;
        }

        if (default_value)
            msIO_fprintf(stream, format, default_value);
    }

    return status;
}


/*
** msOWSPrintEncodeMetadata()
**
** Attempt to output a capability item.  If corresponding metadata is not 
** found then one of a number of predefined actions will be taken. 
** If a default value is provided and metadata is absent then the 
** default will be used.
** Also encode the value with msEncodeHTMLEntities.
*/

int msOWSPrintEncodeMetadata(FILE *stream, hashTableObj *metadata, 
                             const char *namespaces, const char *name, 
                             int action_if_not_found, 
                             const char *format, const char *default_value) 
{
    const char *value;
    char * pszEncodedValue=NULL;
    int status = MS_NOERR;

    if((value = msOWSLookupMetadata(metadata, namespaces, name)))
    {
        pszEncodedValue = msEncodeHTMLEntities(value);
        msIO_fprintf(stream, format, pszEncodedValue);
        free(pszEncodedValue);
    }
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory metadata '%s%s' was missing in this context. -->\n", (namespaces?"..._":""), name);
            status = action_if_not_found;
        }

        if (default_value)
        {
            pszEncodedValue = msEncodeHTMLEntities(default_value);
            msIO_fprintf(stream, format, default_value);
            free(pszEncodedValue);
        }
    }

    return status;
}


/*
** msOWSGetEncodeMetadata()
**
** Equivalent to msOWSPrintEncodeMetadata. Returns en encoded value of the
** metadata or the default value.
** Caller should free the returned string.
*/
char *msOWSGetEncodeMetadata(hashTableObj *metadata, 
                             const char *namespaces, const char *name, 
                             const char *default_value)
{
    const char *value;
    char * pszEncodedValue=NULL;    
    if((value = msOWSLookupMetadata(metadata, namespaces, name)))
      pszEncodedValue = msEncodeHTMLEntities(value);
    else if (default_value)
      pszEncodedValue = msEncodeHTMLEntities(default_value);

    return pszEncodedValue;
}
      

/*
** msOWSPrintValidateMetadata()
**
** Attempt to output a capability item.  If corresponding metadata is not 
** found then one of a number of predefined actions will be taken. 
** If a default value is provided and metadata is absent then the 
** default will be used.
** Also validate the value with msIsXMLTagValid.
*/

int msOWSPrintValidateMetadata(FILE *stream, hashTableObj *metadata, 
                               const char *namespaces, const char *name, 
                               int action_if_not_found, 
                               const char *format, const char *default_value) 
{
    const char *value;
    int status = MS_NOERR;

    if((value = msOWSLookupMetadata(metadata, namespaces, name)))
    {
        if(msIsXMLTagValid(value) == MS_FALSE)
            msIO_fprintf(stream, "<!-- WARNING: The value '%s' is not valid in a "
                    "XML tag context. -->\n", value);
        msIO_fprintf(stream, format, value);
    }
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory metadata '%s%s' was missing in this context. -->\n", (namespaces?"..._":""), name);
            status = action_if_not_found;
        }

        if (default_value)
        {
            if(msIsXMLTagValid(default_value) == MS_FALSE)
                msIO_fprintf(stream, "<!-- WARNING: The value '%s' is not valid "
                        "in a XML tag context. -->\n", default_value);
            msIO_fprintf(stream, format, default_value);
        }
    }

    return status;
}

/*
** msOWSPrintGroupMetadata()
**
** Attempt to output a capability item.  If corresponding metadata is not 
** found then one of a number of predefined actions will be taken. 
** If a default value is provided and metadata is absent then the 
** default will be used.
*/

int msOWSPrintGroupMetadata(FILE *stream, mapObj *map, char* pszGroupName, 
                            const char *namespaces, const char *name, 
                            int action_if_not_found, 
                            const char *format, const char *default_value) 
{
    const char *value;
    char *encoded;
    int status = MS_NOERR;
    int i;

    for (i=0; i<map->numlayers; i++)
    {
        if (GET_LAYER(map, i)->group && (strcmp(GET_LAYER(map, i)->group, pszGroupName) == 0) && &(GET_LAYER(map, i)->metadata))
       {
         if((value = msOWSLookupMetadata(&(GET_LAYER(map, i)->metadata), namespaces, name)))
         { 
            encoded = msEncodeHTMLEntities(value);
            msIO_fprintf(stream, format, encoded);
            msFree(encoded);
            return status;
         }
       }
    }

    if (action_if_not_found == OWS_WARN)
    {
       msIO_fprintf(stream, "<!-- WARNING: Mandatory metadata '%s%s' was missing in this context. -->\n", (namespaces?"..._":""), name);
       status = action_if_not_found;
    }

    if (default_value)
    {
       encoded = msEncodeHTMLEntities(default_value);
       msIO_fprintf(stream, format, encoded);
       msFree(encoded);
    }
   
    return status;
}

/* msOWSPrintURLType()
**
** Attempt to output a URL item in capabilties.  If corresponding metadata 
** is not found then one of a number of predefined actions will be taken. 
** Since it's a capability item, five metadata will be used to populate the
** XML elements.
**
** The 'name' argument is the basename of the metadata items relating to this 
** URL type and the suffixes _type, _width, _height, _format and _href will 
** be appended to the name in the metadata search.
** e.g. passing name=metadataurl will result in the following medata entries 
** being used:
**    ows_metadataurl_type
**    ows_metadataurl_format
**    ows_metadataurl_href
**    ... (width and height are unused for metadata)
**
** As for all the msOWSPrint*() functions, the namespace argument specifies 
** which prefix (ows_, wms_, wcs_, etc.) is used for the metadata names above.
**
** Then the final string will be built from 
** the tag_name and the five metadata. The template is:
** <tag_name%type%width%height%format>%href</tag_name>
**
** For example the width format will usually be " width=\"%s\"". 
** An extern format will be "> <Format>%s</Format"
**
** Another template template may be used, but it needs to contains 5 %s, 
** otherwise leave it to NULL. If tag_format is used then you don't need the 
** tag_name and the tabspace.
**
** Note that all values will be HTML-encoded.
**/
int msOWSPrintURLType(FILE *stream, hashTableObj *metadata, 
                      const char *namespaces, const char *name, 
                      int action_if_not_found, const char *tag_format, 
                      const char *tag_name, const char *type_format, 
                      const char *width_format, const char *height_format, 
                      const char *urlfrmt_format, const char *href_format,
                      int type_is_mandatory, int width_is_mandatory, 
                      int height_is_mandatory, int format_is_mandatory, 
                      int href_is_mandatory, const char *default_type, 
                      const char *default_width, const char *default_height, 
                      const char *default_urlfrmt, const char *default_href, 
                      const char *tabspace)
{
    const char *value;
    char *metadata_name;
    size_t buffer_size = 0, buffer_size_tmp = 0;
    char *encoded;
    int status = MS_NOERR;
    char *type=NULL, *width=NULL, *height=NULL, *urlfrmt=NULL, *href=NULL;

    buffer_size = strlen(name)+10;
    metadata_name = (char*)malloc(buffer_size);

    /* Get type */
    if(type_format != NULL)
    {
        snprintf(metadata_name, buffer_size, "%s_type", name);
        value = msOWSLookupMetadata(metadata, namespaces, metadata_name);
        if(value != NULL)
        {
            encoded = msEncodeHTMLEntities(value);
            buffer_size_tmp = strlen(type_format)+strlen(encoded);
            type = (char*)malloc(buffer_size_tmp);
            snprintf(type, buffer_size_tmp, type_format, encoded);
            msFree(encoded);
        }
    }

    /* Get width */
    if(width_format != NULL)
    {
        snprintf(metadata_name, buffer_size, "%s_width", name);
        value = msOWSLookupMetadata(metadata, namespaces, metadata_name);
        if(value != NULL)
        {
            encoded = msEncodeHTMLEntities(value);
            buffer_size_tmp = strlen(width_format)+strlen(encoded);
            width = (char*)malloc(buffer_size_tmp);
            snprintf(width, buffer_size_tmp, width_format, encoded);
            msFree(encoded);
        }
    }

    /* Get height */
    if(height_format != NULL)
    {
        snprintf(metadata_name, buffer_size, "%s_height", name);
        value = msOWSLookupMetadata(metadata, namespaces, metadata_name);
        if(value != NULL)
        {
            encoded = msEncodeHTMLEntities(value);
            buffer_size_tmp = strlen(height_format)+strlen(encoded);
            height = (char*)malloc(buffer_size_tmp);
            snprintf(height, buffer_size_tmp, height_format, encoded);
            msFree(encoded);
        }
    }

    /* Get format */
    if(urlfrmt_format != NULL)
    {
        snprintf(metadata_name, buffer_size, "%s_format", name);
        value = msOWSLookupMetadata(metadata, namespaces, metadata_name);
        if(value != NULL)
        {
            encoded = msEncodeHTMLEntities(value);
            buffer_size_tmp = strlen(urlfrmt_format)+strlen(encoded);
            urlfrmt = (char*)malloc(buffer_size_tmp);
            snprintf(urlfrmt, buffer_size_tmp, urlfrmt_format, encoded);
            msFree(encoded);
        }
    }

    /* Get href */
    if(href_format != NULL)
    {
        snprintf(metadata_name, buffer_size, "%s_href", name);
        value = msOWSLookupMetadata(metadata, namespaces, metadata_name);
        if(value != NULL)
        {
            encoded = msEncodeHTMLEntities(value);
            buffer_size_tmp = strlen(href_format)+strlen(encoded);
            href = (char*)malloc(buffer_size_tmp);
            snprintf(href, buffer_size_tmp, href_format, encoded);
            msFree(encoded);
        }
    }

    msFree(metadata_name);

    if(type || width || height || urlfrmt || href || 
       (!metadata && (default_type || default_width || default_height || 
                      default_urlfrmt || default_href)))
    {
        if((!type && type_is_mandatory) || (!width && width_is_mandatory) || 
           (!height && height_is_mandatory) || 
           (!urlfrmt && format_is_mandatory) || (!href && href_is_mandatory))
        {
            msIO_fprintf(stream, "<!-- WARNING: Some mandatory elements for '%s' are missing in this context. -->\n", tag_name);
            if (action_if_not_found == OWS_WARN)
            {
                msIO_fprintf(stream, "<!-- WARNING: Mandatory metadata '%s%s' was missing in this context. -->\n", (namespaces?"..._":""), name);
                status = action_if_not_found;
            }
        }
        else
        {
            if(!type && type_format && default_type)
            {
                buffer_size_tmp = strlen(type_format) + strlen(default_type) + 2;
                type = (char*) malloc(buffer_size_tmp);
                snprintf(type, buffer_size_tmp, type_format, default_type);
            }
            else if(!type)
                type = msStrdup("");
            if(!width && width_format && default_width)
            {
                buffer_size_tmp = strlen(width_format) + strlen(default_width) + 2;
                width = (char*) malloc(buffer_size_tmp);
                snprintf(width, buffer_size_tmp, width_format, default_width);
            }
            else if(!width)
                width = msStrdup("");
            if(!height && height_format && default_height)
            {
                buffer_size_tmp =  strlen(height_format) + strlen(default_height) + 2;
                height = (char*) malloc(buffer_size_tmp);
                snprintf(height, buffer_size_tmp, height_format, default_height);
            }
            else if(!height)
                height = msStrdup("");
            if(!urlfrmt && urlfrmt_format && default_urlfrmt)
            {
                buffer_size_tmp = strlen(urlfrmt_format) + strlen(default_urlfrmt) + 2;
                urlfrmt = (char*) malloc(buffer_size_tmp);
                snprintf(urlfrmt, buffer_size_tmp, urlfrmt_format, default_urlfrmt);
            }
            else if(!urlfrmt)
                urlfrmt = msStrdup("");
            if(!href && href_format && default_href)
            {
                buffer_size_tmp = strlen(href_format) + strlen(default_href) + 2;
                href = (char*) malloc(buffer_size_tmp);
                snprintf(href, buffer_size_tmp, href_format, default_href);
            }
            else if(!href)
                href = msStrdup("");

            if(tag_format == NULL)
                msIO_fprintf(stream, "%s<%s%s%s%s%s>%s</%s>\n", tabspace, 
                             tag_name, type, width, height, urlfrmt, href, 
                             tag_name);
            else
                msIO_fprintf(stream, tag_format, 
                             type, width, height, urlfrmt, href);
        }

        msFree(type);
        msFree(width);
        msFree(height);
        msFree(urlfrmt);
        msFree(href);
    }
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory metadata '%s%s' was missing in this context. -->\n", (namespaces?"..._":""), name);
            status = action_if_not_found;
        }
    }

    return status;
}

/* msOWSPrintParam()
**
** Same as printMetadata() but applied to mapfile parameters.
**/
int msOWSPrintParam(FILE *stream, const char *name, const char *value, 
                    int action_if_not_found, const char *format, 
                    const char *default_value) 
{
    int status = MS_NOERR;

    if(value && strlen(value) > 0)
    { 
        msIO_fprintf(stream, format, value);
    }
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory mapfile parameter '%s' was missing in this context. -->\n", name);
            status = action_if_not_found;
        }

        if (default_value)
            msIO_fprintf(stream, format, default_value);
    }

    return status;
}

/* msOWSPrintEncodeParam()
**
** Same as printEncodeMetadata() but applied to mapfile parameters.
**/
int msOWSPrintEncodeParam(FILE *stream, const char *name, const char *value, 
                          int action_if_not_found, const char *format, 
                          const char *default_value) 
{
    int status = MS_NOERR;
    char *encode;

    if(value && strlen(value) > 0)
    { 
        encode = msEncodeHTMLEntities(value);
        msIO_fprintf(stream, format, encode);
        msFree(encode);
    }
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory mapfile parameter '%s' was missing in this context. -->\n", name);
            status = action_if_not_found;
        }

        if (default_value)
        {
            encode = msEncodeHTMLEntities(default_value);
            msIO_fprintf(stream, format, encode);
            msFree(encode);
        }
    }

    return status;
}

/* msOWSPrintMetadataList()
**
** Prints comma-separated lists metadata.  (e.g. keywordList)
**/
int msOWSPrintMetadataList(FILE *stream, hashTableObj *metadata, 
                           const char *namespaces, const char *name, 
                           const char *startTag, 
                           const char *endTag, const char *itemFormat,
                           const char *default_value) 
{
    const char *value;
    if((value = msOWSLookupMetadata(metadata, namespaces, name)) ||
       (value = default_value) != NULL ) 
    {
      char **keywords;
      int numkeywords;
      
      keywords = msStringSplit(value, ',', &numkeywords);
      if(keywords && numkeywords > 0) {
        int kw;
	    if(startTag) msIO_fprintf(stream, "%s", startTag);
	    for(kw=0; kw<numkeywords; kw++) 
            msIO_fprintf(stream, itemFormat, keywords[kw]);
	    if(endTag) msIO_fprintf(stream, "%s", endTag);
	    msFreeCharArray(keywords, numkeywords);
      }
      return MS_TRUE;
    }
    return MS_FALSE;
}

/* msOWSPrintEncodeMetadataList()
**
** Prints comma-separated lists metadata.  (e.g. keywordList)
** This will print HTML encoded values.
**/
int msOWSPrintEncodeMetadataList(FILE *stream, hashTableObj *metadata, 
                                 const char *namespaces, const char *name, 
                                 const char *startTag, 
                                 const char *endTag, const char *itemFormat,
                                 const char *default_value) 
{
    const char *value;
    char *encoded;
    if((value = msOWSLookupMetadata(metadata, namespaces, name)) ||
       (value = default_value) != NULL ) 
    {
      char **keywords;
      int numkeywords;
      
      keywords = msStringSplit(value, ',', &numkeywords);
      if(keywords && numkeywords > 0) {
        int kw;
	    if(startTag) msIO_fprintf(stream, "%s", startTag);
	    for(kw=0; kw<numkeywords; kw++)
            {
                encoded = msEncodeHTMLEntities(keywords[kw]);
                msIO_fprintf(stream, itemFormat, encoded);
                msFree(encoded);
            }
	    if(endTag) msIO_fprintf(stream, "%s", endTag);
	    msFreeCharArray(keywords, numkeywords);
      }
      return MS_TRUE;
    }
    return MS_FALSE;
}

/* msOWSPrintEncodeParamList()
**
** Same as msOWSPrintEncodeMetadataList() but applied to mapfile parameters.
**/
int msOWSPrintEncodeParamList(FILE *stream, const char *name, 
                              const char *value, int action_if_not_found, 
                              char delimiter, const char *startTag, 
                              const char *endTag, const char *format, 
                              const char *default_value) 
{
    int status = MS_NOERR;
    char *encoded;
    char **items = NULL;
    int numitems = 0, i;

    if(value && strlen(value) > 0)
        items = msStringSplit(value, delimiter, &numitems);
    else
    {
        if (action_if_not_found == OWS_WARN)
        {
            msIO_fprintf(stream, "<!-- WARNING: Mandatory mapfile parameter '%s' was missing in this context. -->\n", name);
            status = action_if_not_found;
        }

        if (default_value)
            items = msStringSplit(default_value, delimiter, &numitems);
    }

    if(items && numitems > 0)
    {
        if(startTag) msIO_fprintf(stream, "%s", startTag);
        for(i=0; i<numitems; i++)
        {
            encoded = msEncodeHTMLEntities(items[i]);
            msIO_fprintf(stream, format, encoded);
            msFree(encoded);
        }
        if(endTag) msIO_fprintf(stream, "%s", endTag);
        msFreeCharArray(items, numitems);
    }

    return status;
}


/*
** msOWSPrintEX_GeographicBoundingBox()
**
** Print a EX_GeographicBoundingBox tag for WMS1.3.0
**
*/
void msOWSPrintEX_GeographicBoundingBox(FILE *stream, const char *tabspace, 
                                        rectObj *extent, projectionObj *srcproj)

{
  const char *pszTag = "EX_GeographicBoundingBox";  /* The default for WMS */
  rectObj ext;

  ext = *extent;

  /* always project to lat long */
  if (srcproj->numargs > 0 && !pj_is_latlong(srcproj->proj)) {
    projectionObj wgs84;
    msInitProjection(&wgs84);
    msLoadProjectionString(&wgs84, "+proj=longlat +datum=WGS84");
    msProjectRect(srcproj, &wgs84, &ext);
    msFreeProjection(&wgs84);
  }
  

  msIO_fprintf(stream, "%s<%s>\n", tabspace, pszTag);
  msIO_fprintf(stream, "%s    <westBoundLongitude>%g</westBoundLongitude>\n", tabspace, ext.minx);
  msIO_fprintf(stream, "%s    <eastBoundLongitude>%g</eastBoundLongitude>\n", tabspace, ext.maxx);
  msIO_fprintf(stream, "%s    <southBoundLatitude>%g</southBoundLatitude>\n", tabspace, ext.miny);
  msIO_fprintf(stream, "%s    <northBoundLatitude>%g</northBoundLatitude>\n", tabspace, ext.maxy);
  msIO_fprintf(stream, "%s</%s>\n", tabspace, pszTag);

  //msIO_fprintf(stream, "%s<%s minx=\"%g\" miny=\"%g\" maxx=\"%g\" maxy=\"%g\" />\n", 
  //      tabspace, pszTag, ext.minx, ext.miny, ext.maxx, ext.maxy);
}

/*
** msOWSPrintLatLonBoundingBox()
**
** Print a LatLonBoundingBox tag for WMS, or LatLongBoundingBox for WFS
** ... yes, the tag name differs between WMS and WFS, yuck!
**
*/
void msOWSPrintLatLonBoundingBox(FILE *stream, const char *tabspace, 
                                 rectObj *extent, projectionObj *srcproj,
                                 projectionObj *wfsproj, int nService)
{
  const char *pszTag = "LatLonBoundingBox";  /* The default for WMS */
  rectObj ext;

  ext = *extent;

  if (nService == OWS_WMS) { /* always project to lat long */
    if (srcproj->numargs > 0 && !pj_is_latlong(srcproj->proj)) {
        projectionObj wgs84;
        msInitProjection(&wgs84);
        msLoadProjectionString(&wgs84, "+proj=longlat +datum=WGS84");
        msProjectRect(srcproj, &wgs84, &ext);
        msFreeProjection(&wgs84);
    }
  }

  if (nService == OWS_WFS) {
      pszTag = "LatLongBoundingBox";
      if (wfsproj) {
          if (msProjectionsDiffer(srcproj, wfsproj) == MS_TRUE)
              msProjectRect(srcproj, wfsproj, &ext);
      }
  }

  msIO_fprintf(stream, "%s<%s minx=\"%g\" miny=\"%g\" maxx=\"%g\" maxy=\"%g\" />\n", 
         tabspace, pszTag, ext.minx, ext.miny, ext.maxx, ext.maxy);
}

/*
** Emit a bounding box if we can find projection information.
** If <namespaces>_bbox_extended is not set, emit a single bounding box 
** using the layer's native SRS (ignoring any <namespaces>_srs metadata).
**
** If <namespaces>_bbox_extended is set to true, emit a bounding box
** for every projection listed in the <namespaces>_srs list.
** Check the map level metadata for both _bbox_extended and _srs,
** if there is no such metadata at the layer level.
** (These settings make more sense at the global/map level anyways)
*/
void msOWSPrintBoundingBox(FILE *stream, const char *tabspace, 
                           rectObj *extent, 
                           projectionObj *srcproj,
                           hashTableObj *layer_meta,
                           hashTableObj *map_meta,
                           const char *namespaces,
                           int wms_version) 
{
  const char	*value, *resx, *resy, *wms_bbox_extended, *epsg_str;
  char *encoded, *encoded_resx, *encoded_resy;
  char **epsgs;
  int i, num_epsgs;
  projectionObj proj;
  rectObj ext;

  wms_bbox_extended = msOWSLookupMetadata2(layer_meta, map_meta, namespaces, "bbox_extended");
  if( wms_bbox_extended && strncasecmp(wms_bbox_extended, "true", 5) == 0 ) 
  {
    /* get a list of all projections from the metadata 
       try the layer metadata first, otherwise use the map's */
    if( msOWSLookupMetadata(layer_meta, namespaces, "srs") ) 
    {
      epsg_str = msOWSGetEPSGProj(srcproj, layer_meta, namespaces, MS_FALSE);
    } else {
      epsg_str = msOWSGetEPSGProj(srcproj, map_meta, namespaces, MS_FALSE);
    }
    epsgs = msStringSplit(epsg_str, ' ', &num_epsgs);
  } else {
    /* Look for EPSG code in PROJECTION block only.  "wms_srs" metadata cannot be
     * used to establish the native projection of a layer for BoundingBox purposes.
     */
    epsgs = (char **) msSmallMalloc(sizeof(char *));
    num_epsgs = 1;
    epsgs[0] = msStrdup( msOWSGetEPSGProj(srcproj, layer_meta, namespaces, MS_TRUE) );
  }

  for( i = 0; i < num_epsgs; i++)
  {
    value = epsgs[i]; 
    memcpy(&ext, extent, sizeof(rectObj));

    /* reproject the extents for each SRS's bounding box */
    msInitProjection(&proj);
    if (msLoadProjectionStringEPSG(&proj, (char *)value) == 0)
    {
      if (msProjectionsDiffer(srcproj, &proj) == MS_TRUE)
      {
        msProjectRect(srcproj, &proj, &ext);
      }
      /*for wms 1.3.0 we need to make sure that we present the BBOX with  
        a reversed axes for some espg codes*/
      if (wms_version >= OWS_1_3_0 && value && strncasecmp(value, "EPSG:", 5) == 0)
      {
        msAxisNormalizePoints( &proj, 1, &(ext.minx), &(ext.miny) );
        msAxisNormalizePoints( &proj, 1, &(ext.maxx), &(ext.maxy) );
      }
    }
    msFreeProjection( &proj );

    if( value != NULL )
    {
      encoded = msEncodeHTMLEntities(value);
      if (wms_version >= OWS_1_3_0)
        msIO_fprintf(stream, "%s<BoundingBox CRS=\"%s\"\n"
             "%s            minx=\"%g\" miny=\"%g\" maxx=\"%g\" maxy=\"%g\"",
             tabspace, encoded, 
             tabspace, ext.minx, ext.miny, 
             ext.maxx, ext.maxy);
      else
        msIO_fprintf(stream, "%s<BoundingBox SRS=\"%s\"\n"
             "%s            minx=\"%g\" miny=\"%g\" maxx=\"%g\" maxy=\"%g\"",
             tabspace, encoded, 
             tabspace, ext.minx, ext.miny, 
             ext.maxx, ext.maxy);

      msFree(encoded);

      if( (resx = msOWSLookupMetadata2( layer_meta, map_meta, "MFO", "resx" )) != NULL &&
          (resy = msOWSLookupMetadata2( layer_meta, map_meta, "MFO", "resy" )) != NULL )
      {
          encoded_resx = msEncodeHTMLEntities(resx);
          encoded_resy = msEncodeHTMLEntities(resy);
          msIO_fprintf( stream, "\n%s            resx=\"%s\" resy=\"%s\"",
                        tabspace, encoded_resx, encoded_resy );
          msFree(encoded_resx);
          msFree(encoded_resy);
      }
 
      msIO_fprintf( stream, " />\n" );
    }
  }
  msFreeCharArray(epsgs, num_epsgs);
}


/*
** Print the contact information
*/
void msOWSPrintContactInfo( FILE *stream, const char *tabspace, 
                            int nVersion, hashTableObj *metadata, 
                            const char *namespaces )
{
  /* contact information is a required element in 1.0.7 but the */
  /* sub-elements such as ContactPersonPrimary, etc. are not! */
  /* In 1.1.0, ContactInformation becomes optional. */
  if (nVersion > OWS_1_0_0) 
  {
    msIO_fprintf(stream, "%s<ContactInformation>\n", tabspace); 

      /* ContactPersonPrimary is optional, but when present then all its  */
      /* sub-elements are mandatory */

    if(msOWSLookupMetadata(metadata, namespaces, "contactperson") ||
       msOWSLookupMetadata(metadata, namespaces, "contactorganization")) 
    {
      msIO_fprintf(stream, "%s  <ContactPersonPrimary>\n", tabspace);

      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "contactperson", 
                  OWS_WARN, "      <ContactPerson>%s</ContactPerson>\n", NULL);
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "contactorganization", 
             OWS_WARN, "      <ContactOrganization>%s</ContactOrganization>\n",
             NULL);
      msIO_fprintf(stream, "%s  </ContactPersonPrimary>\n", tabspace);
    }

    if(msOWSLookupMetadata(metadata, namespaces, "contactposition"))
    {
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "contactposition", 
                    OWS_NOERR, "      <ContactPosition>%s</ContactPosition>\n",
                           NULL);
    }

      /* ContactAdress is optional, but when present then all its  */
      /* sub-elements are mandatory */
    if(msOWSLookupMetadata( metadata, namespaces, "addresstype" ) || 
       msOWSLookupMetadata( metadata, namespaces, "address" ) || 
       msOWSLookupMetadata( metadata, namespaces, "city" ) ||
       msOWSLookupMetadata( metadata, namespaces, "stateorprovince" ) || 
       msOWSLookupMetadata( metadata, namespaces, "postcode" ) ||
       msOWSLookupMetadata( metadata, namespaces, "country" )) 
    {
      msIO_fprintf(stream, "%s  <ContactAddress>\n", tabspace);

      msOWSPrintEncodeMetadata(stream, metadata, namespaces,"addresstype", OWS_WARN,
                              "        <AddressType>%s</AddressType>\n", NULL);
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "address", OWS_WARN,
                       "        <Address>%s</Address>\n", NULL);
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "city", OWS_WARN,
                    "        <City>%s</City>\n", NULL);
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "stateorprovince", 
           OWS_WARN,"        <StateOrProvince>%s</StateOrProvince>\n", NULL);
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "postcode", OWS_WARN,
                    "        <PostCode>%s</PostCode>\n", NULL);
      msOWSPrintEncodeMetadata(stream, metadata, namespaces, "country", OWS_WARN,
                    "        <Country>%s</Country>\n", NULL);
      msIO_fprintf(stream, "%s  </ContactAddress>\n", tabspace);
    }

    if(msOWSLookupMetadata(metadata, namespaces, "contactvoicetelephone"))
    {
        msOWSPrintEncodeMetadata(stream, metadata, namespaces, 
                                 "contactvoicetelephone", OWS_NOERR,
                   "      <ContactVoiceTelephone>%s</ContactVoiceTelephone>\n",
                           NULL);
    }

    if(msOWSLookupMetadata(metadata, namespaces, "contactfacsimiletelephone"))
    {
        msOWSPrintEncodeMetadata(stream, metadata, 
                           namespaces, "contactfacsimiletelephone", OWS_NOERR,
           "      <ContactFacsimileTelephone>%s</ContactFacsimileTelephone>\n",
                                 NULL);
    }

    if(msOWSLookupMetadata(metadata, namespaces, "contactelectronicmailaddress"))
    {
        msOWSPrintEncodeMetadata(stream, metadata, 
                           namespaces, "contactelectronicmailaddress", OWS_NOERR,
         "  <ContactElectronicMailAddress>%s</ContactElectronicMailAddress>\n",
                                 NULL);
    }
    msIO_fprintf(stream, "%s</ContactInformation>\n", tabspace);
  }
}

/*
** msOWSGetLayerExtent()
**
** Try to establish layer extent, first looking for "ows_extent" metadata, and
** if not found then call msLayerGetExtent() which will lookup the 
** layer->extent member, and if not found will open layer to read extent.
**
*/
int msOWSGetLayerExtent(mapObj *map, layerObj *lp, const char *namespaces, rectObj *ext)
{
  const char *value;

  if ((value = msOWSLookupMetadata(&(lp->metadata), namespaces, "extent")) != NULL)
  {
    char **tokens;
    int n;

    tokens = msStringSplit(value, ' ', &n);
    if (tokens==NULL || n != 4) {
      msSetError(MS_WMSERR, "Wrong number of arguments for EXTENT metadata.",
                 "msOWSGetLayerExtent()");
      return MS_FAILURE;
    }
    ext->minx = atof(tokens[0]);
    ext->miny = atof(tokens[1]);
    ext->maxx = atof(tokens[2]);
    ext->maxy = atof(tokens[3]);

    msFreeCharArray(tokens, n);
    return MS_SUCCESS;
  }
  else
  {
      return msLayerGetExtent(lp, ext);
  }

  return MS_FAILURE;
}


/**********************************************************************
 *                          msOWSExecuteRequests()
 *
 * Execute a number of WFS/WMS HTTP requests in parallel, and then 
 * update layerObj information with the result of the requests.
 **********************************************************************/
int msOWSExecuteRequests(httpRequestObj *pasReqInfo, int numRequests,
                         mapObj *map, int bCheckLocalCache)
{
    int nStatus, iReq;

    /* Execute requests */
#if defined(USE_CURL)
    nStatus = msHTTPExecuteRequests(pasReqInfo, numRequests, bCheckLocalCache);
#else
    msSetError(MS_WMSERR, "msOWSExecuteRequests() called apparently without libcurl configured, msHTTPExecuteRequests() not available.",
               "msOWSExecuteRequests()");
    return MS_FAILURE;
#endif

    /* Scan list of layers and call the handler for each layer type to */
    /* pass them the request results. */
    for(iReq=0; iReq<numRequests; iReq++)
    {
        if (pasReqInfo[iReq].nLayerId >= 0 && 
            pasReqInfo[iReq].nLayerId < map->numlayers)
        {
            layerObj *lp;

            lp = GET_LAYER(map, pasReqInfo[iReq].nLayerId);

            if (lp->connectiontype == MS_WFS)
                msWFSUpdateRequestInfo(lp, &(pasReqInfo[iReq]));
        }
    }

    return nStatus;
}

/**********************************************************************
 *                          msOWSProcessException()
 *
 **********************************************************************/
void msOWSProcessException(layerObj *lp, const char *pszFname, 
                           int nErrorCode, const char *pszFuncName)
{
    FILE *fp;

    if ((fp = fopen(pszFname, "r")) != NULL)
    {
        char *pszBuf=NULL;
        int   nBufSize=0;
        char *pszStart, *pszEnd;

        fseek(fp, 0, SEEK_END);
        nBufSize = ftell(fp);
        rewind(fp);
        pszBuf = (char*)malloc((nBufSize+1)*sizeof(char));
        if (pszBuf == NULL)
        {
            msSetError(MS_MEMERR, NULL, "msOWSProcessException()");
            fclose(fp);
            return;
        }

        if ((int) fread(pszBuf, 1, nBufSize, fp) != nBufSize)
        {
            msSetError(MS_IOERR, NULL, "msOWSProcessException()");
            free(pszBuf);
            fclose(fp);
            return;
        }

        pszBuf[nBufSize] = '\0';


        /* OK, got the data in the buffer.  Look for the <Message> tags */
        if ((strstr(pszBuf, "<WFS_Exception>") &&            /* WFS style */
             (pszStart = strstr(pszBuf, "<Message>")) &&
             (pszEnd = strstr(pszStart, "</Message>")) ) ||
            (strstr(pszBuf, "<ServiceExceptionReport>") &&   /* WMS style */
             (pszStart = strstr(pszBuf, "<ServiceException>")) &&
             (pszEnd = strstr(pszStart, "</ServiceException>")) ))
        {
            pszStart = strchr(pszStart, '>')+1;
            *pszEnd = '\0';
            msSetError(nErrorCode, "Got Remote Server Exception for layer %s: %s",
                       pszFuncName, lp->name?lp->name:"(null)", pszStart);
        }
        else
        {
            msSetError(MS_WFSCONNERR, "Unable to parse Remote Server Exception Message for layer %s.",
                       pszFuncName, lp->name?lp->name:"(null)");
        }

        free(pszBuf);
        fclose(fp);
    }
}

/**********************************************************************
 *                          msOWSBuildURLFilename()
 *
 * Build a unique filename for this URL to use in caching remote server 
 * requests.  Slashes and illegal characters will be turned into '_'
 *
 * Returns a newly allocated buffer that should be freed by the caller or
 * NULL in case of error.
 **********************************************************************/
char *msOWSBuildURLFilename(const char *pszPath, const char *pszURL, 
                            const char *pszExt)
{
    char *pszBuf, *pszPtr;
    int  i;
    size_t nBufLen = 0;


    nBufLen = strlen(pszURL) + strlen(pszExt) +2;
    if (pszPath)
        nBufLen += (strlen(pszPath)+1);
    
    pszBuf = (char*)malloc(nBufLen);
    if (pszBuf == NULL)
    {
        msSetError(MS_MEMERR, NULL, "msOWSBuildURLFilename()");
        return NULL;
    }
    pszBuf[0] = '\0';

    if (pszPath)
    {
#ifdef _WIN32
        if (pszPath[strlen(pszPath) -1] != '/' &&
            pszPath[strlen(pszPath) -1] != '\\')
          snprintf(pszBuf, nBufLen, "%s\\", pszPath);
        else
          snprintf(pszBuf, nBufLen, "%s", pszPath);
#else
        if (pszPath[strlen(pszPath) -1] != '/')
          snprintf(pszBuf, nBufLen, "%s/", pszPath);
        else
          snprintf(pszBuf, nBufLen, "%s", pszPath);
#endif
    }

    pszPtr = pszBuf + strlen(pszBuf);

    for(i=0; pszURL[i] != '\0'; i++)
    {
        if (isalnum(pszURL[i]))
            *pszPtr = pszURL[i];
        else
            *pszPtr = '_';
        pszPtr++;
    }
    
    strlcpy(pszPtr, pszExt, nBufLen);

    return pszBuf;
}

/*
** msOWSGetEPSGProj()
**
** Extract projection code for this layer/map.
**
** First look for a xxx_srs metadata. If not found then look for an EPSG 
** code in projectionObj, and if not found then return NULL.
**
** If bReturnOnlyFirstOne=TRUE and metadata contains multiple EPSG codes
** then only the first one (which is assumed to be the layer's default
** projection) is returned.
*/
const char *msOWSGetEPSGProj(projectionObj *proj, hashTableObj *metadata, const char *namespaces, int bReturnOnlyFirstOne)
{
  static char epsgCode[20] ="";
  char *value;

  /* metadata value should already be in format "EPSG:n" or "AUTO:..." */
  if (metadata && ((value = (char *) msOWSLookupMetadata(metadata, namespaces, "srs")) != NULL)) {
    
    if (!bReturnOnlyFirstOne) return value;

    /* caller requested only first projection code */
    strlcpy(epsgCode, value, 20);

    if ((value=strchr(epsgCode, ' ')) != NULL) *value = '\0';
    
    return epsgCode;
  } else if (proj && proj->numargs > 0 && (value = strstr(proj->args[0], "init=epsg:")) != NULL && strlen(value) < 20) {
    snprintf(epsgCode, sizeof(epsgCode), "EPSG:%s", value+10);
    return epsgCode;
  } else if (proj && proj->numargs > 0 && (value = strstr(proj->args[0], "init=crs:")) != NULL && strlen(value) < 20) {
    snprintf(epsgCode, sizeof(epsgCode), "CRS:%s", value+9);
    return epsgCode;
  } else if (proj && proj->numargs > 0 && (strncasecmp(proj->args[0], "AUTO:", 5) == 0 ||
                                           strncasecmp(proj->args[0], "AUTO2:", 6) == 0)) {
    return proj->args[0];
  }

  return NULL;
}
/*
** msOWSGetProjURN()
**
** Fetch an OGC URN for this layer or map.  Similar to msOWSGetEPSGProj()
** but returns the result in the form "urn:ogc:def:crs:EPSG::27700".
** The returned buffer is dynamically allocated, and must be freed by the
** caller.
*/
char *msOWSGetProjURN(projectionObj *proj, hashTableObj *metadata, const char *namespaces, int bReturnOnlyFirstOne)
{
    char *result;
    char **tokens;
    int numtokens, i;
    size_t bufferSize = 0;
    
    const char *oldStyle = msOWSGetEPSGProj( proj, metadata, namespaces, 
                                             bReturnOnlyFirstOne );

    if( oldStyle == NULL || strncmp(oldStyle,"EPSG:",5) != 0 )
        return NULL;

    result = msStrdup("");

    tokens = msStringSplit(oldStyle, ' ', &numtokens);
    for(i=0; tokens != NULL && i<numtokens; i++)
    {
        char urn[100];

        if( strncmp(tokens[i],"EPSG:",5) == 0 )
            snprintf( urn, sizeof(urn), "urn:ogc:def:crs:EPSG::%s", tokens[i]+5 );
        else if( strcasecmp(tokens[i],"imageCRS") == 0 )
            snprintf( urn, sizeof(urn), "urn:ogc:def:crs:OGC::imageCRS" );
        else if( strncmp(tokens[i],"urn:ogc:def:crs:",16) == 0 ) {
            strlcpy( urn, tokens[i], sizeof(urn));
        }
        else {
            strlcpy( urn, "", sizeof(urn));
        }

        if( strlen(urn) > 0 )
        {
            bufferSize = strlen(result)+strlen(urn)+2;
            result = (char *) realloc(result, bufferSize);
            
            if( strlen(result) > 0 )
                strlcat( result, " ", bufferSize);
            strlcat( result, urn , bufferSize);
        }
        else
        {
            msDebug( "msOWSGetProjURN(): Failed to process SRS '%s', ignored.", 
                     tokens[i] );
        }
    }

    msFreeCharArray(tokens, numtokens);

    if( strlen(result) == 0 )
    {
        msFree( result );
        return NULL;
    }
    else
        return result;
}

/*
** msOWSGetProjURI()
**
** Fetch an OGC URI for this layer or map.  Similar to msOWSGetEPSGProj()
** but returns the result in the form "http://www.opengis.net/def/crs/EPSG/0/27700".
** The returned buffer is dynamically allocated, and must be freed by the
** caller.
*/
char *msOWSGetProjURI(projectionObj *proj, hashTableObj *metadata, const char *namespaces, int bReturnOnlyFirstOne)
{
    char *result;
    char **tokens;
    int numtokens, i;

    const char *oldStyle = msOWSGetEPSGProj( proj, metadata, namespaces,
                                             bReturnOnlyFirstOne );

    if( oldStyle == NULL || !EQUALN(oldStyle,"EPSG:",5) )
        return NULL;

    result = msStrdup("");

    tokens = msStringSplit(oldStyle, ' ', &numtokens);
    for(i=0; tokens != NULL && i<numtokens; i++)
    {
        char urn[100];

        if( strncmp(tokens[i],"EPSG:",5) == 0 )
            snprintf( urn, sizeof(urn), "http://www.opengis.net/def/crs/EPSG/0/%s", tokens[i]+5 );
        else if( strcasecmp(tokens[i],"imageCRS") == 0 )
            snprintf( urn, sizeof(urn), "http://www.opengis.net/def/crs/OGC/0/imageCRS" );
        else if( strncmp(tokens[i],"http://www.opengis.net/def/crs/",16) == 0 )
            snprintf( urn, sizeof(urn), "%s", tokens[i] );
        else
            strlcpy( urn, "", sizeof(urn) );

        if( strlen(urn) > 0 )
        {
            result = (char *) realloc(result,strlen(result)+strlen(urn)+2);

            if( strlen(result) > 0 )
                strcat( result, " " );
            strcat( result, urn );
        }
        else
        {
            msDebug( "msOWSGetProjURI(): Failed to process SRS '%s', ignored.",
                     tokens[i] );
        }
    }

    msFreeCharArray(tokens, numtokens);

    if( strlen(result) == 0 )
    {
        msFree( result );
        return NULL;
    }
    else
        return result;
}


/*
** msOWSGetDimensionInfo()
**
** Extract dimension information from a layer's metadata
**
** Before 4.9, only the time dimension was support. With the addition of
** Web Map Context 1.1.0, we need to support every dimension types. 
** This function get the dimension information from special metadata in
** the layer, but can also return default values for the time dimension.
** 
*/
void msOWSGetDimensionInfo(layerObj *layer, const char *pszDimension, 
                           const char **papszDimUserValue, 
                           const char **papszDimUnits, 
                           const char **papszDimDefault, 
                           const char **papszDimNearValue, 
                           const char **papszDimUnitSymbol, 
                           const char **papszDimMultiValue)
{
    char *pszDimensionItem;
    size_t bufferSize = 0;

    if(pszDimension == NULL || layer == NULL)
        return;

    bufferSize = strlen(pszDimension)+50;
    pszDimensionItem = (char*)malloc(bufferSize);

    /* units (mandatory in map context) */
    if(papszDimUnits != NULL)
    {
        snprintf(pszDimensionItem, bufferSize, "dimension_%s_units",          pszDimension);
        *papszDimUnits = msOWSLookupMetadata(&(layer->metadata), "MO",
                                           pszDimensionItem);
    }
    /* unitSymbol (mandatory in map context) */
    if(papszDimUnitSymbol != NULL)
    {
        snprintf(pszDimensionItem, bufferSize, "dimension_%s_unitsymbol",     pszDimension);
        *papszDimUnitSymbol = msOWSLookupMetadata(&(layer->metadata), "MO", 
                                                  pszDimensionItem);
    }
    /* userValue (mandatory in map context) */
    if(papszDimUserValue != NULL)
    {
        snprintf(pszDimensionItem, bufferSize, "dimension_%s_uservalue",      pszDimension);
        *papszDimUserValue = msOWSLookupMetadata(&(layer->metadata), "MO", 
                                                 pszDimensionItem);
    }
    /* default */
    if(papszDimDefault != NULL)
    {
        snprintf(pszDimensionItem, bufferSize, "dimension_%s_default",        pszDimension);
        *papszDimDefault = msOWSLookupMetadata(&(layer->metadata), "MO",
                                               pszDimensionItem);
    }
    /* multipleValues */
    if(papszDimMultiValue != NULL)
    {
        snprintf(pszDimensionItem, bufferSize, "dimension_%s_multiplevalues", pszDimension);
        *papszDimMultiValue = msOWSLookupMetadata(&(layer->metadata), "MO", 
                                                  pszDimensionItem);
    }
    /* nearestValue */
    if(papszDimNearValue != NULL)
    {
        snprintf(pszDimensionItem, bufferSize, "dimension_%s_nearestvalue",   pszDimension);
        *papszDimNearValue = msOWSLookupMetadata(&(layer->metadata), "MO", 
                                                 pszDimensionItem);
    }

    /* Use default time value if necessary */
    if(strcasecmp(pszDimension, "time") == 0)
    {
        if(papszDimUserValue != NULL && *papszDimUserValue == NULL)
            *papszDimUserValue = msOWSLookupMetadata(&(layer->metadata), 
                                                   "MO", "time");
        if(papszDimDefault != NULL && *papszDimDefault == NULL)
            *papszDimDefault = msOWSLookupMetadata(&(layer->metadata), 
                                                 "MO", "timedefault");
        if(papszDimUnits != NULL && *papszDimUnits == NULL)
            *papszDimUnits = "ISO8601";
        if(papszDimUnitSymbol != NULL && *papszDimUnitSymbol == NULL)
            *papszDimUnitSymbol = "t";
        if(papszDimNearValue != NULL && *papszDimNearValue == NULL)
            *papszDimNearValue = "0";
    }

    free(pszDimensionItem);

    return;
}

/**
 * msOWSNegotiateUpdateSequence()
 *
 * returns the updateSequence value for an OWS
 *
 * @param requested_updatesequence the updatesequence passed by the client
 * @param updatesequence the updatesequence set by the server
 *
 * @return result of comparison (-1, 0, 1)
 * -1: lower / higher OR values not set by client or server
 *  1: higher / lower
 *  0: equal
 */

int msOWSNegotiateUpdateSequence(const char *requested_updatesequence, const char *updatesequence) {
  int i;
  int valtype1 = 1; /* default datatype for updatesequence passed by client */
  int valtype2 = 1; /* default datatype for updatesequence set by server */
  struct tm tm_requested_updatesequence, tm_updatesequence;

  /* if not specified by client, or set by server,
     server responds with latest Capabilities XML */
  if (! requested_updatesequence || ! updatesequence)
    return -1; 

  /* test to see if server value is an integer (1), string (2) or timestamp (3) */
  if (msStringIsInteger(updatesequence) == MS_FAILURE)
    valtype1 = 2;

  if (valtype1 == 2) { /* test if timestamp */
    msTimeInit(&tm_updatesequence);
    if (msParseTime(updatesequence, &tm_updatesequence) == MS_TRUE)
      valtype1 = 3;
    msResetErrorList();
  }

  /* test to see if client value is an integer (1), string (2) or timestamp (3) */
  if (msStringIsInteger(requested_updatesequence) == MS_FAILURE)
    valtype2 = 2;

  if (valtype2 == 2) { /* test if timestamp */
    msTimeInit(&tm_requested_updatesequence);
    if (msParseTime(requested_updatesequence, &tm_requested_updatesequence) == MS_TRUE)
      valtype2 = 3;
    msResetErrorList();
  }

  /* if the datatypes do not match, do not compare, */
  if (valtype1 != valtype2)
    return -1;

  if (valtype1 == 1) { /* integer */
    if (atoi(requested_updatesequence) < atoi(updatesequence))
      return -1;

    if (atoi(requested_updatesequence) > atoi(updatesequence))
      return 1;

    if (atoi(requested_updatesequence) == atoi(updatesequence))
      return 0;
  }

  if (valtype1 == 2) /* string */
    return strcasecmp(requested_updatesequence, updatesequence);

  if (valtype1 == 3) { /* timestamp */
    /* compare timestamps */
    i = msDateCompare(&tm_requested_updatesequence, &tm_updatesequence) +
        msTimeCompare(&tm_requested_updatesequence, &tm_updatesequence);
    return i;
  }

  /* return default -1 */
  return -1;
}


/************************************************************************/
/*                         msOwsIsOutputFormatValid                     */
/*                                                                      */
/*      Utlity function to parse a comma separated list in a            */
/*      metedata object and select and outputformat.                    */
/************************************************************************/
outputFormatObj* msOwsIsOutputFormatValid(mapObj *map, const char *format, 
                                          hashTableObj *metadata, 
                                          const char *namespaces, const char *name)
{
    char **tokens=NULL;
    int i,n;
    outputFormatObj *psFormat = NULL;
    const char * format_list=NULL;
    
    if (map && format && metadata && namespaces && name)
    {
        msApplyDefaultOutputFormats(map);
        format_list = msOWSLookupMetadata(metadata, namespaces, name);
        n = 0;
        if ( format_list)
          tokens = msStringSplit(format_list,  ',', &n);

        if (tokens && n > 0)
        {
            for (i=0; i<n; i++)
            {
                int iFormat = msGetOutputFormatIndex( map, tokens[i]);
                const char *mimetype;
                if( iFormat == -1 )
                    continue;

                mimetype =  map->outputformatlist[iFormat]->mimetype;

                msStringTrim(tokens[i]);
                if (strcasecmp(tokens[i], format) == 0)
                  break;
                if (mimetype && strcasecmp(mimetype, format) == 0)
                  break;
            }
            msFreeCharArray(tokens, n);
            if (i < n)
              psFormat = msSelectOutputFormat( map, format);
        }
    }

    return psFormat;
}

#endif /* USE_WMS_SVR || USE_WFS_SVR  || USE_WCS_SVR */



