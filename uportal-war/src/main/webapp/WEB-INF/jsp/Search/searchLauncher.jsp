<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<c:set var="n"><portlet:namespace/></c:set>

<!-- The action URLs, including the ajaxSearchUrl, should have windowState=maximized or uPortal will waste time doing an
     HTTP redirect to set it to maximized. -->
<portlet:actionURL var="launchUrl" windowState="maximized"></portlet:actionURL>
<portlet:actionURL var="ajaxSearchUrl" windowState="maximized"/>
<portlet:resourceURL var="ajaxResults" id="retrieveSearchJSONResults"/>

<div id="webSearchContainer" class="fl-widget">
    <div class="fl-widget-inner">
      <div class="fl-widget-content">
        <c:set var="searchLabel"><spring:message code="search"/></c:set>
        <form class="form-inline form-search" role="form" method="post" action="${searchLaunchUrl}" id="webSearchForm">
          <div class="input-group">
            <input id="${n}webSearchInput"  class="searchInput input-large search-query form-control" value="" name="query" type="text" placeholder="Enter search terms"/>
            <span class="input-group-btn">
              <button id="webSearchSubmit" type="submit" name="submit" class="btn btn-default" value="${searchLabel}">
                <span>${searchLabel}</span><i class="fa fa-search"></i></button>
            </span>
          </div>
            <input class="autocompleteUrl" name="autocompleteUrl" type="hidden" value="${autocompleteUrl}"/>
        </form>
      </div>
    </div>
</div>

<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.10.2/jquery-1.10.2.min.js"/>"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jqueryui/1.10.3/jquery-ui-1.10.3.min.js"/>"></script>

<%@ include file="autosuggest_handler.jsp"%>

<script language="javascript" type="text/javascript"><rs:compressJs>
/*
 * Switch jQuery to extreme noConflict mode, keeping a reference to it in the searchjQ["${n}"] namespace
 */
var searchjQ = searchjQ || {};
searchjQ["${n}"] = searchjQ["${n}"] || {};
searchjQ["${n}"].jQuery = jQuery.noConflict(true);

searchjQ["${n}"].jQuery(document).ready(function() {
    initSearchAuto(searchjQ["${n}"].jQuery, "#${n}webSearchInput");
});

</rs:compressJs></script>
