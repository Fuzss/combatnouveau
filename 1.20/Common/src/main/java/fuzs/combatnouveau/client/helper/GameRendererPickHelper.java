package fuzs.combatnouveau.client.helper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class GameRendererPickHelper {

    public static double getEntityPickRange(EntityHitResult entityHitResult, Vec3 eyePosition, Vec3 pickVector) {
        if (entityHitResult != null) {
            AABB aabb = entityHitResult.getEntity().getBoundingBox().inflate(entityHitResult.getEntity().getPickRadius());
            Optional<Vec3> optional = aabb.clip(eyePosition, pickVector);
            if (optional.isPresent()) return Math.sqrt(eyePosition.distanceToSqr(optional.get()));
        }
        return 0.0;
    }

    public static HitResult pick(Entity entity, double pickRange, float partialTicks) {
        Vec3 eyePosition = entity.getEyePosition(partialTicks);
        Vec3 viewVector = entity.getViewVector(partialTicks);
        Vec3 vec3 = eyePosition.add(viewVector.x * pickRange, viewVector.y * pickRange, viewVector.z * pickRange);
        return entity.level().clip(new ClipContext(eyePosition, vec3, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }
}
