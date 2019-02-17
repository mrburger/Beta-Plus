package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePartBitSet;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Template {
   private final List<List<Template.BlockInfo>> blocks = Lists.newArrayList();
   /** entities in the structure */
   private final List<Template.EntityInfo> entities = Lists.newArrayList();
   /** size of the structure */
   private BlockPos size = BlockPos.ORIGIN;
   /** The author of this template. */
   private String author = "?";

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String authorIn) {
      this.author = authorIn;
   }

   public String getAuthor() {
      return this.author;
   }

   /**
    * takes blocks from the world and puts the data them into this template
    */
   public void takeBlocksFromWorld(World worldIn, BlockPos startPos, BlockPos size, boolean takeEntities, @Nullable Block toIgnore) {
      if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
         BlockPos blockpos = startPos.add(size).add(-1, -1, -1);
         List<Template.BlockInfo> list = Lists.newArrayList();
         List<Template.BlockInfo> list1 = Lists.newArrayList();
         List<Template.BlockInfo> list2 = Lists.newArrayList();
         BlockPos blockpos1 = new BlockPos(Math.min(startPos.getX(), blockpos.getX()), Math.min(startPos.getY(), blockpos.getY()), Math.min(startPos.getZ(), blockpos.getZ()));
         BlockPos blockpos2 = new BlockPos(Math.max(startPos.getX(), blockpos.getX()), Math.max(startPos.getY(), blockpos.getY()), Math.max(startPos.getZ(), blockpos.getZ()));
         this.size = size;

         for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos1, blockpos2)) {
            BlockPos blockpos3 = blockpos$mutableblockpos.subtract(blockpos1);
            IBlockState iblockstate = worldIn.getBlockState(blockpos$mutableblockpos);
            if (toIgnore == null || toIgnore != iblockstate.getBlock()) {
               TileEntity tileentity = worldIn.getTileEntity(blockpos$mutableblockpos);
               if (tileentity != null) {
                  NBTTagCompound nbttagcompound = tileentity.write(new NBTTagCompound());
                  nbttagcompound.removeTag("x");
                  nbttagcompound.removeTag("y");
                  nbttagcompound.removeTag("z");
                  list1.add(new Template.BlockInfo(blockpos3, iblockstate, nbttagcompound));
               } else if (!iblockstate.isOpaqueCube(worldIn, blockpos$mutableblockpos) && !iblockstate.isFullCube()) {
                  list2.add(new Template.BlockInfo(blockpos3, iblockstate, (NBTTagCompound)null));
               } else {
                  list.add(new Template.BlockInfo(blockpos3, iblockstate, (NBTTagCompound)null));
               }
            }
         }

         List<Template.BlockInfo> list3 = Lists.newArrayList();
         list3.addAll(list);
         list3.addAll(list1);
         list3.addAll(list2);
         this.blocks.clear();
         this.blocks.add(list3);
         if (takeEntities) {
            this.takeEntitiesFromWorld(worldIn, blockpos1, blockpos2.add(1, 1, 1));
         } else {
            this.entities.clear();
         }

      }
   }

   /**
    * takes blocks from the world and puts the data them into this template
    */
   private void takeEntitiesFromWorld(World worldIn, BlockPos startPos, BlockPos endPos) {
      List<Entity> list = worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(startPos, endPos), (p_201048_0_) -> {
         return !(p_201048_0_ instanceof EntityPlayer);
      });
      this.entities.clear();

      for(Entity entity : list) {
         Vec3d vec3d = new Vec3d(entity.posX - (double)startPos.getX(), entity.posY - (double)startPos.getY(), entity.posZ - (double)startPos.getZ());
         NBTTagCompound nbttagcompound = new NBTTagCompound();
         entity.writeUnlessPassenger(nbttagcompound);
         BlockPos blockpos;
         if (entity instanceof EntityPainting) {
            blockpos = ((EntityPainting)entity).getHangingPosition().subtract(startPos);
         } else {
            blockpos = new BlockPos(vec3d);
         }

         this.entities.add(new Template.EntityInfo(vec3d, blockpos, nbttagcompound));
      }

   }

   public Map<BlockPos, String> getDataBlocks(BlockPos pos, PlacementSettings placementIn) {
      Map<BlockPos, String> map = Maps.newHashMap();
      MutableBoundingBox mutableboundingbox = placementIn.getBoundingBox();

      for(Template.BlockInfo template$blockinfo : placementIn.func_204764_a(this.blocks, pos)) {
         BlockPos blockpos = transformedBlockPos(placementIn, template$blockinfo.pos).add(pos);
         if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos)) {
            IBlockState iblockstate = template$blockinfo.blockState;
            if (iblockstate.getBlock() == Blocks.STRUCTURE_BLOCK && template$blockinfo.tileentityData != null) {
               StructureMode structuremode = StructureMode.valueOf(template$blockinfo.tileentityData.getString("mode"));
               if (structuremode == StructureMode.DATA) {
                  map.put(blockpos, template$blockinfo.tileentityData.getString("metadata"));
               }
            }
         }
      }

      return map;
   }

   public BlockPos calculateConnectedPos(PlacementSettings placementIn, BlockPos p_186262_2_, PlacementSettings p_186262_3_, BlockPos p_186262_4_) {
      BlockPos blockpos = transformedBlockPos(placementIn, p_186262_2_);
      BlockPos blockpos1 = transformedBlockPos(p_186262_3_, p_186262_4_);
      return blockpos.subtract(blockpos1);
   }

   public static BlockPos transformedBlockPos(PlacementSettings placementIn, BlockPos pos) {
      return getTransformedPos(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.func_207664_d());
   }

   /**
    * Add blocks and entities from this structure to the given world, restricting placement to within the chunk bounding
    * box.
    *  
    * @see PlacementSettings#setBoundingBoxFromChunk
    */
   public void addBlocksToWorldChunk(IWorld worldIn, BlockPos pos, PlacementSettings placementIn) {
      placementIn.setBoundingBoxFromChunk();
      this.addBlocksToWorld(worldIn, pos, placementIn);
   }

   /**
    * This takes the data stored in this instance and puts them into the world.
    */
   public void addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn) {
      this.addBlocksToWorld(worldIn, pos, new IntegrityProcessor(pos, placementIn), placementIn, 2);
   }

   /**
    * Adds blocks and entities from this structure to the given world.
    */
   public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, PlacementSettings placementIn, int flags) {
      return this.addBlocksToWorld(worldIn, pos, new IntegrityProcessor(pos, placementIn), placementIn, flags);
   }

   /**
    * Adds blocks and entities from this structure to the given world.
    */
   public boolean addBlocksToWorld(IWorld worldIn, BlockPos pos, @Nullable ITemplateProcessor templateProcessor, PlacementSettings placementIn, int flags) {
      if (this.blocks.isEmpty()) {
         return false;
      } else {
         List<Template.BlockInfo> list = placementIn.func_204764_a(this.blocks, pos);
         if ((!list.isEmpty() || !placementIn.getIgnoreEntities() && !this.entities.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            Block block = placementIn.getReplacedBlock();
            MutableBoundingBox mutableboundingbox = placementIn.getBoundingBox();
            List<BlockPos> list1 = Lists.newArrayListWithCapacity(placementIn.func_204763_l() ? list.size() : 0);
            List<Pair<BlockPos, NBTTagCompound>> list2 = Lists.newArrayListWithCapacity(list.size());
            int i = Integer.MAX_VALUE;
            int j = Integer.MAX_VALUE;
            int k = Integer.MAX_VALUE;
            int l = Integer.MIN_VALUE;
            int i1 = Integer.MIN_VALUE;
            int j1 = Integer.MIN_VALUE;

            for(Template.BlockInfo template$blockinfo : list) {
               BlockPos blockpos = transformedBlockPos(placementIn, template$blockinfo.pos).add(pos);
               // Forge: skip processing blocks outside BB to prevent cascading worldgen issues
               if (mutableboundingbox != null && !mutableboundingbox.isVecInside(blockpos)) continue;
               Template.BlockInfo template$blockinfo1 = templateProcessor != null ? templateProcessor.processBlock(worldIn, blockpos, template$blockinfo) : template$blockinfo;
               if (template$blockinfo1 != null) {
                  Block block1 = template$blockinfo1.blockState.getBlock();
                  if ((block == null || block != block1) && (!placementIn.getIgnoreStructureBlock() || block1 != Blocks.STRUCTURE_BLOCK) && (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos))) {
                     IFluidState ifluidstate = placementIn.func_204763_l() ? worldIn.getFluidState(blockpos) : null;
                     IBlockState iblockstate = template$blockinfo1.blockState.mirror(placementIn.getMirror());
                     IBlockState iblockstate1 = iblockstate.rotate(placementIn.getRotation());
                     if (template$blockinfo1.tileentityData != null) {
                        TileEntity tileentity = worldIn.getTileEntity(blockpos);
                        if (tileentity instanceof IInventory) {
                           ((IInventory)tileentity).clear();
                        }

                        worldIn.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 4);
                     }

                     if (worldIn.setBlockState(blockpos, iblockstate1, flags)) {
                        i = Math.min(i, blockpos.getX());
                        j = Math.min(j, blockpos.getY());
                        k = Math.min(k, blockpos.getZ());
                        l = Math.max(l, blockpos.getX());
                        i1 = Math.max(i1, blockpos.getY());
                        j1 = Math.max(j1, blockpos.getZ());
                        list2.add(Pair.of(blockpos, template$blockinfo.tileentityData));
                        if (template$blockinfo1.tileentityData != null) {
                           TileEntity tileentity2 = worldIn.getTileEntity(blockpos);
                           if (tileentity2 != null) {
                              template$blockinfo1.tileentityData.setInt("x", blockpos.getX());
                              template$blockinfo1.tileentityData.setInt("y", blockpos.getY());
                              template$blockinfo1.tileentityData.setInt("z", blockpos.getZ());
                              tileentity2.read(template$blockinfo1.tileentityData);
                              tileentity2.mirror(placementIn.getMirror());
                              tileentity2.rotate(placementIn.getRotation());
                           }
                        }

                        if (ifluidstate != null && iblockstate1.getBlock() instanceof ILiquidContainer) {
                           ((ILiquidContainer)iblockstate1.getBlock()).receiveFluid(worldIn, blockpos, iblockstate1, ifluidstate);
                           if (!ifluidstate.isSource()) {
                              list1.add(blockpos);
                           }
                        }
                     }
                  }
               }
            }

            boolean flag = true;
            EnumFacing[] aenumfacing = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};

            while(flag && !list1.isEmpty()) {
               flag = false;
               Iterator<BlockPos> iterator = list1.iterator();

               while(iterator.hasNext()) {
                  BlockPos blockpos1 = iterator.next();
                  IFluidState ifluidstate1 = worldIn.getFluidState(blockpos1);

                  for(int i2 = 0; i2 < aenumfacing.length && !ifluidstate1.isSource(); ++i2) {
                     IFluidState ifluidstate2 = worldIn.getFluidState(blockpos1.offset(aenumfacing[i2]));
                     if (ifluidstate2.getHeight() > ifluidstate1.getHeight() || ifluidstate2.isSource() && !ifluidstate1.isSource()) {
                        ifluidstate1 = ifluidstate2;
                     }
                  }

                  if (ifluidstate1.isSource()) {
                     IBlockState iblockstate4 = worldIn.getBlockState(blockpos1);
                     if (iblockstate4.getBlock() instanceof ILiquidContainer) {
                        ((ILiquidContainer)iblockstate4.getBlock()).receiveFluid(worldIn, blockpos1, iblockstate4, ifluidstate1);
                        flag = true;
                        iterator.remove();
                     }
                  }
               }
            }

            if (i <= l) {
               VoxelShapePart voxelshapepart = new VoxelShapePartBitSet(l - i + 1, i1 - j + 1, j1 - k + 1);
               int k1 = i;
               int l1 = j;
               int j2 = k;

               for(Pair<BlockPos, NBTTagCompound> pair : list2) {
                  BlockPos blockpos2 = pair.getFirst();
                  voxelshapepart.setFilled(blockpos2.getX() - k1, blockpos2.getY() - l1, blockpos2.getZ() - j2, true, true);
               }

               voxelshapepart.forEachFace((p_211754_5_, p_211754_6_, p_211754_7_, p_211754_8_) -> {
                  BlockPos blockpos4 = new BlockPos(k1 + p_211754_6_, l1 + p_211754_7_, j2 + p_211754_8_);
                  BlockPos blockpos5 = blockpos4.offset(p_211754_5_);
                  IBlockState iblockstate5 = worldIn.getBlockState(blockpos4);
                  IBlockState iblockstate6 = worldIn.getBlockState(blockpos5);
                  IBlockState iblockstate7 = iblockstate5.updatePostPlacement(p_211754_5_, iblockstate6, worldIn, blockpos4, blockpos5);
                  if (iblockstate5 != iblockstate7) {
                     worldIn.setBlockState(blockpos4, iblockstate7, flags & -2 | 16);
                  }

                  IBlockState iblockstate8 = iblockstate6.updatePostPlacement(p_211754_5_.getOpposite(), iblockstate7, worldIn, blockpos5, blockpos4);
                  if (iblockstate6 != iblockstate8) {
                     worldIn.setBlockState(blockpos5, iblockstate8, flags & -2 | 16);
                  }

               });

               for(Pair<BlockPos, NBTTagCompound> pair1 : list2) {
                  BlockPos blockpos3 = pair1.getFirst();
                  IBlockState iblockstate2 = worldIn.getBlockState(blockpos3);
                  IBlockState iblockstate3 = Block.getValidBlockForPosition(iblockstate2, worldIn, blockpos3);
                  if (iblockstate2 != iblockstate3) {
                     worldIn.setBlockState(blockpos3, iblockstate3, flags & -2 | 16);
                  }

                  worldIn.notifyNeighbors(blockpos3, iblockstate3.getBlock());
                  if (pair1.getSecond() != null) {
                     TileEntity tileentity1 = worldIn.getTileEntity(blockpos3);
                     if (tileentity1 != null) {
                        tileentity1.markDirty();
                     }
                  }
               }
            }

            if (!placementIn.getIgnoreEntities()) {
               this.func_207668_a(worldIn, pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.func_207664_d(), mutableboundingbox);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void func_207668_a(IWorld p_207668_1_, BlockPos p_207668_2_, Mirror p_207668_3_, Rotation p_207668_4_, BlockPos p_207668_5_, @Nullable MutableBoundingBox p_207668_6_) {
      for(Template.EntityInfo template$entityinfo : this.entities) {
         BlockPos blockpos = getTransformedPos(template$entityinfo.blockPos, p_207668_3_, p_207668_4_, p_207668_5_).add(p_207668_2_);
         if (p_207668_6_ == null || p_207668_6_.isVecInside(blockpos)) {
            NBTTagCompound nbttagcompound = template$entityinfo.entityData;
            Vec3d vec3d = getTransformedPos(template$entityinfo.pos, p_207668_3_, p_207668_4_, p_207668_5_);
            Vec3d vec3d1 = vec3d.add((double)p_207668_2_.getX(), (double)p_207668_2_.getY(), (double)p_207668_2_.getZ());
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.add((INBTBase)(new NBTTagDouble(vec3d1.x)));
            nbttaglist.add((INBTBase)(new NBTTagDouble(vec3d1.y)));
            nbttaglist.add((INBTBase)(new NBTTagDouble(vec3d1.z)));
            nbttagcompound.setTag("Pos", nbttaglist);
            nbttagcompound.setUniqueId("UUID", UUID.randomUUID());

            Entity entity;
            try {
               entity = EntityType.create(nbttagcompound, p_207668_1_.getWorld());
            } catch (Exception var16) {
               entity = null;
            }

            if (entity != null) {
               float f = entity.getMirroredYaw(p_207668_3_);
               f = f + (entity.rotationYaw - entity.getRotatedYaw(p_207668_4_));
               entity.setLocationAndAngles(vec3d1.x, vec3d1.y, vec3d1.z, f, entity.rotationPitch);
               p_207668_1_.spawnEntity(entity);
            }
         }
      }

   }

   public BlockPos transformedSize(Rotation rotationIn) {
      switch(rotationIn) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos getTransformedPos(BlockPos targetPos, Mirror mirrorIn, Rotation rotationIn, BlockPos offset) {
      int i = targetPos.getX();
      int j = targetPos.getY();
      int k = targetPos.getZ();
      boolean flag = true;
      switch(mirrorIn) {
      case LEFT_RIGHT:
         k = -k;
         break;
      case FRONT_BACK:
         i = -i;
         break;
      default:
         flag = false;
      }

      int l = offset.getX();
      int i1 = offset.getZ();
      switch(rotationIn) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(l - i1 + k, j, l + i1 - i);
      case CLOCKWISE_90:
         return new BlockPos(l + i1 - k, j, i1 - l + i);
      case CLOCKWISE_180:
         return new BlockPos(l + l - i, j, i1 + i1 - k);
      default:
         return flag ? new BlockPos(i, j, k) : targetPos;
      }
   }

   private static Vec3d getTransformedPos(Vec3d target, Mirror mirrorIn, Rotation rotationIn, BlockPos centerOffset) {
      double d0 = target.x;
      double d1 = target.y;
      double d2 = target.z;
      boolean flag = true;
      switch(mirrorIn) {
      case LEFT_RIGHT:
         d2 = 1.0D - d2;
         break;
      case FRONT_BACK:
         d0 = 1.0D - d0;
         break;
      default:
         flag = false;
      }

      int i = centerOffset.getX();
      int j = centerOffset.getZ();
      switch(rotationIn) {
      case COUNTERCLOCKWISE_90:
         return new Vec3d((double)(i - j) + d2, d1, (double)(i + j + 1) - d0);
      case CLOCKWISE_90:
         return new Vec3d((double)(i + j + 1) - d2, d1, (double)(j - i) + d0);
      case CLOCKWISE_180:
         return new Vec3d((double)(i + i + 1) - d0, d1, (double)(j + j + 1) - d2);
      default:
         return flag ? new Vec3d(d0, d1, d2) : target;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos p_189961_1_, Mirror p_189961_2_, Rotation p_189961_3_) {
      return getZeroPositionWithTransform(p_189961_1_, p_189961_2_, p_189961_3_, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos p_191157_0_, Mirror p_191157_1_, Rotation p_191157_2_, int p_191157_3_, int p_191157_4_) {
      --p_191157_3_;
      --p_191157_4_;
      int i = p_191157_1_ == Mirror.FRONT_BACK ? p_191157_3_ : 0;
      int j = p_191157_1_ == Mirror.LEFT_RIGHT ? p_191157_4_ : 0;
      BlockPos blockpos = p_191157_0_;
      switch(p_191157_2_) {
      case COUNTERCLOCKWISE_90:
         blockpos = p_191157_0_.add(j, 0, p_191157_3_ - i);
         break;
      case CLOCKWISE_90:
         blockpos = p_191157_0_.add(p_191157_4_ - j, 0, i);
         break;
      case CLOCKWISE_180:
         blockpos = p_191157_0_.add(p_191157_3_ - i, 0, p_191157_4_ - j);
         break;
      case NONE:
         blockpos = p_191157_0_.add(i, 0, j);
      }

      return blockpos;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      if (this.blocks.isEmpty()) {
         nbt.setTag("blocks", new NBTTagList());
         nbt.setTag("palette", new NBTTagList());
      } else {
         List<Template.BasicPalette> list = Lists.newArrayList();
         Template.BasicPalette template$basicpalette = new Template.BasicPalette();
         list.add(template$basicpalette);

         for(int i = 1; i < this.blocks.size(); ++i) {
            list.add(new Template.BasicPalette());
         }

         NBTTagList nbttaglist1 = new NBTTagList();
         List<Template.BlockInfo> list1 = this.blocks.get(0);

         for(int j = 0; j < list1.size(); ++j) {
            Template.BlockInfo template$blockinfo = list1.get(j);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("pos", this.writeInts(template$blockinfo.pos.getX(), template$blockinfo.pos.getY(), template$blockinfo.pos.getZ()));
            int k = template$basicpalette.idFor(template$blockinfo.blockState);
            nbttagcompound.setInt("state", k);
            if (template$blockinfo.tileentityData != null) {
               nbttagcompound.setTag("nbt", template$blockinfo.tileentityData);
            }

            nbttaglist1.add((INBTBase)nbttagcompound);

            for(int l = 1; l < this.blocks.size(); ++l) {
               Template.BasicPalette template$basicpalette1 = list.get(l);
               template$basicpalette1.addMapping((this.blocks.get(j).get(j)).blockState, k);
            }
         }

         nbt.setTag("blocks", nbttaglist1);
         if (list.size() == 1) {
            NBTTagList nbttaglist2 = new NBTTagList();

            for(IBlockState iblockstate : template$basicpalette) {
               nbttaglist2.add((INBTBase)NBTUtil.writeBlockState(iblockstate));
            }

            nbt.setTag("palette", nbttaglist2);
         } else {
            NBTTagList nbttaglist3 = new NBTTagList();

            for(Template.BasicPalette template$basicpalette2 : list) {
               NBTTagList nbttaglist4 = new NBTTagList();

               for(IBlockState iblockstate1 : template$basicpalette2) {
                  nbttaglist4.add((INBTBase)NBTUtil.writeBlockState(iblockstate1));
               }

               nbttaglist3.add((INBTBase)nbttaglist4);
            }

            nbt.setTag("palettes", nbttaglist3);
         }
      }

      NBTTagList nbttaglist = new NBTTagList();

      for(Template.EntityInfo template$entityinfo : this.entities) {
         NBTTagCompound nbttagcompound1 = new NBTTagCompound();
         nbttagcompound1.setTag("pos", this.writeDoubles(template$entityinfo.pos.x, template$entityinfo.pos.y, template$entityinfo.pos.z));
         nbttagcompound1.setTag("blockPos", this.writeInts(template$entityinfo.blockPos.getX(), template$entityinfo.blockPos.getY(), template$entityinfo.blockPos.getZ()));
         if (template$entityinfo.entityData != null) {
            nbttagcompound1.setTag("nbt", template$entityinfo.entityData);
         }

         nbttaglist.add((INBTBase)nbttagcompound1);
      }

      nbt.setTag("entities", nbttaglist);
      nbt.setTag("size", this.writeInts(this.size.getX(), this.size.getY(), this.size.getZ()));
      nbt.setInt("DataVersion", 1631);
      return nbt;
   }

   public void read(NBTTagCompound compound) {
      this.blocks.clear();
      this.entities.clear();
      NBTTagList nbttaglist = compound.getList("size", 3);
      this.size = new BlockPos(nbttaglist.getInt(0), nbttaglist.getInt(1), nbttaglist.getInt(2));
      NBTTagList nbttaglist1 = compound.getList("blocks", 10);
      if (compound.contains("palettes", 9)) {
         NBTTagList nbttaglist2 = compound.getList("palettes", 9);

         for(int i = 0; i < nbttaglist2.size(); ++i) {
            this.func_204768_a(nbttaglist2.getList(i), nbttaglist1);
         }
      } else {
         this.func_204768_a(compound.getList("palette", 10), nbttaglist1);
      }

      NBTTagList nbttaglist5 = compound.getList("entities", 10);

      for(int j = 0; j < nbttaglist5.size(); ++j) {
         NBTTagCompound nbttagcompound = nbttaglist5.getCompound(j);
         NBTTagList nbttaglist3 = nbttagcompound.getList("pos", 6);
         Vec3d vec3d = new Vec3d(nbttaglist3.getDouble(0), nbttaglist3.getDouble(1), nbttaglist3.getDouble(2));
         NBTTagList nbttaglist4 = nbttagcompound.getList("blockPos", 3);
         BlockPos blockpos = new BlockPos(nbttaglist4.getInt(0), nbttaglist4.getInt(1), nbttaglist4.getInt(2));
         if (nbttagcompound.hasKey("nbt")) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("nbt");
            this.entities.add(new Template.EntityInfo(vec3d, blockpos, nbttagcompound1));
         }
      }

   }

   private void func_204768_a(NBTTagList p_204768_1_, NBTTagList p_204768_2_) {
      Template.BasicPalette template$basicpalette = new Template.BasicPalette();
      List<Template.BlockInfo> list = Lists.newArrayList();

      for(int i = 0; i < p_204768_1_.size(); ++i) {
         template$basicpalette.addMapping(NBTUtil.readBlockState(p_204768_1_.getCompound(i)), i);
      }

      for(int j = 0; j < p_204768_2_.size(); ++j) {
         NBTTagCompound nbttagcompound = p_204768_2_.getCompound(j);
         NBTTagList nbttaglist = nbttagcompound.getList("pos", 3);
         BlockPos blockpos = new BlockPos(nbttaglist.getInt(0), nbttaglist.getInt(1), nbttaglist.getInt(2));
         IBlockState iblockstate = template$basicpalette.stateFor(nbttagcompound.getInt("state"));
         NBTTagCompound nbttagcompound1;
         if (nbttagcompound.hasKey("nbt")) {
            nbttagcompound1 = nbttagcompound.getCompound("nbt");
         } else {
            nbttagcompound1 = null;
         }

         list.add(new Template.BlockInfo(blockpos, iblockstate, nbttagcompound1));
      }

      this.blocks.add(list);
   }

   private NBTTagList writeInts(int... values) {
      NBTTagList nbttaglist = new NBTTagList();

      for(int i : values) {
         nbttaglist.add((INBTBase)(new NBTTagInt(i)));
      }

      return nbttaglist;
   }

   private NBTTagList writeDoubles(double... values) {
      NBTTagList nbttaglist = new NBTTagList();

      for(double d0 : values) {
         nbttaglist.add((INBTBase)(new NBTTagDouble(d0)));
      }

      return nbttaglist;
   }

   static class BasicPalette implements Iterable<IBlockState> {
      public static final IBlockState DEFAULT_BLOCK_STATE = Blocks.AIR.getDefaultState();
      private final ObjectIntIdentityMap<IBlockState> ids = new ObjectIntIdentityMap<>(16);
      private int lastId;

      private BasicPalette() {
      }

      public int idFor(IBlockState state) {
         int i = this.ids.get(state);
         if (i == -1) {
            i = this.lastId++;
            this.ids.put(state, i);
         }

         return i;
      }

      @Nullable
      public IBlockState stateFor(int id) {
         IBlockState iblockstate = this.ids.getByValue(id);
         return iblockstate == null ? DEFAULT_BLOCK_STATE : iblockstate;
      }

      public Iterator<IBlockState> iterator() {
         return this.ids.iterator();
      }

      public void addMapping(IBlockState p_189956_1_, int p_189956_2_) {
         this.ids.put(p_189956_1_, p_189956_2_);
      }
   }

   public static class BlockInfo {
      /** the position the block is to be generated to */
      public final BlockPos pos;
      /** The type of block in this particular spot in the structure. */
      public final IBlockState blockState;
      /** NBT data for the tileentity */
      public final NBTTagCompound tileentityData;

      public BlockInfo(BlockPos posIn, IBlockState stateIn, @Nullable NBTTagCompound compoundIn) {
         this.pos = posIn;
         this.blockState = stateIn;
         this.tileentityData = compoundIn;
      }
   }

   public static class EntityInfo {
      /** the position the entity is will be generated to */
      public final Vec3d pos;
      /** Block position this entity is counted towards, for structure bounding box checks */
      public final BlockPos blockPos;
      /** the serialized NBT data of the entity in the structure */
      public final NBTTagCompound entityData;

      public EntityInfo(Vec3d vecIn, BlockPos posIn, NBTTagCompound compoundIn) {
         this.pos = vecIn;
         this.blockPos = posIn;
         this.entityData = compoundIn;
      }
   }
}