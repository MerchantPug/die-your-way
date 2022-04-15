package io.github.merchantpug.dieyourway.access;

import net.minecraft.fluid.Fluid;
import net.minecraft.tag.TagKey;

public interface SubmergableEntity {

    boolean dywIsSubmergedInLoosely(TagKey<Fluid> tag);

    double dywGetFluidHeightLoosely(TagKey<Fluid> tag);
}