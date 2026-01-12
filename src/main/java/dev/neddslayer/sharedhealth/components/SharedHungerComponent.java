package dev.neddslayer.sharedhealth.components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

public class SharedHungerComponent implements IHungerComponent {

    private final World world;
    private int hunger = 20;

    public SharedHungerComponent(World world) {
        this.world = world;
    }

    @Override
    public int getHunger() {
        return this.hunger;
    }

    @Override
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.hunger = tag.getInt("Hunger");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("Hunger", this.hunger);
        return tag;
    }
}