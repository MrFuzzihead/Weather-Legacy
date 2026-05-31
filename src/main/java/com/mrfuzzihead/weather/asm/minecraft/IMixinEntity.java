package com.mrfuzzihead.weather.asm.minecraft;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IMixinEntity {

    @Invoker
    void invokeSetSize(float width, float height);
}
