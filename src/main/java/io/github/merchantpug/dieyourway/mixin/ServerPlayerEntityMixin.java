package io.github.merchantpug.dieyourway.mixin;

import com.mojang.authlib.GameProfile;
import io.github.merchantpug.dieyourway.DieYourWay;
import io.github.merchantpug.dieyourway.util.DeathMessageGenerator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
	@Unique
	Pair<DamageSource, Float> dyw_damageSource;

	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "damage", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void getVariables(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		dyw_damageSource = new Pair<>(source, amount);
	}

	@ModifyVariable(method = "onDeath", at = @At(value = "STORE", ordinal = 0))
	private Text replaceText(Text text) {
		Text text1 = DeathMessageGenerator.generateDeathMessage(dyw_damageSource, this.getDamageTracker());
		if (text1 != null) {
			return text1;
		}
		return text;
	}
}
