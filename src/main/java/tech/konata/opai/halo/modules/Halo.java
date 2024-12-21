package tech.konata.opai.halo.modules;

import org.lwjgl.opengl.GL11;
import tech.konata.opai.halo.HaloExtension;
import tech.konata.opai.halo.rendering.HaloRenderer;
import today.opai.api.OpenAPI;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.events.EventRender2D;
import today.opai.api.events.EventRender3D;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.game.entity.LocalPlayer;
import today.opai.api.interfaces.modules.values.BooleanValue;
import today.opai.api.interfaces.modules.values.ModeValue;
import today.opai.api.interfaces.modules.values.NumberValue;
import today.opai.api.interfaces.render.GLStateManager;

import java.util.List;

/**
 * @author IzumiiKonata
 * @date 12/10/2024
 */
public class Halo extends ExtensionModule implements EventHandler {

    // 你抄袭 = 你死妈
    // 尤其是某个R开头的、使用 rise 5.x 作为 base 的、性能低下并且抄袭 thunderhack 视觉的客户端。

    // 以下代码来自本人开发的 Phosphate 客户端。
    // 禁止任何未经授权的使用。

    public static Halo INSTANCE;

    public ModeValue haloMode;

    public final BooleanValue inFirstPerson = HaloExtension.getAPI().getValueManager().createBoolean("Render In First Person", false);
    public final BooleanValue followHeadModel = HaloExtension.getAPI().getValueManager().createBoolean("Follow Pitch", false);

    public final NumberValue size = HaloExtension.getAPI().getValueManager().createDouble("Size", 1.5, 0.1, 3.0, 0.1);
    public final NumberValue spacing = HaloExtension.getAPI().getValueManager().createDouble("Spacing", 0.65, 0.5, 2.0, 0.05);
    public final NumberValue yawRot = HaloExtension.getAPI().getValueManager().createDouble("X Rot", 0f, -90f, 90f, 1f);
    public final NumberValue pitchRot = HaloExtension.getAPI().getValueManager().createDouble("Y Rot", 0f, -90f, 90f, 1f);

    public Halo() {
        super("Halo", "Renders a halo above the player's head that resembles a character from the game \247oBlue Archive.", EnumModuleCategory.VISUAL);
        super.setEventHandler(this);
        this.initHalos();
        super.addValues(inFirstPerson, followHeadModel, size, spacing, yawRot, pitchRot);
        INSTANCE = this;
    }

    public final HaloRenderer haloRenderer = new HaloRenderer();

    /**
     * 初始化所有光环。
     */
    private void initHalos() {
        this.haloRenderer.getHaloDataMap().clear();
        List<String> haloNames = this.haloRenderer.getHaloNames();
        haloNames.clear();

        this.haloRenderer.addHalo(
                "砂狼 白子",
                new HaloRenderer.HaloData(
                        0.08,
                        "/halo/shiroko/layer0.png",
                        "/halo/shiroko/layer1.png"
                )
        );

        this.haloRenderer.addHalo(
                "黑见 芹香",
                new HaloRenderer.HaloData(
                        0.08,
                        "/halo/serika/layer0.png",
                        "/halo/serika/layer1.png"
                )
        );

        this.haloRenderer.addHalo(
                "小鸟游 星野",
                new HaloRenderer.HaloData(
                        0.06,
                        "/halo/hoshino/layer0.png",
                        "/halo/hoshino/layer1.png",
                        "/halo/hoshino/layer2.png"
                )
        );

        this.haloRenderer.addHalo(
                "Opai Logo",
                new HaloRenderer.HaloData(
        "/assets/minecraft/opai/image/logo2.png"
                )
        );

        this.haloRenderer.setCurrentHalo(this.haloRenderer.getHaloDataMap().get(haloNames.get(0)));

        this.haloMode = HaloExtension.getAPI().getValueManager().createModes("Style", haloNames.get(0), haloNames.toArray(new String[0]));

        this.getValues().removeIf(s -> s instanceof ModeValue);
        this.getValues().add(this.haloMode);
    }

    @Override
    public void onRender3D(EventRender3D event) {

        float height = 1.4f;

        OpenAPI api = HaloExtension.getAPI();

        if (api.getOptions().getThirdPersonViewState() == 0 && !this.inFirstPerson.getValue())
            return;

        GLStateManager glStateManager = api.getGLStateManager();
        LocalPlayer lp = api.getLocalPlayer();

        glStateManager.pushMatrix();

        glStateManager.translate(0, height, 0);

        glStateManager.rotate(lp.getRotation().getYaw(), 0, -1, 0);

        if (this.followHeadModel.getValue()) {
            glStateManager.rotate(lp.getRotation().getPitch(), 1, 0, 0);
        }

        glStateManager.rotate(this.yawRot.getValue().floatValue(), 0, 0, 1);
        glStateManager.rotate(this.pitchRot.getValue().floatValue(), 1, 0, 0);

        glStateManager.translate(0, spacing.getValue(), 0);

        glStateManager.rotate(90, 1, 0, 0);

        boolean currentState = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        // 关闭 cull face, 防止贴图背面不可见
        if (currentState)
            glStateManager.disableCull();

        HaloRenderer.HaloData current = haloRenderer.getHaloDataMap().get(haloMode.getValue());
        if (current != haloRenderer.getCurrentHalo())
            haloRenderer.setCurrentHalo(current);

        this.haloRenderer.render();

        glStateManager.popMatrix();

        if (currentState)
            glStateManager.enableCull();

    }

}
