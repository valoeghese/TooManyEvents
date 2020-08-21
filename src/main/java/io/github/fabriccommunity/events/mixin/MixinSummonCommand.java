package io.github.fabriccommunity.events.mixin;

import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.github.fabriccommunity.events.EntitySpawnCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

/**
 * @author Valoeghese
 */
@Mixin(SummonCommand.class)
public class MixinSummonCommand {
	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tryLoadEntity(Lnet/minecraft/entity/Entity;)Z"),
			method = "method_18192(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)Lnet/minecraft/entity/Entity;")
	private static boolean entitySpawnEventCommand(ServerWorld self, Entity entity) {
		AtomicReference<Entity> currentEntity = new AtomicReference<>(entity);
		ActionResult result = EntitySpawnCallback.PRE.invoker().onEntitySpawnPre(entity, currentEntity, self, SpawnReason.COMMAND);
		entity = currentEntity.get();

		if (result == ActionResult.SUCCESS) {
			if (self.spawnEntity(entity)) {
				EntitySpawnCallback.POST.invoker().onEntitySpawnPost(entity, self, entity.getPos(), SpawnReason.COMMAND);
				return true;
			}
		}

		return false;
	}
}
