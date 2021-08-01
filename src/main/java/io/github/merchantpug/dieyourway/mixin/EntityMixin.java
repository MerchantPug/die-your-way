package io.github.merchantpug.dieyourway.mixin;

import io.github.apace100.calio.Calio;
import io.github.merchantpug.dieyourway.access.MovingEntity;
import io.github.merchantpug.dieyourway.access.SubmergableEntity;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements MovingEntity, SubmergableEntity {

    @Shadow
    public World world;

    @Shadow
    public float distanceTraveled;

    @Shadow @Nullable
    protected Tag<Fluid> submergedFluidTag;

    @Shadow protected Object2DoubleMap<Tag<Fluid>> fluidHeight;

    private boolean isMoving;
    private float distanceBefore;

    @Inject(method = "move", at = @At("HEAD"))
    private void saveDistanceTraveled(MovementType type, Vec3d movement, CallbackInfo ci) {
        this.isMoving = false;
        this.distanceBefore = this.distanceTraveled;
    }

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void checkIsMoving(MovementType type, Vec3d movement, CallbackInfo ci) {
        if(this.distanceTraveled > this.distanceBefore) {
            this.isMoving = true;
        }
    }

    @Override
    public boolean dywIsSubmergedInLoosely(Tag<Fluid> tag) {
        if(tag == null || submergedFluidTag == null) {
            return false;
        }
        if(tag == submergedFluidTag) {
            return true;
        }
        return Calio.areTagsEqual(Registry.FLUID_KEY, tag, submergedFluidTag);
    }

    @Override
    public double dywGetFluidHeightLoosely(Tag<Fluid> tag) {
        if(tag == null) {
            return 0;
        }
        if(fluidHeight.containsKey(tag)) {
            return fluidHeight.getDouble(tag);
        }
        for(Tag<Fluid> ft : fluidHeight.keySet()) {
            if(Calio.areTagsEqual(Registry.FLUID_KEY, ft, tag)) {
                return fluidHeight.getDouble(ft);
            }
        }
        return 0;
    }

    @Override
    public boolean dywIsMoving() {
        return isMoving;
    }
}
