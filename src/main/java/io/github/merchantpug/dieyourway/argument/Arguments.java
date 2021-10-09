package io.github.merchantpug.dieyourway.argument;

import io.github.apace100.calio.data.SerializableData;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.registry.DYWRegistries;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class Arguments {
    @SuppressWarnings("unchecked")
    public static void register() {
        register(new ArgumentFactory<>(DieYourWay.identifier("dead_entity"), new SerializableData(),
                (data) ->
                        ((damage, tracker) -> Text.Serializer.toJson(tracker.getEntity().getDisplayName()))));
        register(new ArgumentFactory<>(DieYourWay.identifier("attacker"), new SerializableData(),
                (data) ->
                        ((damage, tracker) -> {
                            if (damage.getLeft().getAttacker() != null) {
                                return Text.Serializer.toJson(damage.getLeft().getAttacker().getDisplayName());
                            }
                            return "{\"text\":\"Attacker\"}";
                        })));
        register(new ArgumentFactory<>(DieYourWay.identifier("attacker_item"), new SerializableData(),
                (data) ->
                        ((damage, tracker) -> {
                            if (damage.getLeft().getAttacker() != null) {
                                if (damage.getLeft().getAttacker() instanceof LivingEntity) {
                                    if (!((LivingEntity)damage.getLeft().getAttacker()).getMainHandStack().isEmpty()) {
                                        return Text.Serializer.toJson(((LivingEntity)damage.getLeft().getAttacker()).getMainHandStack().toHoverableText());
                                    }
                                    return "{\"text\":\"[Air]\"}";
                                }
                            }
                            return "{\"text\":\"[ItemStack]\"}";
                        })));
    }

    private static void register(ArgumentFactory<String> argumentFactory) {
        Registry.register(DYWRegistries.ARGUMENT_FACTORY, argumentFactory.getSerializerId(), argumentFactory);
    }
}
