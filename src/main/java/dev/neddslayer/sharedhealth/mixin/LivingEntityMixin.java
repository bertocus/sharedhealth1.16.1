package dev.neddslayer.sharedhealth.mixin;

import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_HEALTH;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract boolean isAlive();

    @Inject(method = "heal", at = @At("HEAD"))
    public void healListener(float amount, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player && this.isAlive()) {
            if (player.isSpectator()) return;
            float currentHealth = player.getHealth();
            // Use player.world instead of getScoreboard()
            SharedHealthComponent component = SHARED_HEALTH.get(player.world);
            float knownHealth = component.getHealth();
            if (currentHealth == knownHealth) {
                component.setHealth(knownHealth + amount);
            }
        }
    }
}