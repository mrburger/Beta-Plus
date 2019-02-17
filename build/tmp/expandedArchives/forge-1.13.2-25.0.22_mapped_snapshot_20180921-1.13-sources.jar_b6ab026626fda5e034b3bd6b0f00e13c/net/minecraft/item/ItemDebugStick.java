package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDebugStick extends Item {
   public ItemDebugStick(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public boolean canPlayerBreakBlockWhileHolding(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player) {
      if (!worldIn.isRemote) {
         this.handleClick(player, state, worldIn, pos, false, player.getHeldItem(EnumHand.MAIN_HAND));
      }

      return false;
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      EntityPlayer entityplayer = p_195939_1_.getPlayer();
      World world = p_195939_1_.getWorld();
      if (!world.isRemote && entityplayer != null) {
         BlockPos blockpos = p_195939_1_.getPos();
         this.handleClick(entityplayer, world.getBlockState(blockpos), world, blockpos, true, p_195939_1_.getItem());
      }

      return EnumActionResult.SUCCESS;
   }

   private void handleClick(EntityPlayer p_195958_1_, IBlockState p_195958_2_, IWorld p_195958_3_, BlockPos p_195958_4_, boolean rightClick, ItemStack p_195958_6_) {
      if (p_195958_1_.canUseCommandBlock()) {
         Block block = p_195958_2_.getBlock();
         StateContainer<Block, IBlockState> statecontainer = block.getStateContainer();
         Collection<IProperty<?>> collection = statecontainer.getProperties();
         String s = IRegistry.field_212618_g.getKey(block).toString();
         if (collection.isEmpty()) {
            sendMessage(p_195958_1_, new TextComponentTranslation(this.getTranslationKey() + ".empty", s));
         } else {
            NBTTagCompound nbttagcompound = p_195958_6_.getOrCreateChildTag("DebugProperty");
            String s1 = nbttagcompound.getString(s);
            IProperty<?> iproperty = statecontainer.getProperty(s1);
            if (rightClick) {
               if (iproperty == null) {
                  iproperty = collection.iterator().next();
               }

               IBlockState iblockstate = cycleProperty(p_195958_2_, iproperty, p_195958_1_.isSneaking());
               p_195958_3_.setBlockState(p_195958_4_, iblockstate, 18);
               sendMessage(p_195958_1_, new TextComponentTranslation(this.getTranslationKey() + ".update", iproperty.getName(), func_195957_a(iblockstate, iproperty)));
            } else {
               iproperty = getAdjacentValue(collection, iproperty, p_195958_1_.isSneaking());
               String s2 = iproperty.getName();
               nbttagcompound.setString(s, s2);
               sendMessage(p_195958_1_, new TextComponentTranslation(this.getTranslationKey() + ".select", s2, func_195957_a(p_195958_2_, iproperty)));
            }

         }
      }
   }

   private static <T extends Comparable<T>> IBlockState cycleProperty(IBlockState p_195960_0_, IProperty<T> p_195960_1_, boolean backwards) {
      return p_195960_0_.with(p_195960_1_, (T)(getAdjacentValue(p_195960_1_.getAllowedValues(), p_195960_0_.get(p_195960_1_), backwards)));
   }

   private static <T> T getAdjacentValue(Iterable<T> p_195959_0_, @Nullable T p_195959_1_, boolean p_195959_2_) {
      return (T)(p_195959_2_ ? Util.getElementBefore(p_195959_0_, p_195959_1_) : Util.getElementAfter(p_195959_0_, p_195959_1_));
   }

   private static void sendMessage(EntityPlayer p_195956_0_, ITextComponent p_195956_1_) {
      ((EntityPlayerMP)p_195956_0_).sendMessage(p_195956_1_, ChatType.GAME_INFO);
   }

   private static <T extends Comparable<T>> String func_195957_a(IBlockState p_195957_0_, IProperty<T> p_195957_1_) {
      return p_195957_1_.getName(p_195957_0_.get(p_195957_1_));
   }
}