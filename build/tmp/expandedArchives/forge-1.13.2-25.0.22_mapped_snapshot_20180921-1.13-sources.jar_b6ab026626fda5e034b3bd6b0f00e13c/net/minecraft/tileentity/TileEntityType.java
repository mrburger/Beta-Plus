package net.minecraft.tileentity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityType<T extends TileEntity> extends net.minecraftforge.registries.ForgeRegistryEntry<TileEntityType<?>> {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TileEntityType<TileEntityFurnace> FURNACE = register("furnace", TileEntityType.Builder.create(TileEntityFurnace::new));
   public static final TileEntityType<TileEntityChest> CHEST = register("chest", TileEntityType.Builder.create(TileEntityChest::new));
   public static final TileEntityType<TileEntityTrappedChest> TRAPPED_CHEST = register("trapped_chest", TileEntityType.Builder.create(TileEntityTrappedChest::new));
   public static final TileEntityType<TileEntityEnderChest> ENDER_CHEST = register("ender_chest", TileEntityType.Builder.create(TileEntityEnderChest::new));
   public static final TileEntityType<TileEntityJukebox> JUKEBOX = register("jukebox", TileEntityType.Builder.create(TileEntityJukebox::new));
   public static final TileEntityType<TileEntityDispenser> DISPENSER = register("dispenser", TileEntityType.Builder.create(TileEntityDispenser::new));
   public static final TileEntityType<TileEntityDropper> DROPPER = register("dropper", TileEntityType.Builder.create(TileEntityDropper::new));
   public static final TileEntityType<TileEntitySign> SIGN = register("sign", TileEntityType.Builder.create(TileEntitySign::new));
   public static final TileEntityType<TileEntityMobSpawner> MOB_SPAWNER = register("mob_spawner", TileEntityType.Builder.create(TileEntityMobSpawner::new));
   public static final TileEntityType<TileEntityPiston> PISTON = register("piston", TileEntityType.Builder.create(TileEntityPiston::new));
   public static final TileEntityType<TileEntityBrewingStand> BREWING_STAND = register("brewing_stand", TileEntityType.Builder.create(TileEntityBrewingStand::new));
   public static final TileEntityType<TileEntityEnchantmentTable> ENCHANTING_TABLE = register("enchanting_table", TileEntityType.Builder.create(TileEntityEnchantmentTable::new));
   public static final TileEntityType<TileEntityEndPortal> END_PORTAL = register("end_portal", TileEntityType.Builder.create(TileEntityEndPortal::new));
   public static final TileEntityType<TileEntityBeacon> BEACON = register("beacon", TileEntityType.Builder.create(TileEntityBeacon::new));
   public static final TileEntityType<TileEntitySkull> SKULL = register("skull", TileEntityType.Builder.create(TileEntitySkull::new));
   public static final TileEntityType<TileEntityDaylightDetector> DAYLIGHT_DETECTOR = register("daylight_detector", TileEntityType.Builder.create(TileEntityDaylightDetector::new));
   public static final TileEntityType<TileEntityHopper> HOPPER = register("hopper", TileEntityType.Builder.create(TileEntityHopper::new));
   public static final TileEntityType<TileEntityComparator> COMPARATOR = register("comparator", TileEntityType.Builder.create(TileEntityComparator::new));
   public static final TileEntityType<TileEntityBanner> BANNER = register("banner", TileEntityType.Builder.create(TileEntityBanner::new));
   public static final TileEntityType<TileEntityStructure> STRUCTURE_BLOCK = register("structure_block", TileEntityType.Builder.create(TileEntityStructure::new));
   public static final TileEntityType<TileEntityEndGateway> END_GATEWAY = register("end_gateway", TileEntityType.Builder.create(TileEntityEndGateway::new));
   public static final TileEntityType<TileEntityCommandBlock> COMMAND_BLOCK = register("command_block", TileEntityType.Builder.create(TileEntityCommandBlock::new));
   public static final TileEntityType<TileEntityShulkerBox> SHULKER_BOX = register("shulker_box", TileEntityType.Builder.create(TileEntityShulkerBox::new));
   public static final TileEntityType<TileEntityBed> BED = register("bed", TileEntityType.Builder.create(TileEntityBed::new));
   public static final TileEntityType<TileEntityConduit> CONDUIT = register("conduit", TileEntityType.Builder.create(TileEntityConduit::new));
   private final Supplier<? extends T> factory;
   private final Type<?> datafixerType;

   @Nullable
   public static ResourceLocation getId(TileEntityType<?> tileEntityTypeIn) {
      return IRegistry.field_212626_o.getKey(tileEntityTypeIn);
   }

   public static <T extends TileEntity> TileEntityType<T> register(String id, TileEntityType.Builder<T> builder) {
      Type<?> type = null;

      try {
         type = DataFixesManager.getDataFixer().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(TypeReferences.BLOCK_ENTITY, id);
      } catch (IllegalArgumentException illegalstateexception) {
         if (SharedConstants.developmentMode) {
            throw illegalstateexception;
         }

         LOGGER.warn("No data fixer registered for block entity {}", (Object)id);
      }

      TileEntityType<T> tileentitytype = builder.build(type);
      IRegistry.field_212626_o.put(new ResourceLocation(id), tileentitytype);
      return tileentitytype;
   }

   public static void func_212641_a() {
   }

   public TileEntityType(Supplier<? extends T> factoryIn, Type<?> datafixerTypeIn) {
      this.factory = factoryIn;
      this.datafixerType = datafixerTypeIn;
   }

   @Nullable
   public T create() {
      return (T)(this.factory.get());
   }

   @Nullable
   static TileEntity create(String id) {
      TileEntityType<?> tileentitytype = IRegistry.field_212626_o.func_212608_b(new ResourceLocation(id));
      return tileentitytype == null ? null : tileentitytype.create();
   }

   public static final class Builder<T extends TileEntity> {
      private final Supplier<? extends T> factory;

      private Builder(Supplier<? extends T> factoryIn) {
         this.factory = factoryIn;
      }

      public static <T extends TileEntity> TileEntityType.Builder<T> create(Supplier<? extends T> factoryIn) {
         return new TileEntityType.Builder<>(factoryIn);
      }

      public TileEntityType<T> build(Type<?> datafixerType) {
         return new TileEntityType<>(this.factory, datafixerType);
      }
   }
}