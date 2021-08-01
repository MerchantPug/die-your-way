package io.github.merchantpug.dieyourway.util;

import io.github.merchantpug.dieyourway.DieYourWay;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;

public enum Argument {
    CALLER(DieYourWay.identifier("caller")),
    ATTACKER(DieYourWay.identifier("attacker")),
    CALLER_ITEM(DieYourWay.identifier("item"));

    private final Identifier argumentIdentifier;

    Argument(Identifier argumentIdentifier) {
        this.argumentIdentifier = argumentIdentifier;
    }

    public Identifier getComparisonIdentifier() {
        return argumentIdentifier;
    }
}
