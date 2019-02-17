package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureIO {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.newHashMap();
   private static final Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.newHashMap();
   private static final Map<String, Class<? extends StructurePiece>> componentNameToClassMap = Maps.newHashMap();
   private static final Map<Class<? extends StructurePiece>, String> componentClassToNameMap = Maps.newHashMap();

   private static void registerStructure(Class<? extends StructureStart> startClass, String structureName) {
      startNameToClassMap.put(structureName, startClass);
      startClassToNameMap.put(startClass, structureName);
   }

   public static void registerStructureComponent(Class<? extends StructurePiece> componentClass, String componentName) {
      componentNameToClassMap.put(componentName, componentClass);
      componentClassToNameMap.put(componentClass, componentName);
   }

   public static String getStructureStartName(StructureStart start) {
      return startClassToNameMap.get(start.getClass());
   }

   public static String getStructureComponentName(StructurePiece component) {
      return componentClassToNameMap.get(component.getClass());
   }

   @Nullable
   public static StructureStart func_202602_a(NBTTagCompound p_202602_0_, IWorld p_202602_1_) {
      StructureStart structurestart = null;
      String s = p_202602_0_.getString("id");
      if ("INVALID".equals(s)) {
         return Structure.NO_STRUCTURE;
      } else {
         try {
            Class<? extends StructureStart> oclass = startNameToClassMap.get(s);
            if (oclass != null) {
               structurestart = oclass.newInstance();
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed Start with id {}", (Object)s);
            exception.printStackTrace();
         }

         if (structurestart != null) {
            structurestart.read(p_202602_1_, p_202602_0_);
         } else {
            LOGGER.warn("Skipping Structure with id {}", (Object)s);
         }

         return structurestart;
      }
   }

   public static StructurePiece getStructureComponent(NBTTagCompound tagCompound, IWorld worldIn) {
      StructurePiece structurepiece = null;

      try {
         Class<? extends StructurePiece> oclass = componentNameToClassMap.get(tagCompound.getString("id"));
         if (oclass != null) {
            structurepiece = oclass.newInstance();
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed Piece with id {}", (Object)tagCompound.getString("id"));
         exception.printStackTrace();
      }

      if (structurepiece != null) {
         structurepiece.readStructureBaseNBT(worldIn, tagCompound);
      } else {
         LOGGER.warn("Skipping Piece with id {}", (Object)tagCompound.getString("id"));
      }

      return structurepiece;
   }

   static {
      registerStructure(MineshaftStructure.Start.class, "Mineshaft");
      registerStructure(VillageStructure.Start.class, "Village");
      registerStructure(FortressStructure.Start.class, "Fortress");
      registerStructure(StrongholdStructure.Start.class, "Stronghold");
      registerStructure(JunglePyramidStructure.Start.class, "Jungle_Pyramid");
      registerStructure(OceanRuinStructure.Start.class, "Ocean_Ruin");
      registerStructure(DesertPyramidStructure.Start.class, "Desert_Pyramid");
      registerStructure(IglooStructure.Start.class, "Igloo");
      registerStructure(SwampHutStructure.Start.class, "Swamp_Hut");
      registerStructure(OceanMonumentStructure.Start.class, "Monument");
      registerStructure(EndCityStructure.Start.class, "EndCity");
      registerStructure(WoodlandMansionStructure.Start.class, "Mansion");
      registerStructure(BuriedTreasureStructure.Start.class, "Buried_Treasure");
      registerStructure(ShipwreckStructure.Start.class, "Shipwreck");
      MineshaftPieces.registerStructurePieces();
      VillagePieces.registerVillagePieces();
      FortressPieces.registerNetherFortressPieces();
      StrongholdPieces.registerStrongholdPieces();
      JunglePyramidPiece.registerJunglePyramidPieces();
      OceanRuinPieces.registerPieces();
      IglooPieces.registerPieces();
      SwampHutPiece.registerPieces();
      DesertPyramidPiece.registerPieces();
      OceanMonumentPieces.registerOceanMonumentPieces();
      EndCityPieces.registerPieces();
      WoodlandMansionPieces.registerWoodlandMansionPieces();
      BuriedTreasurePieces.registerBuriedTreasurePieces();
      ShipwreckPieces.registerShipwreckPieces();
   }
}