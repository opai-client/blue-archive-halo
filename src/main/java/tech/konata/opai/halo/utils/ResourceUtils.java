package tech.konata.opai.halo.utils;

import lombok.experimental.UtilityClass;

import java.io.InputStream;

/**
 * @author IzumiiKonata
 * @date 2024/12/10 18:18
 */
@UtilityClass
public class ResourceUtils {

    public InputStream getResourceAsStream(String resourceName) {
        return ResourceUtils.class.getResourceAsStream(resourceName);
    }

}
