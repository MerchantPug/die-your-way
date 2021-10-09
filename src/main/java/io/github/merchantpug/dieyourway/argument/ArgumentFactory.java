package io.github.merchantpug.dieyourway.argument;

import com.google.gson.JsonObject;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ArgumentFactory<S> {

    private final Identifier identifier;
    protected SerializableData data;
    protected Function<SerializableData.Instance, BiFunction<Pair<DamageSource, Float>, DamageTracker, S>> factoryConstructor;

    public ArgumentFactory(Identifier identifier, SerializableData data, Function<SerializableData.Instance, BiFunction<Pair<DamageSource, Float>, DamageTracker, S>> factoryConstructor) {
        this.identifier = identifier;
        this.data = data;
        this.factoryConstructor = factoryConstructor;
    }

    public class Instance implements BiFunction<Pair<DamageSource, Float>, DamageTracker, S> {

        private final SerializableData.Instance dataInstance;

        private Instance(SerializableData.Instance data) {
            this.dataInstance = data;
        }

        public void write(PacketByteBuf buf) {
            buf.writeIdentifier(identifier);
            data.write(buf, dataInstance);
        }

        @Override
        public S apply(Pair<DamageSource, Float> damageSourceFloatPair, DamageTracker damageTracker) {
            BiFunction<Pair<DamageSource, Float>, DamageTracker, S> argumentFactory = factoryConstructor.apply(dataInstance);
            return argumentFactory.apply(damageSourceFloatPair, damageTracker);
        }
    }

    public Identifier getSerializerId() {
        return identifier;
    }

    public Instance read(JsonObject json) {
        return new Instance(data.read(json));
    }

    public Instance read(PacketByteBuf buffer) {
        return new Instance(data.read(buffer));
    }
}
