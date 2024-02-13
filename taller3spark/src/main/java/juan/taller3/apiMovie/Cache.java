package juan.taller3.apiMovie;

import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.*;

/**
 * Cache class implemented to simulated cache
 * 
 * @author juan cepeda
 */
public class Cache {
    private ConcurrentHashMap<String, JsonObject> movieCache;
    private static Cache cache = null;

    /**
     * Cache's class constructor
     * 
     * @param apf
     */
    public Cache() {
        movieCache = new ConcurrentHashMap<String, JsonObject>();
    }

    /**
     * method that returns the instance of Cache class
     * 
     * @return the current instance of cache
     */
    public static Cache getInstance() {
        if (cache == null) {
            cache = new Cache();
        }

        return cache;
    }

    /**
     * method that returns a result if this one exist inside cache
     * 
     * @param name name of the movie to search
     * @return All data of the movie
     */
    public JsonObject getMovie(String name) {
        return movieCache.get(name);
    }

    /**
     * method that returns if a movie is inside cache
     * 
     * @return
     */
    public boolean movieInCache(String name) {
        return movieCache.containsKey(name);
    }

    /**
     * 
     * 
     * @param name
     * @param movieInfo
     */
    public void addMovieToCache(String name, JsonObject movieInfo) {
        movieCache.putIfAbsent(name, movieInfo);
    }
}
