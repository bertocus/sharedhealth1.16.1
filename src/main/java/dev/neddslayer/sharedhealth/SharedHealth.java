package dev.neddslayer.sharedhealth;

import dev.neddslayer.sharedhealth.components.SharedExhaustionComponent;
import dev.neddslayer.sharedhealth.components.SharedHealthComponent;
import dev.neddslayer.sharedhealth.components.SharedHungerComponent;
import dev.neddslayer.sharedhealth.components.SharedSaturationComponent;
import dev.neddslayer.sharedhealth.mixin.HungerManagerAccessor;
import nerdhub.cardinal.components.api.event.WorldComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import static dev.neddslayer.sharedhealth.components.SharedComponentsInitializer.*;

public class SharedHealth implements ModInitializer {

    public static final GameRules.Key<GameRules.BooleanRule> SYNC_HEALTH =
            GameRuleRegistry.register("shareHealth", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> SYNC_HUNGER =
            GameRuleRegistry.register("shareHunger", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    private static boolean lastHealthValue = true;
    private static boolean lastHungerValue = true;

    @Override
    public void onInitialize() {
        // --- 1. CARDINAL COMPONENTS REGISTRATION ---
        WorldComponentCallback.EVENT.register((world, components) -> {
            components.put(SHARED_HEALTH, new SharedHealthComponent(world));
            components.put(SHARED_HUNGER, new SharedHungerComponent(world));
            components.put(SHARED_SATURATION, new SharedSaturationComponent(world));
            components.put(SHARED_EXHAUSTION, new SharedExhaustionComponent(world));
        });

        // --- 2. SERVER TICK CALLBACK ---
        ServerTickCallback.EVENT.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                final boolean currentHealthValue = world.getGameRules().getBoolean(SYNC_HEALTH);
                final boolean currentHungerValue = world.getGameRules().getBoolean(SYNC_HUNGER);

                // Gamerule notifications
                if (currentHealthValue != lastHealthValue) {
                    Formatting color = currentHealthValue ? Formatting.GREEN : Formatting.RED;
                    String status = currentHealthValue ? "enabled" : "disabled";
                    world.getPlayers().forEach(p -> p.sendMessage(new TranslatableText("gamerule.shareHealth." + status).formatted(color, Formatting.BOLD), false));
                    lastHealthValue = currentHealthValue;
                }

                if (currentHungerValue != lastHungerValue) {
                    Formatting color = currentHungerValue ? Formatting.GREEN : Formatting.RED;
                    String status = currentHungerValue ? "enabled" : "disabled";
                    world.getPlayers().forEach(p -> p.sendMessage(new TranslatableText("gamerule.shareHunger." + status).formatted(color, Formatting.BOLD), false));
                    lastHungerValue = currentHungerValue;
                }

                // --- HEALTH SYNC ---
                if (currentHealthValue) {
                    final float targetHealth = SHARED_HEALTH.get(world).getHealth();

                    world.getPlayers().forEach(player -> {
                        // Do not touch players who are spectating, have 0 HP, or are marked for removal.
                        // Healing a player with 0 HP in Hardcore triggers the death screen loop.
                        if (player.isSpectator() || player.getHealth() <= 0 || player.removed) {
                            return;
                        }

                        float currentHealth = player.getHealth();
                        if (currentHealth > targetHealth) {
                            player.damage(DamageSource.OUT_OF_WORLD, currentHealth - targetHealth);
                        } else if (currentHealth < targetHealth) {
                            player.heal(targetHealth - currentHealth);
                        }
                    });
                }

                // --- HUNGER SYNC ---
                if (currentHungerValue) {
                    final int targetHunger = SHARED_HUNGER.get(world).getHunger();
                    final float targetSaturation = SHARED_SATURATION.get(world).getSaturation();
                    final float targetExhaustion = SHARED_EXHAUSTION.get(world).getExhaustion();

                    world.getPlayers().forEach(player -> {
                        // Skip spectators for hunger too
                        if (player.isSpectator() || player.removed) return;

                        try {
                            HungerManager hm = player.getHungerManager();
                            HungerManagerAccessor acc = (HungerManagerAccessor) hm;

                            if (hm.getFoodLevel() != targetHunger) hm.setFoodLevel(targetHunger);
                            if (hm.getSaturationLevel() != targetSaturation) acc.setSat(targetSaturation);
                            if (acc.getExh() != targetExhaustion) acc.setExh(targetExhaustion);
                        } catch (Exception ignored) {
                        }
                    });
                }
            }
        });
    }
}