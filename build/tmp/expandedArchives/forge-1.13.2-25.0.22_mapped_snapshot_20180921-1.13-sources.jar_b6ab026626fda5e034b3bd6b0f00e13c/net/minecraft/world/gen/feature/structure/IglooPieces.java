package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class IglooPieces {
   private static final ResourceLocation field_202592_e = new ResourceLocation("igloo/top");
   private static final ResourceLocation field_202593_f = new ResourceLocation("igloo/middle");
   private static final ResourceLocation field_202594_g = new ResourceLocation("igloo/bottom");
   private static final Map<ResourceLocation, BlockPos> field_207621_d = ImmutableMap.of(field_202592_e, new BlockPos(3, 5, 5), field_202593_f, new BlockPos(1, 3, 1), field_202594_g, new BlockPos(3, 6, 7));
   private static final Map<ResourceLocation, BlockPos> field_207622_e = ImmutableMap.of(field_202592_e, new BlockPos(0, 0, 0), field_202593_f, new BlockPos(2, -3, 4), field_202594_g, new BlockPos(0, -3, -2));

   public static void registerPieces() {
      StructureIO.registerStructureComponent(IglooPieces.Piece.class, "Iglu");
   }

   public static void func_207617_a(TemplateManager p_207617_0_, BlockPos p_207617_1_, Rotation p_207617_2_, List<StructurePiece> p_207617_3_, Random p_207617_4_, IglooConfig p_207617_5_) {
      if (p_207617_4_.nextDouble() < 0.5D) {
         int i = p_207617_4_.nextInt(8) + 4;
         p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202594_g, p_207617_1_, p_207617_2_, i * 3));

         for(int j = 0; j < i - 1; ++j) {
            p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202593_f, p_207617_1_, p_207617_2_, j * 3));
         }
      }

      p_207617_3_.add(new IglooPieces.Piece(p_207617_0_, field_202592_e, p_207617_1_, p_207617_2_, 0));
   }

   public static class Piece extends TemplateStructurePiece {
      private ResourceLocation field_207615_d;
      private Rotation field_207616_e;

      public Piece() {
      }

      public Piece(TemplateManager p_i49313_1_, ResourceLocation p_i49313_2_, BlockPos p_i49313_3_, Rotation p_i49313_4_, int p_i49313_5_) {
         super(0);
         this.field_207615_d = p_i49313_2_;
         BlockPos blockpos = IglooPieces.field_207622_e.get(p_i49313_2_);
         this.templatePosition = p_i49313_3_.add(blockpos.getX(), blockpos.getY() - p_i49313_5_, blockpos.getZ());
         this.field_207616_e = p_i49313_4_;
         this.func_207614_a(p_i49313_1_);
      }

      private void func_207614_a(TemplateManager p_207614_1_) {
         Template template = p_207614_1_.getTemplateDefaulted(this.field_207615_d);
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset(IglooPieces.field_207621_d.get(this.field_207615_d));
         this.setup(template, this.templatePosition, placementsettings);
      }

      /**
       * (abstract) Helper method to write subclass data to NBT
       */
      protected void writeStructureToNBT(NBTTagCompound tagCompound) {
         super.writeStructureToNBT(tagCompound);
         tagCompound.setString("Template", this.field_207615_d.toString());
         tagCompound.setString("Rot", this.field_207616_e.name());
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
         super.readStructureFromNBT(tagCompound, p_143011_2_);
         this.field_207615_d = new ResourceLocation(tagCompound.getString("Template"));
         this.field_207616_e = Rotation.valueOf(tagCompound.getString("Rot"));
         this.func_207614_a(p_143011_2_);
      }

      protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
         if ("chest".equals(function)) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof TileEntityChest) {
               ((TileEntityChest)tileentity).setLootTable(LootTableList.CHESTS_IGLOO_CHEST, rand.nextLong());
            }

         }
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.field_207616_e).setMirror(Mirror.NONE).setCenterOffset(IglooPieces.field_207621_d.get(this.field_207615_d));
         BlockPos blockpos = IglooPieces.field_207622_e.get(this.field_207615_d);
         BlockPos blockpos1 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(3 - blockpos.getX(), 0, 0 - blockpos.getZ())));
         int i = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, blockpos1.getX(), blockpos1.getZ());
         BlockPos blockpos2 = this.templatePosition;
         this.templatePosition = this.templatePosition.add(0, i - 90 - 1, 0);
         boolean flag = super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn, p_74875_4_);
         if (this.field_207615_d.equals(IglooPieces.field_202592_e)) {
            BlockPos blockpos3 = this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(3, 0, 5)));
            IBlockState iblockstate = worldIn.getBlockState(blockpos3.down());
            if (!iblockstate.isAir() && iblockstate.getBlock() != Blocks.LADDER) {
               worldIn.setBlockState(blockpos3, Blocks.SNOW_BLOCK.getDefaultState(), 3);
            }
         }

         this.templatePosition = blockpos2;
         return flag;
      }
   }
}