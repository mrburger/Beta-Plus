package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.WorldServer;

public class EntitySelector {
   private final int limit;
   private final boolean includeNonPlayers;
   private final boolean currentWorldOnly;
   private final Predicate<Entity> filter;
   private final MinMaxBounds.FloatBound distance;
   private final Function<Vec3d, Vec3d> positionGetter;
   @Nullable
   private final AxisAlignedBB aabb;
   private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
   private final boolean self;
   @Nullable
   private final String username;
   @Nullable
   private final UUID uuid;
   private final Class<? extends Entity> type;
   private final boolean checkPermission;

   public EntitySelector(int p_i49719_1_, boolean p_i49719_2_, boolean p_i49719_3_, Predicate<Entity> p_i49719_4_, MinMaxBounds.FloatBound p_i49719_5_, Function<Vec3d, Vec3d> p_i49719_6_, @Nullable AxisAlignedBB p_i49719_7_, BiConsumer<Vec3d, List<? extends Entity>> p_i49719_8_, boolean p_i49719_9_, @Nullable String p_i49719_10_, @Nullable UUID p_i49719_11_, Class<? extends Entity> p_i49719_12_, boolean p_i49719_13_) {
      this.limit = p_i49719_1_;
      this.includeNonPlayers = p_i49719_2_;
      this.currentWorldOnly = p_i49719_3_;
      this.filter = p_i49719_4_;
      this.distance = p_i49719_5_;
      this.positionGetter = p_i49719_6_;
      this.aabb = p_i49719_7_;
      this.sorter = p_i49719_8_;
      this.self = p_i49719_9_;
      this.username = p_i49719_10_;
      this.uuid = p_i49719_11_;
      this.type = p_i49719_12_;
      this.checkPermission = p_i49719_13_;
   }

   public int getLimit() {
      return this.limit;
   }

   public boolean includesEntities() {
      return this.includeNonPlayers;
   }

   public boolean isSelfSelector() {
      return this.self;
   }

   public boolean isWorldLimited() {
      return this.currentWorldOnly;
   }

   private void checkPermission(CommandSource p_210324_1_) throws CommandSyntaxException {
      if (this.checkPermission && !p_210324_1_.hasPermissionLevel(2)) {
         throw EntityArgument.SELECTOR_NOT_ALLOWED.create();
      }
   }

   public Entity selectOne(CommandSource p_197340_1_) throws CommandSyntaxException {
      this.checkPermission(p_197340_1_);
      List<? extends Entity> list = this.select(p_197340_1_);
      if (list.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else if (list.size() > 1) {
         throw EntityArgument.TOO_MANY_ENTITIES.create();
      } else {
         return list.get(0);
      }
   }

   public List<? extends Entity> select(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      if (!this.includeNonPlayers) {
         return this.selectPlayers(source);
      } else if (this.username != null) {
         EntityPlayerMP entityplayermp = source.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List<? extends Entity>)(entityplayermp == null ? Collections.emptyList() : Lists.newArrayList(entityplayermp));
      } else if (this.uuid != null) {
         for(WorldServer worldserver1 : source.getServer().func_212370_w()) {
            Entity entity = worldserver1.getEntityFromUuid(this.uuid);
            if (entity != null) {
               return Lists.newArrayList(entity);
            }
         }

         return Collections.emptyList();
      } else {
         Vec3d vec3d = this.positionGetter.apply(source.getPos());
         Predicate<Entity> predicate = this.updateFilter(vec3d);
         if (this.self) {
            return (List<? extends Entity>)(source.getEntity() != null && predicate.test(source.getEntity()) ? Lists.newArrayList(source.getEntity()) : Collections.emptyList());
         } else {
            List<Entity> list = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.getEntities(list, source.getWorld(), vec3d, predicate);
            } else {
               for(WorldServer worldserver : source.getServer().func_212370_w()) {
                  this.getEntities(list, worldserver, vec3d, predicate);
               }
            }

            return this.sortAndLimit(vec3d, list);
         }
      }
   }

   private void getEntities(List<Entity> p_197348_1_, WorldServer p_197348_2_, Vec3d p_197348_3_, Predicate<Entity> p_197348_4_) {
      if (this.aabb != null) {
         p_197348_1_.addAll(p_197348_2_.getEntitiesWithinAABB(this.type, this.aabb.offset(p_197348_3_), p_197348_4_::test));
      } else {
         p_197348_1_.addAll(p_197348_2_.getEntities(this.type, p_197348_4_::test));
      }

   }

   public EntityPlayerMP selectOnePlayer(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      List<EntityPlayerMP> list = this.selectPlayers(source);
      if (list.size() != 1) {
         throw EntityArgument.PLAYER_NOT_FOUND.create();
      } else {
         return list.get(0);
      }
   }

   public List<EntityPlayerMP> selectPlayers(CommandSource p_197342_1_) throws CommandSyntaxException {
      this.checkPermission(p_197342_1_);
      if (this.username != null) {
         EntityPlayerMP entityplayermp2 = p_197342_1_.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List<EntityPlayerMP>)(entityplayermp2 == null ? Collections.emptyList() : Lists.newArrayList(entityplayermp2));
      } else if (this.uuid != null) {
         EntityPlayerMP entityplayermp1 = p_197342_1_.getServer().getPlayerList().getPlayerByUUID(this.uuid);
         return (List<EntityPlayerMP>)(entityplayermp1 == null ? Collections.emptyList() : Lists.newArrayList(entityplayermp1));
      } else {
         Vec3d vec3d = this.positionGetter.apply(p_197342_1_.getPos());
         Predicate<Entity> predicate = this.updateFilter(vec3d);
         if (this.self) {
            if (p_197342_1_.getEntity() instanceof EntityPlayerMP) {
               EntityPlayerMP entityplayermp3 = (EntityPlayerMP)p_197342_1_.getEntity();
               if (predicate.test(entityplayermp3)) {
                  return Lists.newArrayList(entityplayermp3);
               }
            }

            return Collections.emptyList();
         } else {
            List<EntityPlayerMP> list;
            if (this.isWorldLimited()) {
               list = p_197342_1_.getWorld().getPlayers(EntityPlayerMP.class, predicate::test);
            } else {
               list = Lists.newArrayList();

               for(EntityPlayerMP entityplayermp : p_197342_1_.getServer().getPlayerList().getPlayers()) {
                  if (predicate.test(entityplayermp)) {
                     list.add(entityplayermp);
                  }
               }
            }

            return this.sortAndLimit(vec3d, list);
         }
      }
   }

   private Predicate<Entity> updateFilter(Vec3d p_197349_1_) {
      Predicate<Entity> predicate = this.filter;
      if (this.aabb != null) {
         AxisAlignedBB axisalignedbb = this.aabb.offset(p_197349_1_);
         predicate = predicate.and((p_197344_1_) -> {
            return axisalignedbb.intersects(p_197344_1_.getBoundingBox());
         });
      }

      if (!this.distance.isUnbounded()) {
         predicate = predicate.and((p_211376_2_) -> {
            return this.distance.testSquared(p_211376_2_.getDistanceSq(p_197349_1_));
         });
      }

      return predicate;
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3d p_197345_1_, List<T> p_197345_2_) {
      if (p_197345_2_.size() > 1) {
         this.sorter.accept(p_197345_1_, p_197345_2_);
      }

      return p_197345_2_.subList(0, Math.min(this.limit, p_197345_2_.size()));
   }

   public static ITextComponent joinNames(List<? extends Entity> p_197350_0_) {
      return TextComponentUtils.makeList(p_197350_0_, Entity::getDisplayName);
   }
}