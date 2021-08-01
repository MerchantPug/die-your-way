package io.github.merchantpug.dieyourway.access;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.Tag;

public interface SubmergableEntity {

    boolean dywIsSubmergedInLoosely(Tag<Fluid> fluidTag);

    double dywGetFluidHeightLoosely(Tag<Fluid> fluidTag);
}