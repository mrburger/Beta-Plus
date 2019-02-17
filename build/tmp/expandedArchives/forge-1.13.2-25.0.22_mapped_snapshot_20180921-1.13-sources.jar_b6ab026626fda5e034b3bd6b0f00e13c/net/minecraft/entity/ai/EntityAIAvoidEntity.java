package net.minecraft.entity.ai;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> canBeSeenSelector = new Predicate<Entity>() {
      public boolean test(@Nullable Entity p_test_1_) {
         return p_test_1_.isAlive() && EntityAIAvoidEntity.this.entity.getEntitySenses().canSee(p_test_1_) && !EntityAIAvoidEntity.this.entity.isOnSameTeam(p_test_1_);
      }
   };
   /** The entity we are attached to */
   protected EntityCreature entity;
   private final double farSpeed;
   private final double nearSpeed;
   protected T closestLivingEntity;
   private final float avoidDistance;
   /** The PathEntity of our entity */
   private Path path;
   /** The PathNavigate of our entity */
   private final PathNavigate navigation;
   /** Class of entity this behavior seeks to avoid */
   private final Class<T> classToAvoid;
   private final Predicate<? super Entity> avoidTargetSelector;
   private final Predicate<? super Entity> field_203784_k;

   public EntityAIAvoidEntity(EntityCreature entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this(entityIn, classToAvoidIn, (p_200828_0_) -> {
         return true;
      }, avoidDistanceIn, farSpeedIn, nearSpeedIn, EntitySelectors.CAN_AI_TARGET);
   }

   public EntityAIAvoidEntity(EntityCreature p_i48859_1_, Class<T> p_i48859_2_, Predicate<? super Entity> p_i48859_3_, float p_i48859_4_, double p_i48859_5_, double p_i48859_7_, Predicate<Entity> p_i48859_9_) {
      this.entity = p_i48859_1_;
      this.classToAvoid = p_i48859_2_;
      this.avoidTargetSelector = p_i48859_3_;
      this.avoidDistance = p_i48859_4_;
      this.farSpeed = p_i48859_5_;
      this.nearSpeed = p_i48859_7_;
      this.field_203784_k = p_i48859_9_;
      this.navigation = p_i48859_1_.getNavigator();
      this.setMutexBits(1);
   }

   public EntityAIAvoidEntity(EntityCreature p_i48860_1_, Class<T> p_i48860_2_, float p_i48860_3_, double p_i48860_4_, double p_i48860_6_, Predicate<Entity> p_i48860_8_) {
      this(p_i48860_1_, p_i48860_2_, (p_203782_0_) -> {
         return true;
      }, p_i48860_3_, p_i48860_4_, p_i48860_6_, p_i48860_8_);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      List<T> list = this.entity.world.getEntitiesWithinAABB(this.classToAvoid, this.entity.getBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), (p_203783_1_) -> {
         return this.field_203784_k.test(p_203783_1_) && this.canBeSeenSelector.test(p_203783_1_) && this.avoidTargetSelector.test(p_203783_1_);
      });
      if (list.isEmpty()) {
         return false;
      } else {
         this.closestLivingEntity = list.get(0);
         Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));
         if (vec3d == null) {
            return false;
         } else if (this.closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.getDistanceSq(this.entity)) {
            return false;
         } else {
            this.path = this.navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
            return this.path != null;
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.navigation.noPath();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.navigation.setPath(this.path, this.farSpeed);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.closestLivingEntity = null;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.entity.getDistanceSq(this.closestLivingEntity) < 49.0D) {
         this.entity.getNavigator().setSpeed(this.nearSpeed);
      } else {
         this.entity.getNavigator().setSpeed(this.farSpeed);
      }

   }
}