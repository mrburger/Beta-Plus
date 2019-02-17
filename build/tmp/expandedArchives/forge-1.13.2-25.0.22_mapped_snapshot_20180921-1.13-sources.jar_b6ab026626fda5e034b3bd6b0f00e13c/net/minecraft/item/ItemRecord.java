package net.minecraft.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemRecord extends Item {
   private static final Map<SoundEvent, ItemRecord> RECORDS = Maps.newHashMap();
   private static final List<ItemRecord> field_195976_b = Lists.newArrayList();
   private final int comparatorValue;
   private final SoundEvent sound;

   protected ItemRecord(int comparatorValueIn, SoundEvent soundIn, Item.Properties builder) {
      super(builder);
      this.comparatorValue = comparatorValueIn;
      this.sound = soundIn;
      RECORDS.put(this.sound, this);
      field_195976_b.add(this);
   }

   public static ItemRecord getRandom(Random p_195974_0_) {
      return field_195976_b.get(p_195974_0_.nextInt(field_195976_b.size()));
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      BlockPos blockpos = p_195939_1_.getPos();
      IBlockState iblockstate = world.getBlockState(blockpos);
      if (iblockstate.getBlock() == Blocks.JUKEBOX && !iblockstate.get(BlockJukebox.HAS_RECORD)) {
         ItemStack itemstack = p_195939_1_.getItem();
         if (!world.isRemote) {
            ((BlockJukebox)Blocks.JUKEBOX).insertRecord(world, blockpos, iblockstate, itemstack);
            world.playEvent((EntityPlayer)null, 1010, blockpos, Item.getIdFromItem(this));
            itemstack.shrink(1);
            EntityPlayer entityplayer = p_195939_1_.getPlayer();
            if (entityplayer != null) {
               entityplayer.addStat(StatList.PLAY_RECORD);
            }
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public int getComparatorValue() {
      return this.comparatorValue;
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(this.getRecordDescription().applyTextStyle(TextFormatting.GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getRecordDescription() {
      return new TextComponentTranslation(this.getTranslationKey() + ".desc");
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static ItemRecord getBySound(SoundEvent soundIn) {
      return RECORDS.get(soundIn);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
   }
}