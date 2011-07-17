/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.portlet.container.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.jasig.portal.portlet.om.IPortletWindow;
import org.jasig.portal.portlet.om.IPortletWindowId;
import org.jasig.portal.url.IPortalRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * Portlet property manager that just translates properties to/from the portal request attributes
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
@Service
public class PropertyToAttributePropertiesManager extends BaseRequestPropertiesManager {
	private IPortalRequestUtils portalRequestUtils;
	private BiMap<String, String> propertyToAttributeMappings = ImmutableBiMap.of();
	private BiMap<String, String> attributeToPropertyMappings = ImmutableBiMap.of();
	private Set<String> nonNamespacedProperties = Collections.emptySet();

	/**
	 * Map of portlet property names to attribute names, if the value is null the key will be used for both
	 * the property and attribute name 
	 */
	@Resource(name="portletPropertyToAttributeMappings")
	public void setPropertyMappings(Map<String, String> propertyMappings) {
		this.propertyToAttributeMappings = ImmutableBiMap.copyOf(propertyMappings);
		this.attributeToPropertyMappings = this.propertyToAttributeMappings.inverse();
	}

	/**
	 * Properties that should not be namespaced with the portlet's windowId when stored
	 * as request attributes
	 */
	@Resource(name="nonNamespacedPortletProperties")
	public void setNonNamespacedProperties(Set<String> nonNamespacedProperties) {
		this.nonNamespacedProperties = ImmutableSet.copyOf(nonNamespacedProperties);
	}

	@Autowired
	public void setPortalRequestUtils(IPortalRequestUtils portalRequestUtils) {
		this.portalRequestUtils = portalRequestUtils;
	}

	/* (non-Javadoc)
	 * @see org.jasig.portal.portlet.container.properties.BaseRequestPropertiesManager#addResponseProperty(javax.servlet.http.HttpServletRequest, org.jasig.portal.portlet.om.IPortletWindow, java.lang.String, java.lang.String)
	 */
	@Override
	public void addResponseProperty(HttpServletRequest portletRequest, IPortletWindow portletWindow, String property, String value) {
		if (this.propertyToAttributeMappings.isEmpty() && this.nonNamespacedProperties.isEmpty()) {
			return;
		}
		
		final HttpServletRequest portalRequest = this.portalRequestUtils.getOriginalPortalRequest(portletRequest);
		
		final String attributeName = getAttributeName(portletWindow, property);
		
		final Object existingValue = portalRequest.getAttribute(attributeName);
		if (!(existingValue instanceof List)) {
			this.logger.warn("Attribute {} for property {} exists but is NOT a List, it will be replaced", attributeName, property);
			this.setResponseProperty(portletRequest, portletWindow, property, value);
			return;
		}
		
		logger.debug("Adding property {} as attribute {}", property, attributeName);
		
		final List<String> values = (List<String>)existingValue;
		values.add(value);
		portalRequest.setAttribute(attributeName, values);
	}

	/* (non-Javadoc)
	 * @see org.jasig.portal.portlet.container.properties.BaseRequestPropertiesManager#setResponseProperty(javax.servlet.http.HttpServletRequest, org.jasig.portal.portlet.om.IPortletWindow, java.lang.String, java.lang.String)
	 */
	@Override
	public void setResponseProperty(HttpServletRequest portletRequest, IPortletWindow portletWindow, String property, String value) {
		if (this.propertyToAttributeMappings.isEmpty() && this.nonNamespacedProperties.isEmpty()) {
			return;
		}
		
		final HttpServletRequest portalRequest = this.portalRequestUtils.getOriginalPortalRequest(portletRequest);
		
		final String attributeName = getAttributeName(portletWindow, property);
		
		logger.debug("Setting property {} as attribute {}", property, attributeName);
		
		final List<String> values = new LinkedList<String>();
		values.add(value);
		portalRequest.setAttribute(attributeName, values);
	}

	protected String getAttributeName(IPortletWindow portletWindow, String property) {
		final String mappedAttributeName = this.propertyToAttributeMappings.get(property);
		final String attributeName;
		if (mappedAttributeName == null) {
			attributeName = property;
		}
		else {
			attributeName = mappedAttributeName;
		}
		
		if (this.nonNamespacedProperties.contains(property)){
			return attributeName;
		}

		final IPortletWindowId portletWindowId = portletWindow.getPortletWindowId();
		return portletWindowId.getStringId() + attributeName;
	}

	/* (non-Javadoc)
	 * @see org.jasig.portal.portlet.container.properties.BaseRequestPropertiesManager#getRequestProperties(javax.servlet.http.HttpServletRequest, org.jasig.portal.portlet.om.IPortletWindow)
	 */
	@Override
	public Map<String, String[]> getRequestProperties(HttpServletRequest portletRequest, IPortletWindow portletWindow) {
		if (this.propertyToAttributeMappings.isEmpty() && this.nonNamespacedProperties.isEmpty()) {
			return Collections.emptyMap();
		}
		
		final HttpServletRequest portalRequest = this.portalRequestUtils.getOriginalPortalRequest(portletRequest);
		final String windowIdStr = portletWindow.getPortletWindowId().getStringId();
		
		final Builder<String, String[]> properties = ImmutableMap.builder();
		for (final Enumeration<String> attributeNames = portalRequest.getAttributeNames(); attributeNames.hasMoreElements();) {
			final String fullAttributeName = attributeNames.nextElement();
			final String propertyName = getPropertyName(windowIdStr, fullAttributeName);
			if (propertyName == null) {
				continue;
			}
			
			logger.debug("Found portal request attribute {} returning as property {}", fullAttributeName, propertyName);

			final Object value = portalRequest.getAttribute(fullAttributeName);
			final String[] values = convertValue(value);
			
			properties.put(propertyName, values);
		}
		
		return properties.build();
	}

	/**
	 * Convert a request attribute name to a portlet property name
	 */
	private String getPropertyName(final String windowIdStr, final String fullAttributeName) {
		final String attributeName;
		if (this.nonNamespacedProperties.contains(fullAttributeName)) {
			attributeName = fullAttributeName;
		}
		else if (fullAttributeName.startsWith(windowIdStr)) { 
			attributeName = fullAttributeName.substring(windowIdStr.length());
		}
		else {
			return null;
		}
			
		final String mappedPropertyName = this.attributeToPropertyMappings.get(attributeName);
		if (mappedPropertyName == null) {
			logger.warn("Attribute {} found that matches the portlet window ID but it is not listed in the propertyMappings or nonNamespacedProperties and will not be returned to the portlet", attributeName);
			return null;
		}
		
		return mappedPropertyName;
	}
	
	protected String[] convertValue(Object value) {
		if (value == null) {
			return new String[] { null };
		}
		else if (value instanceof Collection) {
			final Collection<?> valuesCol = (Collection<?>)value;
			final String[] values = new String[valuesCol.size()];
			int i = 0;
			for (final Object obj : valuesCol) {
				values[i++] = String.valueOf(obj);
			}
			return values;
		}
		else {
			return new String[] { String.valueOf(value) };
		}
	}
}
