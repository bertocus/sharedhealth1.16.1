package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class SharedExhaustionComponent implements IExhaustionComponent {
    private final World world; // Changed from Scoreboard to World
    private float exhaustion = 0.0f;

    public SharedExhaustionComponent(World world) {
        this.world = world;
    }

    @Override
    public float getExhaustion() {
        return this.exhaustion;
    }

    @Override
    public void setExhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.exhaustion = tag.getFloat("Exhaustion");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("Exhaustion", this.exhaustion);
        return tag;
    }
}