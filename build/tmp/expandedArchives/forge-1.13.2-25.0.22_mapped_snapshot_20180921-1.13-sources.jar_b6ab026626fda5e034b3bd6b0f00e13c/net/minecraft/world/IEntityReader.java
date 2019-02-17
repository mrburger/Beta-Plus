package net.minecraft.world;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public interface IEntityReader {
   /**
    * Gets all entities within the specified AABB excluding the one passed into it.
    */
   List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate);

   /**
    * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
    */
   default List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
      return this.getEntitiesInAABBexcluding(entityIn, bb, EntitySelectors.NOT_SPECTATING);
   }

   default Stream<VoxelShape> getCollisionBoxes(@Nullable Entity entityIn, VoxelShape shape, Set<Entity> breakOnEntityCollide) {
      if (shape.isEmpty()) {
         return Stream.empty();
      } else {
         AxisAlignedBB axisalignedbb = shape.getBoundingBox();
         return this.getEntitiesWithinAABBExcludingEntity(entityIn, axisalignedbb.grow(0.25D)).stream().filter((p_212382_2_) -> {
            return !breakOnEntityCollide.contains(p_212382_2_) && (entityIn == null || !entityIn.isRidingSameEntity(p_212382_2_));
         }).flatMap((p_212380_2_) -> {
            return Stream.of(p_212380_2_.getCollisionBoundingBox(), entityIn == null ? null : entityIn.getCollisionBox(p_212380_2_)).filter(Objects::nonNull).filter((p_212381_1_) -> {
               return p_212381_1_.intersects(axisalignedbb);
            }).map(VoxelShapes::create);
         });
      }
   }
}