package com.example.weather.persistence;

import com.example.weather.model.Location;
import com.thoughtworks.xstream.XStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists favourite locations to an XML file via XStream 1.4.5
 * (intentionally vulnerable: CVE-2013-7285 and the 2020/2021 deserialization CVE batches).
 *
 * <p>XStream 1.4.5 predates the JPMS and performs deep reflective access at construction time.
 * On JDK 17+ that requires {@code --add-opens} flags (see README); without them XStream throws
 * {@link java.lang.reflect.InaccessibleObjectException}. To keep the application usable regardless, the XStream
 * instance is created lazily and any failure degrades gracefully (favourites disabled) instead
 * of crashing the UI. The vulnerable dependency and its code path remain real and invoked.
 */
public class FavoritesStore {

    private static final Logger LOG = LogManager.getLogger(FavoritesStore.class);
    private static final File FILE = new File("data/favorites.xml");

    private XStream xstream;

    /** Lazily build XStream so a JPMS reflection failure does not break app startup. */
    private XStream xstream() {
        if (xstream == null) {
            xstream = new XStream();
            xstream.alias("location", Location.class);
            xstream.alias("favorites", ArrayList.class);
        }
        return xstream;
    }

    @SuppressWarnings("unchecked")
    public List<Location> load() {
        if (!FILE.exists()) {
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(FILE)) {
            // fromXML deserializes untrusted XML -> the XStream RCE code path.
            Object obj = xstream().fromXML(reader);
            if (obj instanceof List) {
                return (List<Location>) obj;
            }
        } catch (Throwable t) {
            LOG.error("Failed to load favorites (XStream); favorites disabled", t);
        }
        return new ArrayList<>();
    }

    public void save(List<Location> favorites) {
        File dir = FILE.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            LOG.warn("Could not create directory {}", dir);
        }
        try (FileWriter writer = new FileWriter(FILE)) {
            xstream().toXML(favorites, writer);
            LOG.info("Saved {} favorite(s)", favorites.size());
        } catch (Throwable t) {
            LOG.error("Failed to save favorites (XStream); favorites disabled", t);
        }
    }
}
