package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemMap extends ItemMapBase {
   public ItemMap(Item.Properties builder) {
      super(builder);
   }

   public static ItemStack setupNewMap(World p_195952_0_, int p_195952_1_, int p_195952_2_, byte p_195952_3_, boolean p_195952_4_, boolean p_195952_5_) {
      ItemStack itemstack = new ItemStack(Items.FILLED_MAP);
      createMapData(itemstack, p_195952_0_, p_195952_1_, p_195952_2_, p_195952_3_, p_195952_4_, p_195952_5_, p_195952_0_.dimension.getType());
      return itemstack;
   }

   @Nullable
   public static MapData getMapData(ItemStack p_195950_0_, World p_195950_1_) {
       // FORGE: Add instance method for mods to override
       Item map = p_195950_0_.getItem();
       if (map instanceof ItemMap) {
         return ((ItemMap)map).getCustomMapData(p_195950_0_, p_195950_1_);
       }
       return null;
    }

    @Nullable
    protected MapData getCustomMapData(ItemStack p_195950_0_, World p_195950_1_) {
      MapData mapdata = loadMapData(p_195950_1_, "map_" + getMapId(p_195950_0_));
      if (mapdata == null && !p_195950_1_.isRemote) {
         mapdata = createMapData(p_195950_0_, p_195950_1_, p_195950_1_.getWorldInfo().getSpawnX(), p_195950_1_.getWorldInfo().getSpawnZ(), 3, false, false, p_195950_1_.dimension.getType());
      }

      return mapdata;
   }

   public static int getMapId(ItemStack p_195949_0_) {
      NBTTagCompound nbttagcompound = p_195949_0_.getTag();
      return nbttagcompound != null && nbttagcompound.contains("map", 99) ? nbttagcompound.getInt("map") : 0;
   }

   private static MapData createMapData(ItemStack p_195951_0_, World p_195951_1_, int p_195951_2_, int p_195951_3_, int p_195951_4_, boolean p_195951_5_, boolean p_195951_6_, DimensionType p_195951_7_) {
      int i = p_195951_1_.func_212410_a(DimensionType.OVERWORLD, "map");
      MapData mapdata = new MapData("map_" + i);
      mapdata.func_212440_a(p_195951_2_, p_195951_3_, p_195951_4_, p_195951_5_, p_195951_6_, p_195951_7_);
      p_195951_1_.func_212409_a(DimensionType.OVERWORLD, mapdata.getName(), mapdata);
      p_195951_0_.getOrCreateTag().setInt("map", i);
      return mapdata;
   }

   @Nullable
   public static MapData loadMapData(IWorld worldIn, String mapId) {
      return worldIn.func_212411_a(DimensionType.OVERWORLD, MapData::new, mapId);
   }

   public void updateMapData(World worldIn, Entity viewer, MapData data) {
      if (worldIn.dimension.getType() == data.dimension && viewer instanceof EntityPlayer) {
         int i = 1 << data.scale;
         int j = data.xCenter;
         int k = data.zCenter;
         int l = MathHelper.floor(viewer.posX - (double)j) / i + 64;
         int i1 = MathHelper.floor(viewer.posZ - (double)k) / i + 64;
         int j1 = 128 / i;
         if (worldIn.dimension.isNether()) {
            j1 /= 2;
         }

         MapData.MapInfo mapdata$mapinfo = data.getMapInfo((EntityPlayer)viewer);
         ++mapdata$mapinfo.step;
         boolean flag = false;

         for(int k1 = l - j1 + 1; k1 < l + j1; ++k1) {
            if ((k1 & 15) == (mapdata$mapinfo.step & 15) || flag) {
               flag = false;
               double d0 = 0.0D;

               for(int l1 = i1 - j1 - 1; l1 < i1 + j1; ++l1) {
                  if (k1 >= 0 && l1 >= -1 && k1 < 128 && l1 < 128) {
                     int i2 = k1 - l;
                     int j2 = l1 - i1;
                     boolean flag1 = i2 * i2 + j2 * j2 > (j1 - 2) * (j1 - 2);
                     int k2 = (j / i + k1 - 64) * i;
                     int l2 = (k / i + l1 - 64) * i;
                     Multiset<MaterialColor> multiset = LinkedHashMultiset.create();
                     Chunk chunk = worldIn.getChunk(new BlockPos(k2, 0, l2));
                     if (!chunk.isEmpty()) {
                        int i3 = k2 & 15;
                        int j3 = l2 & 15;
                        int k3 = 0;
                        double d1 = 0.0D;
                        if (worldIn.dimension.isNether()) {
                           int l3 = k2 + l2 * 231871;
                           l3 = l3 * l3 * 31287121 + l3 * 11;
                           if ((l3 >> 20 & 1) == 0) {
                              multiset.add(Blocks.DIRT.getDefaultState().getMapColor(worldIn, BlockPos.ORIGIN), 10);
                           } else {
                              multiset.add(Blocks.STONE.getDefaultState().getMapColor(worldIn, BlockPos.ORIGIN), 100);
                           }

                           d1 = 100.0D;
                        } else {
                           BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

                           for(int i4 = 0; i4 < i; ++i4) {
                              for(int j4 = 0; j4 < i; ++j4) {
                                 int k4 = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, i4 + i3, j4 + j3) + 1;
                                 IBlockState iblockstate;
                                 if (k4 <= 1) {
                                    iblockstate = Blocks.BEDROCK.getDefaultState();
                                 } else {
                                    while(true) {
                                       --k4;
                                       iblockstate = chunk.getBlockState(i4 + i3, k4, j4 + j3);
                                       blockpos$mutableblockpos.setPos((chunk.x << 4) + i4 + i3, k4, (chunk.z << 4) + j4 + j3);
                                       if (iblockstate.getMapColor(worldIn, blockpos$mutableblockpos) != MaterialColor.AIR || k4 <= 0) {
                                          break;
                                       }
                                    }

                                    if (k4 > 0 && !iblockstate.getFluidState().isEmpty()) {
                                       int l4 = k4 - 1;

                                       while(true) {
                                          IBlockState iblockstate1 = chunk.getBlockState(i4 + i3, l4--, j4 + j3);
                                          ++k3;
                                          if (l4 <= 0 || iblockstate1.getFluidState().isEmpty()) {
                                             break;
                                          }
                                       }

                                       iblockstate = this.func_211698_a(worldIn, iblockstate, blockpos$mutableblockpos);
                                    }
                                 }

                                 data.removeStaleBanners(worldIn, (chunk.x << 4) + i4 + i3, (chunk.z << 4) + j4 + j3);
                                 d1 += (double)k4 / (double)(i * i);
                                 multiset.add(iblockstate.getMapColor(worldIn, blockpos$mutableblockpos));
                              }
                           }
                        }

                        k3 = k3 / (i * i);
                        double d2 = (d1 - d0) * 4.0D / (double)(i + 4) + ((double)(k1 + l1 & 1) - 0.5D) * 0.4D;
                        int i5 = 1;
                        if (d2 > 0.6D) {
                           i5 = 2;
                        }

                        if (d2 < -0.6D) {
                           i5 = 0;
                        }

                        MaterialColor materialcolor = Iterables.getFirst(Multisets.copyHighestCountFirst(multiset), MaterialColor.AIR);
                        if (materialcolor == MaterialColor.WATER) {
                           d2 = (double)k3 * 0.1D + (double)(k1 + l1 & 1) * 0.2D;
                           i5 = 1;
                           if (d2 < 0.5D) {
                              i5 = 2;
                           }

                           if (d2 > 0.9D) {
                              i5 = 0;
                           }
                        }

                        d0 = d1;
                        if (l1 >= 0 && i2 * i2 + j2 * j2 < j1 * j1 && (!flag1 || (k1 + l1 & 1) != 0)) {
                           byte b0 = data.colors[k1 + l1 * 128];
                           byte b1 = (byte)(materialcolor.colorIndex * 4 + i5);
                           if (b0 != b1) {
                              data.colors[k1 + l1 * 128] = b1;
                              data.updateMapData(k1, l1);
                              flag = true;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private IBlockState func_211698_a(World p_211698_1_, IBlockState p_211698_2_, BlockPos p_211698_3_) {
      IFluidState ifluidstate = p_211698_2_.getFluidState();
      return !ifluidstate.isEmpty() && !Block.doesSideFillSquare(p_211698_2_.getCollisionShape(p_211698_1_, p_211698_3_), EnumFacing.UP) ? ifluidstate.getBlockState() : p_211698_2_;
   }

   private static boolean func_195954_a(Biome[] p_195954_0_, int p_195954_1_, int p_195954_2_, int p_195954_3_) {
      return p_195954_0_[p_195954_2_ * p_195954_1_ + p_195954_3_ * p_195954_1_ * 128 * p_195954_1_].getDepth() >= 0.0F;
   }

   /**
    * Draws ambiguous landmasses representing unexplored terrain onto a treasure map
    */
   public static void renderBiomePreviewMap(World worldIn, ItemStack map) {
      MapData mapdata = getMapData(map, worldIn);
      if (mapdata != null) {
         if (worldIn.dimension.getType() == mapdata.dimension) {
            int i = 1 << mapdata.scale;
            int j = mapdata.xCenter;
            int k = mapdata.zCenter;
            Biome[] abiome = worldIn.getChunkProvider().getChunkGenerator().getBiomeProvider().getBiomes((j / i - 64) * i, (k / i - 64) * i, 128 * i, 128 * i, false);

            for(int l = 0; l < 128; ++l) {
               for(int i1 = 0; i1 < 128; ++i1) {
                  if (l > 0 && i1 > 0 && l < 127 && i1 < 127) {
                     Biome biome = abiome[l * i + i1 * i * 128 * i];
                     int j1 = 8;
                     if (func_195954_a(abiome, i, l - 1, i1 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l - 1, i1 + 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l - 1, i1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l + 1, i1 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l + 1, i1 + 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l + 1, i1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l, i1 - 1)) {
                        --j1;
                     }

                     if (func_195954_a(abiome, i, l, i1 + 1)) {
                        --j1;
                     }

                     int k1 = 3;
                     MaterialColor materialcolor = MaterialColor.AIR;
                     if (biome.getDepth() < 0.0F) {
                        materialcolor = MaterialColor.ADOBE;
                        if (j1 > 7 && i1 % 2 == 0) {
                           k1 = (l + (int)(MathHelper.sin((float)i1 + 0.0F) * 7.0F)) / 8 % 5;
                           if (k1 == 3) {
                              k1 = 1;
                           } else if (k1 == 4) {
                              k1 = 0;
                           }
                        } else if (j1 > 7) {
                           materialcolor = MaterialColor.AIR;
                        } else if (j1 > 5) {
                           k1 = 1;
                        } else if (j1 > 3) {
                           k1 = 0;
                        } else if (j1 > 1) {
                           k1 = 0;
                        }
                     } else if (j1 > 0) {
                        materialcolor = MaterialColor.BROWN;
                        if (j1 > 3) {
                           k1 = 1;
                        } else {
                           k1 = 3;
                        }
                     }

                     if (materialcolor != MaterialColor.AIR) {
                        mapdata.colors[l + i1 * 128] = (byte)(materialcolor.colorIndex * 4 + k1);
                        mapdata.updateMapData(l, i1);
                     }
                  }
               }
            }

         }
      }
   }

   /**
    * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
    * update it's contents.
    */
   public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote) {
         MapData mapdata = getMapData(stack, worldIn);
         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityIn;
            mapdata.updateVisiblePlayers(entityplayer, stack);
         }

         if (isSelected || entityIn instanceof EntityPlayer && ((EntityPlayer)entityIn).getHeldItemOffhand() == stack) {
            this.updateMapData(worldIn, entityIn, mapdata);
         }

      }
   }

   @Nullable
   public Packet<?> getUpdatePacket(ItemStack stack, World worldIn, EntityPlayer player) {
      return getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
   }

   /**
    * Called when item is crafted/smelted. Used only by maps so far.
    */
   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      NBTTagCompound nbttagcompound = stack.getTag();
      if (nbttagcompound != null && nbttagcompound.contains("map_scale_direction", 99)) {
         scaleMap(stack, worldIn, nbttagcompound.getInt("map_scale_direction"));
         nbttagcompound.removeTag("map_scale_direction");
      }

   }

   protected static void scaleMap(ItemStack p_185063_0_, World p_185063_1_, int p_185063_2_) {
      MapData mapdata = getMapData(p_185063_0_, p_185063_1_);
      if (mapdata != null) {
         createMapData(p_185063_0_, p_185063_1_, mapdata.xCenter, mapdata.zCenter, MathHelper.clamp(mapdata.scale + p_185063_2_, 0, 4), mapdata.trackingPosition, mapdata.unlimitedTracking, mapdata.dimension);
      }

   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      if (flagIn.isAdvanced()) {
         MapData mapdata = worldIn == null ? null : getMapData(stack, worldIn);
         if (mapdata != null) {
            tooltip.add((new TextComponentTranslation("filled_map.id", getMapId(stack))).applyTextStyle(TextFormatting.GRAY));
            tooltip.add((new TextComponentTranslation("filled_map.scale", 1 << mapdata.scale)).applyTextStyle(TextFormatting.GRAY));
            tooltip.add((new TextComponentTranslation("filled_map.level", mapdata.scale, 4)).applyTextStyle(TextFormatting.GRAY));
         } else {
            tooltip.add((new TextComponentTranslation("filled_map.unknown")).applyTextStyle(TextFormatting.GRAY));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static int getColor(ItemStack p_190907_0_) {
      NBTTagCompound nbttagcompound = p_190907_0_.getChildTag("display");
      if (nbttagcompound != null && nbttagcompound.contains("MapColor", 99)) {
         int i = nbttagcompound.getInt("MapColor");
         return -16777216 | i & 16777215;
      } else {
         return -12173266;
      }
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      IBlockState iblockstate = p_195939_1_.getWorld().getBlockState(p_195939_1_.getPos());
      if (iblockstate.isIn(BlockTags.BANNERS)) {
         if (!p_195939_1_.world.isRemote) {
            MapData mapdata = getMapData(p_195939_1_.getItem(), p_195939_1_.getWorld());
            mapdata.tryAddBanner(p_195939_1_.getWorld(), p_195939_1_.getPos());
         }

         return EnumActionResult.SUCCESS;
      } else {
         return super.onItemUse(p_195939_1_);
      }
   }
}