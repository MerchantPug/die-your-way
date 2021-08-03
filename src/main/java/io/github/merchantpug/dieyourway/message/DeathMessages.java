package io.github.merchantpug.dieyourway.message;

import com.google.gson.JsonObject;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.merchantpug.dieyourway.data.DYWDataTypes;
import io.github.merchantpug.dieyourway.message.argument.ArgumentFactory;
import io.github.merchantpug.dieyourway.message.condition.DYWConditionFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DeathMessages {

    public static final SerializableData DATA = new SerializableData()
            .add("loading_order", SerializableDataTypes.INT, Integer.MAX_VALUE)
            .add("messages", DYWDataTypes.STRINGS, null)
            .add("arguments", DYWDataTypes.ARGUMENT_TYPES, null)
            .add("override", SerializableDataTypes.BOOLEAN, false);


    private Identifier identifier;
    private int loadingOrder;
    private DYWConditionFactory<Pair<DamageSource, Float>>.Instance damageCondition;
    private DYWConditionFactory<LivingEntity>.Instance condition;
    private ConditionFactory<Pair<DamageSource, Float>>.Instance apoliDamageCondition;
    private ConditionFactory<LivingEntity>.Instance apoliCondition;
    private boolean override;

    private List<String> messages = new ArrayList<>();
    private List<ArgumentFactory<String>.Instance> arguments = new ArrayList<>();

    public DeathMessages(Identifier id, int loadingOrder, boolean override) {
        this.identifier = id;
        this.loadingOrder = loadingOrder;
        this.override = override;
    }

    public DeathMessages addMessage(String message) {
        this.messages.add(message);
        return this;
    }

    public DeathMessages addArguments(ArgumentFactory.Instance argumentFactory) {
        this.arguments.add(argumentFactory);
        return this;
    }

    public DYWConditionFactory<Pair<DamageSource, Float>>.Instance getDamageCondition() {
        return damageCondition;
    }

    public DYWConditionFactory<LivingEntity>.Instance getCondition() {
        return condition;
    }

    public ConditionFactory<Pair<DamageSource, Float>>.Instance getApoliDamageCondition() {
        return apoliDamageCondition;
    }

    public ConditionFactory<LivingEntity>.Instance getApoliCondition() {
        return apoliCondition;
    }

    public boolean hasMessage() {
        return this.messages.size() > 0;
    }

    public boolean hasArguments() {
        return this.arguments.size() > 0;
    }

    public int loadingOrderValue() {
        return loadingOrder;
    }

    public boolean doesOverride() {
        return this.override;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<ArgumentFactory<String>.Instance> getArguments() {
        return arguments;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void write(PacketByteBuf buffer) {
        SerializableData.Instance data = DATA.new Instance();
        data.set("loading_order", loadingOrder);
        data.set("damage_condition", damageCondition);
        data.set("condition", condition);
        data.set("messages", messages);
        data.set("arguments", arguments);
        data.set("override", override);
        DATA.write(buffer, data);
    }

    @SuppressWarnings("unchecked")
    public static DeathMessages createFromData(Identifier id, SerializableData.Instance data) {
        DeathMessages deathMessage = new DeathMessages(id, data.getInt("loading_order"), data.getBoolean("override"));
        if (FabricLoader.getInstance().isModLoaded("apoli")) {
            if (data.isPresent("damage_condition")) {
                deathMessage.apoliDamageCondition = (ConditionFactory<Pair<DamageSource, Float>>.Instance) data.get("damage_condition");
            }
            if (data.isPresent("condition")) {
                deathMessage.apoliCondition = (ConditionFactory<LivingEntity>.Instance)data.get("condition");
            }
        } else {
            if (data.isPresent("damage_condition")) {
                deathMessage.damageCondition = (DYWConditionFactory<Pair<DamageSource, Float>>.Instance) data.get("damage_condition");
            }
            if (data.isPresent("condition")) {
                deathMessage.condition = (DYWConditionFactory<LivingEntity>.Instance)data.get("condition");
            }
        }
        if(data.isPresent("messages")) {
            ((List<String>)data.get("messages")).forEach(deathMessage::addMessage);
        }
        if (data.isPresent("arguments")) {
            ((List<ArgumentFactory<String>.Instance>)data.get("arguments")).forEach(deathMessage::addArguments);
        }
        return deathMessage;
    }

    @Environment(EnvType.CLIENT)
    public static DeathMessages read(PacketByteBuf buffer) {
        Identifier identifier = Identifier.tryParse(buffer.readString(32767));
        return createFromData(identifier, DATA.read(buffer));
    }

    public static DeathMessages fromJson(Identifier id, JsonObject json) {
        return createFromData(id, DATA.read(json));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("DeathMessage(" + identifier.toString() + ")[");
        for(String pt : messages) {
            str.append(pt);
            str.append(",");
        }
        str = new StringBuilder(str.substring(0, str.length() - 1) + "]");
        return str.toString();
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DeathMessages) {
            return ((DeathMessages)obj).identifier.equals(identifier);
        }
        return false;
    }
}