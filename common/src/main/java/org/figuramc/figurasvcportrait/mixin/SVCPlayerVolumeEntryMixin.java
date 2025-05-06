package org.figuramc.figurasvcportrait.mixin;

import java.util.function.Function;

import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.Badges;
import org.figuramc.figura.config.Configs;
import org.figuramc.figura.lua.api.nameplate.NameplateCustomization;
import org.figuramc.figura.permissions.Permissions;
import org.figuramc.figura.utils.TextUtils;
import org.figuramc.figura.utils.ui.UIHelper;
import org.figuramc.figurasvcportrait.FiguraSVCIconCompat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import de.maxhenkel.voicechat.gui.volume.PlayerVolumeEntry;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Pseudo
@Mixin(value = PlayerVolumeEntry.class, remap = true)
public class SVCPlayerVolumeEntryMixin {
    @Nullable
    @Shadow
    @Final
    protected PlayerState state;

    @WrapWithCondition(require = 0, method = "renderElement(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIZFIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIFFIIII)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lde/maxhenkel/voicechat/gui/GameProfileUtils;getSkin(Ljava/util/UUID;)Lnet/minecraft/client/resources/PlayerSkin;"), to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V")))
    private boolean figuraSvcPortrait(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, @Local(ordinal = 7) int skinX, @Local(ordinal = 8) int skinY) {
        return FiguraSVCIconCompat.shouldRenderVanillaIcon(guiGraphics, state, skinX, skinY, 24, 48);
    }

    // >1.21.1
    @WrapWithCondition(require = 0, method = "renderElement(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIZFIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Ljava/util/function/Function;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIII)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lde/maxhenkel/voicechat/gui/GameProfileUtils;getSkin(Ljava/util/UUID;)Lnet/minecraft/client/resources/PlayerSkin;"), to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V")))
    private boolean figuraSvcPortrait2(GuiGraphics guiGraphics, Function<ResourceLocation, RenderType> function, ResourceLocation id, int i, int j, float f, float g, int k, int l, int m, int n, int o, int p, @Local(ordinal = 7) int skinX, @Local(ordinal = 8) int skinY) {
        return FiguraSVCIconCompat.shouldRenderVanillaIcon(guiGraphics, state, skinX, skinY, 24, 48);
    }

    @WrapWithCondition(method = "renderElement(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIZFIIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private boolean figuraName(GuiGraphics guiGraphics, Font renderer, @Nullable String text, int x, int y, int color, boolean shadowed, @Local(ordinal = 9) int textX, @Local(ordinal = 10) int textY) {
        Component playerName = Component.literal(state.getName());

        int config = Configs.CHAT_NAMEPLATE.value;

        // apply customization
        Avatar avatar = AvatarManager.getAvatarForPlayer(state.getUuid());
        NameplateCustomization custom = avatar == null || avatar.luaRuntime == null ? null : avatar.luaRuntime.nameplate.CHAT;

        if (custom == null && config < 2) // no customization and no possible badges to append
            return true;

        Component replacement = custom != null && custom.getJson() != null && avatar.permissions.get(Permissions.NAMEPLATE_EDIT) == 1 ? TextUtils.replaceInText(custom.getJson().copy(), "\n|\\\\n", " ") : playerName;

        // name
        replacement = TextUtils.replaceInText(replacement, "\\$\\{name\\}", playerName);

        // badges
        Component emptyReplacement = Badges.appendBadges(replacement, state.getUuid(), config > 1);

        // trim
        emptyReplacement = TextUtils.trim(emptyReplacement);

        UIHelper.renderScrollingText(guiGraphics, emptyReplacement, x, y, 80, 0xFFFFFF);

        return false;
    }
}
