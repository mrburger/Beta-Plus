package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   protected BlockWorkbench(Block.Properties builder) {
      super(builder);
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         player.displayGui(new BlockWorkbench.InterfaceCraftingTable(worldIn, pos));
         player.addStat(StatList.INTERACT_WITH_CRAFTING_TABLE);
         return true;
      }
   }

   public static class InterfaceCraftingTable implements IInteractionObject {
      private final World world;
      private final BlockPos position;

      public InterfaceCraftingTable(World worldIn, BlockPos pos) {
         this.world = worldIn;
         this.position = pos;
      }

      public ITextComponent getName() {
         return new TextComponentTranslation(Blocks.CRAFTING_TABLE.getTranslationKey() + ".name");
      }

      public boolean hasCustomName() {
         return false;
      }

      @Nullable
      public ITextComponent getCustomName() {
         return null;
      }

      public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
         return new ContainerWorkbench(playerInventory, this.world, this.position);
      }

      public String getGuiID() {
         return "minecraft:crafting_table";
      }
   }
}