package tech.konata.opai.halo.rendering;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;
import tech.konata.opai.halo.HaloExtension;
import tech.konata.opai.halo.modules.Halo;
import tech.konata.opai.halo.rendering.animation.Animation;
import tech.konata.opai.halo.rendering.animation.Easing;
import tech.konata.opai.halo.utils.ResourceUtils;
import tech.konata.opai.halo.utils.TextureUtils;
import today.opai.api.interfaces.render.GLStateManager;
import today.opai.api.interfaces.render.Image;

import java.io.InputStream;
import java.time.Duration;
import java.util.*;

/**
 * @author IzumiiKonata
 * @since 2024/11/22 17:44
 */
public class HaloRenderer {

    @Getter
    private final Map<String, HaloData> haloDataMap = new HashMap<>();

    @Getter
    private final List<String> haloNames = new ArrayList<>();

    @Getter
    @Setter
    private HaloData currentHalo = null;

    public HaloRenderer() {

    }

    final Animation floatingAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, Duration.ofSeconds(2));
    boolean animationBackwards = false;

    public void render() {
        double animationHeight = 0.1;
        floatingAnimation.run(animationBackwards ? 0 : animationHeight);

        if (floatingAnimation.getValue() == 0) animationBackwards = false;
        if (floatingAnimation.getValue() == animationHeight) animationBackwards = true;

        if (this.currentHalo == null) {

            if (this.haloDataMap.isEmpty())
                return;

            this.currentHalo = this.haloDataMap.values().iterator().next();

        }

        GLStateManager glStateManager = HaloExtension.getAPI().getGLStateManager();

        glStateManager.rotate(-90, 1, 0, 0);
        glStateManager.translate(0, floatingAnimation.getValue(), 0);
        glStateManager.rotate(90, 1, 0, 0);

        float imgWidth = Halo.INSTANCE.size.getValue().floatValue(), imgHeight = Halo.INSTANCE.size.getValue().floatValue();

        boolean maskEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

        // 不更新 depth buffer 内容
        if (maskEnabled)
            glStateManager.depthMask(false);

        currentHalo.render(glStateManager, imgWidth, imgHeight);

        if (maskEnabled)
            glStateManager.depthMask(true);

    }

    public void addHalo(String name, HaloData data) {
        haloDataMap.put(name, data);
        this.haloNames.add(name);
    }

    public static class HaloData {

        public final boolean layered;

        private final List<Integer> textureIDs = new ArrayList<>();

        // for layered
        public double spacing = 0.0;

        // not layered
        public HaloData(String textureLocation) {
            this.textureIDs.add(TextureUtils.createImage(ResourceUtils.getResourceAsStream(textureLocation), true));
            this.layered = false;
        }

        // layered
        public HaloData(double spacing, String... textureLocations) {
            this.spacing = spacing;
            this.layered = true;

            for (String location : textureLocations) {
                this.textureIDs.add(TextureUtils.createImage(ResourceUtils.getResourceAsStream(location), true));
            }

        }

        private void renderImage(float x, float y, float width, float height) {

            // 我想要 WorldRenderer

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(0, 1);
            GL11.glVertex2f(x, y + height);
            GL11.glTexCoord2f(1, 1);
            GL11.glVertex2f(x + width, y + height);
            GL11.glTexCoord2f(1, 0);
            GL11.glVertex2f(x + width, y);
            GL11.glTexCoord2f(0, 0);
            GL11.glVertex2f(x, y);
            GL11.glEnd();

        }

        public void render(GLStateManager glStateManager, float imgWidth, float imgHeight) {

            glStateManager.enableTexture2D();
            glStateManager.enableBlend();
            glStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            glStateManager.color(1, 1, 1, 1);

            for (int texId : this.textureIDs) {
                glStateManager.bindTexture(texId);
                this.renderImage(-imgWidth * 0.5f, -imgHeight * 0.5f, imgWidth, imgHeight);

                glStateManager.rotate(-90, 1, 0, 0);
                glStateManager.translate(0, this.spacing * (Halo.INSTANCE.size.getValue() / 1.5), 0);
                glStateManager.rotate(90, 1, 0, 0);
            }

        }

    }

}
