package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemFireworkRocket extends Item {
   public ItemFireworkRocket(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getWorld();
      if (!world.isRemote) {
         BlockPos blockpos = p_195939_1_.getPos();
         ItemStack itemstack = p_195939_1_.getItem();
         EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(world, (double)((float)blockpos.getX() + p_195939_1_.getHitX()), (double)((float)blockpos.getY() + p_195939_1_.getHitY()), (double)((float)blockpos.getZ() + p_195939_1_.getHitZ()), itemstack);
         world.spawnEntity(entityfireworkrocket);
         itemstack.shrink(1);
      }

      return EnumActionResult.SUCCESS;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (playerIn.isElytraFlying()) {
         ItemStack itemstack = playerIn.getHeldItem(handIn);
         if (!worldIn.isRemote) {
            EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(worldIn, itemstack, playerIn);
            worldIn.spawnEntity(entityfireworkrocket);
            if (!playerIn.abilities.isCreativeMode) {
               itemstack.shrink(1);
            }
         }

         return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
      } else {
         return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
      }
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      NBTTagCompound nbttagcompound = stack.getChildTag("Fireworks");
      if (nbttagcompound != null) {
         if (nbttagcompound.contains("Flight", 99)) {
            tooltip.add((new TextComponentTranslation("item.minecraft.firework_rocket.flight")).appendText(" ").appendText(String.valueOf((int)nbttagcompound.getByte("Flight"))).applyTextStyle(TextFormatting.GRAY));
         }

         NBTTagList nbttaglist = nbttagcompound.getList("Explosions", 10);
         if (!nbttaglist.isEmpty()) {
            for(int i = 0; i < nbttaglist.size(); ++i) {
               NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
               List<ITextComponent> list = Lists.newArrayList();
               ItemFireworkStar.func_195967_a(nbttagcompound1, list);
               if (!list.isEmpty()) {
                  for(int j = 1; j < list.size(); ++j) {
                     list.set(j, (new TextComponentString("  ")).appendSibling(list.get(j)).applyTextStyle(TextFormatting.GRAY));
                  }

                  tooltip.addAll(list);
               }
            }
         }

      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final ItemFireworkRocket.Shape[] field_196077_f = Arrays.stream(values()).sorted(Comparator.comparingInt((p_199796_0_) -> {
         return p_199796_0_.field_196078_g;
      })).toArray((p_199797_0_) -> {
         return new ItemFireworkRocket.Shape[p_199797_0_];
      });
      private final int field_196078_g;
      private final String field_196079_h;

      private Shape(int p_i47931_3_, String p_i47931_4_) {
         this.field_196078_g = p_i47931_3_;
         this.field_196079_h = p_i47931_4_;
      }

      public int func_196071_a() {
         return this.field_196078_g;
      }

      @OnlyIn(Dist.CLIENT)
      public String func_196068_b() {
         return this.field_196079_h;
      }

      @OnlyIn(Dist.CLIENT)
      public static ItemFireworkRocket.Shape func_196070_a(int p_196070_0_) {
         return p_196070_0_ >= 0 && p_196070_0_ < field_196077_f.length ? field_196077_f[p_196070_0_] : SMALL_BALL;
      }
   }
}