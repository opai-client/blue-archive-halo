package tech.konata.opai.halo;

import lombok.Getter;
import tech.konata.opai.halo.modules.Halo;
import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;

@ExtensionInfo(name = "Halo", author = "IzumiiKonata",version = "1.0")
public class HaloExtension extends Extension {

    @Getter
    private static OpenAPI API;

    @Override
    public void initialize(OpenAPI api) {
        HaloExtension.API = api;

        api.registerFeature(new Halo());
    }

}
