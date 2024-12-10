package tech.konata.opai.halo.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import tech.konata.opai.halo.HaloExtension;
import today.opai.api.interfaces.render.GLStateManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author IzumiiKonata
 * @since 2024/12/10 18:44
 */
@UtilityClass
public class TextureUtils {

    @SneakyThrows
    public int createImage(InputStream stream, boolean linear) {

        BufferedImage img = ImageIO.read(stream);

        int[] dynamicTextureData = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), dynamicTextureData, 0, img.getWidth());

        int texId = GL11.glGenTextures();

        TextureUtils.allocateTexture(texId, img.getWidth(), img.getHeight());

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, linear ? GL11.GL_LINEAR : GL11.GL_NEAREST);

        int rowStride = (img.getWidth() * 4 + 3) & ~3; // align: 4 bytes
        int padding = rowStride * img.getHeight() - (img.getWidth() * img.getHeight() * 4);

        IntBuffer buffer = BufferUtils.createByteBuffer((dynamicTextureData.length + padding) << 2).asIntBuffer();

        buffer.clear();
        buffer.put(dynamicTextureData, 0, dynamicTextureData.length);
        buffer.flip();

        GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, img.getWidth(), img.getHeight(), GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, buffer);

        System.out.println("Image ID: " + texId);

        return texId;
    }

    private void allocateTexture(int textureId, int width, int height) {
        HaloExtension.getAPI().getGLStateManager().bindTexture(textureId);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, (float) 0);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) null);

    }

}
