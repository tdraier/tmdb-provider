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
package org.jahia.modules.provider.tmdb.old.node.handler;

import org.jahia.api.Constants;

import java.util.Set;

/**
 * @author Jerome Blanchard
 */
//@NodeMapping(pathPattern = "^/movies/\\d{4}/\\d{2}/\\d+/(cast_|crew_)\\d+$", idPattern = "^credits-\\d+-(cast_|crew_)\\d+$",
//        supportedNodeType = {Naming.NodeType.CREW, Naming.NodeType.CAST}, hasLazyProperties = false)
public class CreditsNodeHandler {

    /*
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditsNodeHandler.class);
    public static final String PATH_LABEL = "credits";
    public static final String ID_PREFIX = "credits-";
    public static final String CAST = "cast_";
    public static final String CREW = "crew_";

    public CreditsNodeHandler() {
    }

    @Override public List<String> listChildren(String path) {
        return Collections.emptyList();
    }

    @Override public List<ExternalData> listChildrenNodes(String path) {
        return Collections.emptyList();
    }

    @Override public ExternalData getData(String identifier) {
        String cleanId = identifier.substring(ID_PREFIX.length());
        String creditsId = StringUtils.substringAfter(cleanId, "-");
        String movieId = StringUtils.substringBefore(cleanId, "-");

        if (getCache().get(Naming.Cache.MOVIE_CREDITS_CACHE_PREFIX + identifier) != null) {
            return (ExternalData) getCache().get(Naming.Cache.MOVIE_CREDITS_CACHE_PREFIX + identifier).getObjectValue();
        } else {
            try {
                MovieDb movie = getApiClient().getMovies().getDetails(Integer.parseInt(movieId), "en");
                Credits credits = getApiClient().getMovies().getCredits(Integer.parseInt(movieId), "en");
                String year = movie.getReleaseDate().split("-")[0];
                String month = movie.getReleaseDate().split("-")[1];
                String path = new PathBuilder(MoviesNodeHandler.PATH_LABEL).append(year).append(month).append(movieId).append(creditsId).build();
                String baseUrl = getConfiguration().getImageConfig().getBaseUrl();
                if (creditsId.startsWith(CREW)) {
                    String pid = creditsId.substring(CREW.length());
                    Crew crew = credits.getCrew().stream().filter(c -> c.getId() == Integer.parseInt(pid)).findFirst().orElseThrow();
                    Map<String, String[]> properties = new HashMap<>();
                    ExternalData data = new ExternalData(identifier, path, Naming.NodeType.CREW, properties);
                    if (StringUtils.isNotEmpty(crew.getDepartment())) {
                        properties.put("department", new String[] { crew.getDepartment() });
                    }
                    if (StringUtils.isNotEmpty(crew.getJob())) {
                        properties.put("job", new String[] { crew.getJob() });
                    }
                    properties.put("person", new String[] { PersonsNodeHandler.ID_PREFIX + crew.getId() });
                    if (StringUtils.isNotEmpty(crew.getName())) {
                        properties.put("name", new String[] { crew.getName() });
                    }
                    if (StringUtils.isNotEmpty(crew.getProfilePath())) {
                        properties.put("profile", new String[] {
                                baseUrl + getConfiguration().getImageConfig().getProfileSizes().get(1) + crew.getProfilePath() });
                    }
                    getCache().put(new Element(Naming.Cache.MOVIE_CREDITS_CACHE_PREFIX + identifier, data));
                    return data;
                }
                if (creditsId.startsWith(CAST)) {
                    String pid = creditsId.substring(CAST.length());
                    Cast cast = credits.getCast().stream().filter(c -> c.getId() == Integer.parseInt(pid)).findFirst().orElseThrow();
                    Map<String, String[]> properties = new HashMap<>();
                    ExternalData data = new ExternalData(identifier, path, Naming.NodeType.CAST, properties);
                    if (StringUtils.isNotEmpty(cast.getCharacter())) {
                        properties.put("character", new String[] { cast.getCharacter() });
                    }
                    properties.put("order", new String[] { Integer.toString(cast.getOrder()) });
                    properties.put("cast_id", new String[] { Integer.toString(cast.getCastId()) });
                    properties.put("id", new String[] { Integer.toString(cast.getId()) });
                    if (StringUtils.isNotEmpty(cast.getName())) {
                        properties.put("name", new String[] { cast.getName() });
                    }
                    if (StringUtils.isNotEmpty(cast.getProfilePath())) {
                        properties.put("profile", new String[] {
                                baseUrl + getConfiguration().getImageConfig().getProfileSizes().get(1) + cast.getProfilePath() });
                    }
                    getCache().put(new Element(Naming.Cache.MOVIE_CREDITS_CACHE_PREFIX + identifier, data));
                    return data;
                }
                return null;
            } catch (TmdbException e) {
                LOGGER.warn("Error while getting movie credits for identifier: " + identifier, e);
                return null;
            }
        }
    }

    @Override public List<String> search(String nodeType, ExternalQuery query) throws RepositoryException {
        Map<String, Value> m = QueryHelper.getSimpleOrConstraints(query.getConstraint());
        String lang = QueryHelper.getLanguage(query.getConstraint());
        List<String> results = new ArrayList<>();
        if ( m.containsKey("id")) {
            String id = m.get("id").getString();
            try {
            MovieCredits credits;
                if (getCache().get(Naming.Cache.MOVIE_CREDITS_QUERY_CACHE_KEY_PREFIX + id) != null) {
                    credits = (MovieCredits) getCache().get(Naming.Cache.MOVIE_CREDITS_QUERY_CACHE_KEY_PREFIX + id).getObjectValue();
                } else {
                    credits = getApiClient().getPeople().getMovieCredits(Integer.parseInt(id), lang);
                    getCache().put(new Element(Naming.Cache.MOVIE_CREDITS_QUERY_CACHE_KEY_PREFIX + id, credits));
                }
                if (Naming.NodeType.CAST.equals(nodeType)) {
                    credits.getCrew().stream()
                            .filter(c -> StringUtils.isNotEmpty(c.getReleaseDate()))
                            .map(c -> buildPath(Integer.toString(c.getId()), CAST.concat(id), c.getReleaseDate()))
                            .forEach(results::add);
                }
                if (Naming.NodeType.CREW.equals(nodeType) && m.containsKey("id")) {
                    credits.getCrew().stream()
                            .filter(c -> StringUtils.isNotEmpty(c.getReleaseDate()))
                            .map(c -> buildPath(Integer.toString(c.getId()), CREW.concat(id), c.getReleaseDate()))
                            .forEach(results::add);
                }
            } catch (TmdbException e) {
                throw new RepositoryException("Error while searching credits", e);
            }
        }
        return results;
    }

    @Override public String getIdFromPath(String path) {
        return ID_PREFIX.concat(PathHelper.getParent(path)).concat("-").concat(PathHelper.getLeaf(path));
    }

    @Override public String getPathLabel() {
        return PATH_LABEL;
    }

    private String buildPath(String mid, String cid, String releaseDate) {
        String year = releaseDate.split("-")[0];
        String month = releaseDate.split("-")[1];
        return new PathBuilder(MoviesNodeHandler.PATH_LABEL).append(year).append(month).append(mid).append(cid).build();
    }

     */
}
