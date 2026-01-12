package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class SharedHealthComponent implements IHealthComponent {

    private final World world;
    private float health = 20.0f;

    public SharedHealthComponent(World world) {
        this.world = world;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public void setHealth(float health) {
        this.health = health;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.health = tag.getFloat("playerHealth");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("playerHealth", this.health);
        return tag;
    }
}