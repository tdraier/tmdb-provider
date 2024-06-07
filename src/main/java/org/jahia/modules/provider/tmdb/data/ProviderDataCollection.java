/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2024 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms &amp; Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.jahia.modules.provider.tmdb.data;

/**
 * Item Mapper goal is to retrieve items and properties from an external collection and map them to Jahia structure : ExternalData.
 * The information used to retrieve the items an identifier.
 * An idPattern is used to identify the mapper that can handle an identifier.
 * It is used by NodeHandlers to retrieve items from the external collection.
 * It is supposed to have an efficient cache system depending on the external data retrieval
 *
 * @author Jerome Blanchard
 */
public interface ProviderDataCollection {
    
    ProviderData getData(String identifier);

}