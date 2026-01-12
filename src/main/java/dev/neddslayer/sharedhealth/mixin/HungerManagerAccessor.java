package dev.neddslayer.sharedhealth.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public interface HungerManagerAccessor {
    @Accessor("foodLevel")
    int getLevel();

    @Accessor("foodLevel")
    void setLevel(int foodLevel);

    @Accessor("foodStarvationTimer")
    int getTimer();

    @Accessor("foodStarvationTimer")
    void setTimer(int foodStarvationTimer);

    @Accessor("foodSaturationLevel")
    float getSat();

    @Accessor("foodSaturationLevel")
    void setSat(float saturationLevel);

    @Accessor("exhaustion")
    float getExh();

    @Accessor("exhaustion")
    void setExh(float exhaustion);
}