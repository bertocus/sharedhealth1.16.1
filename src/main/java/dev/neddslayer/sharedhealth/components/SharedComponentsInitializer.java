package dev.neddslayer.sharedhealth.components;

import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import net.minecraft.util.Identifier;

public class SharedComponentsInitializer {

    public static final ComponentType<SharedHealthComponent> SHARED_HEALTH =
            ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("sharedhealth", "health"), SharedHealthComponent.class);

    public static final ComponentType<SharedHungerComponent> SHARED_HUNGER =
            ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("sharedhealth", "hunger"), SharedHungerComponent.class);

    public static final ComponentType<SharedSaturationComponent> SHARED_SATURATION =
            ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("sharedhealth", "saturation"), SharedSaturationComponent.class);

    public static final ComponentType<SharedExhaustionComponent> SHARED_EXHAUSTION =
            ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("sharedhealth", "exhaustion"), SharedExhaustionComponent.class);
}