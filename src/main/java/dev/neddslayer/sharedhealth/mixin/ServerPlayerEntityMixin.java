package dev.neddslayer.sharedhealth.mixin;

import com.mojang.authlib.GameProfile;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.SHARED_HEALTH;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, GameProfile gameProfile) {
        super(world, pos, gameProfile);
    }

    @Shadow
    public abstract ServerWorld getServerWorld();

    @Inject(method = "damage", at = @At("RETURN"))
    public void damageListener(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        // cir.getReturnValue() check ensures damage wasn't blocked (by a shield or invulnerability)
        // source != DamageSource.OUT_OF_WORLD ensures we don't log the 'sync' damage, preventing duplicates.
        if (cir.getReturnValue() && source != DamageSource.OUT_OF_WORLD) {

            float currentHealth = this.getHealth();
            SharedHealthComponent component = SHARED_HEALTH.get(this.world);

            // Sync the health (including the 0.0 for death)
            if (currentHealth != component.getHealth()) {
                component.setHealth(currentHealth);
            }

            if (!this.world.isClient) {
                String playerName = this.getGameProfile().getName();

                // Logic to get specific name for the cause of damage (Mob name or Environment name)
                String damageName = "";
                if (source.getAttacker() != null) {
                    // .getDisplayName().getString() is the most reliable way in 1.16.1 to get "Zombie", "Creeper", etc.
                    damageName = source.getAttacker().getDisplayName().getString();
                }

                // Fallback to environment name (e.g. "fall", "lava") if no attacker or name is empty
                if (damageName == null || damageName.isEmpty()) {
                    damageName = source.getName();
                }

                // Safety fallback
                if (damageName == null || damageName.isEmpty()) {
                    damageName = "unknown";
                }

                // Calculation for "Full Heart" display (Total HP is 20, we want to show out of 10)
                float heartsTaken = amount / 2.0f;
                float currentHearts = Math.max(0, currentHealth / 2.0f);
                float maxHearts = this.getMaxHealth() / 2.0f;

                // Build the message piece-by-piece to avoid the Networking Style NullPointer error
                LiteralText message = new LiteralText("");
                message.append(new LiteralText("[Damage] ").formatted(Formatting.GRAY));
                message.append(new LiteralText(playerName + " ").formatted(Formatting.WHITE));
                message.append(new LiteralText("took ").formatted(Formatting.GRAY));
                message.append(new LiteralText(String.format("%.1f ", heartsTaken)).formatted(Formatting.RED));
                message.append(new LiteralText("❤ ").formatted(Formatting.DARK_RED));
                message.append(new LiteralText("from ").formatted(Formatting.GRAY));
                message.append(new LiteralText(damageName + " ").formatted(Formatting.YELLOW));
                message.append(new LiteralText(" | HP: ").formatted(Formatting.GRAY));
                message.append(new LiteralText(String.format("%.1f", currentHearts)).formatted(Formatting.GREEN));
                message.append(new LiteralText("/").formatted(Formatting.GRAY));
                message.append(new LiteralText(String.format("%.1f", maxHearts)).formatted(Formatting.DARK_GREEN));

                // Broadcast to everyone
                this.getServerWorld().getPlayers().forEach(p ->
                        p.sendMessage(message, false)
                );
            }
        }
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void killEveryoneOnDeath(DamageSource damageSource, CallbackInfo ci) {
        this.getServerWorld().getPlayers().forEach(p -> {
            // (Object) cast for comparison. Only kill if not spectator and actually alive.
            if (p != (Object) this && !p.isSpectator() && p.getHealth() > 0) {
                p.kill();
            }
        });

        // We leave the health at 0 on the world.
        // This stops the mod from trying to heal dead players back to 20.
        SHARED_HEALTH.get(this.world).setHealth(0.0f);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void onRespawnSync(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        // In Hardcore, clicking 'Spectate' counts as a respawn where 'alive' is false.
        if (!alive || this.isSpectator() || this.getHealth() <= 0) {
            return;
        }

        float sharedHealth = SHARED_HEALTH.get(this.world).getHealth();
        if (sharedHealth > 0) {
            this.setHealth(sharedHealth);
        }
    }
}