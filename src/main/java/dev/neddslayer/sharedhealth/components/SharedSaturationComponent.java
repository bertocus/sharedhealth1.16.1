package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class SharedSaturationComponent implements ISaturationComponent {
    private final World world;
    private float saturation = 5.0f;

    public SharedSaturationComponent(World world) {
        this.world = world;
    }

    @Override
    public float getSaturation() {
        return this.saturation;
    }

    @Override
    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.saturation = tag.getFloat("Saturation");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("Saturation", this.saturation);
        return tag;
    }
}