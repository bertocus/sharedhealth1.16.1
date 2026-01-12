package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;foodLevel:I", ordinal = 1, shift = At.Shift.BEFORE))
    public void decreaseHunger(PlayerEntity player, CallbackInfo ci) {
        if (!player.world.isClient) {
            HungerManagerAccessor acc = (HungerManagerAccessor) this;
            SharedHungerComponent component = SHARED_HUNGER.get(player.world);
            if (acc.getLevel() == component.getHunger()) {
                component.setHunger(Math.max(acc.getLevel() - 1, 0));
            }
        }
    }

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;foodSaturationLevel:F", ordinal = 0, shift = At.Shift.BEFORE))
    public void decreaseSaturation(PlayerEntity player, CallbackInfo ci) {
        if (!player.world.isClient) {
            HungerManagerAccessor acc = (HungerManagerAccessor) this;
            SharedSaturationComponent component = SHARED_SATURATION.get(player.world);
            if (acc.getSat() == component.getSaturation()) {
                component.setSaturation(Math.max(acc.getSat() - 1.0f, 0.0f));
            }
        }
    }

    @Inject(method = "update", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/HungerManager;exhaustion:F", ordinal = 1))
    public void decreaseExhaustion(PlayerEntity player, CallbackInfo ci) {
        if (!player.world.isClient) {
            HungerManagerAccessor acc = (HungerManagerAccessor) this;
            SharedExhaustionComponent component = SHARED_EXHAUSTION.get(player.world);
            if (acc.getExh() == component.getExhaustion()) {
                component.setExhaustion(Math.max(acc.getExh() - 4.0f, 0.0f));
            }
        }
    }

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"))
    public void hookAddExhaustion(PlayerEntity player, CallbackInfo ci) {
        if (!player.world.isClient) {
            HungerManagerAccessor acc = (HungerManagerAccessor) this;
            SharedExhaustionComponent exComp = SHARED_EXHAUSTION.get(player.world);
            SharedSaturationComponent satComp = SHARED_SATURATION.get(player.world);

            if (acc.getExh() == exComp.getExhaustion()) {
                if (acc.getTimer() < 80 && acc.getTimer() >= 10) {
                    float f = Math.min(satComp.getSaturation(), 6.0F);
                    exComp.setExhaustion(Math.max(exComp.getExhaustion() + f, 0.0f));
                } else {
                    exComp.setExhaustion(Math.max(exComp.getExhaustion() + 6.0f, 0.0f));
                }
            }
        }
    }
}