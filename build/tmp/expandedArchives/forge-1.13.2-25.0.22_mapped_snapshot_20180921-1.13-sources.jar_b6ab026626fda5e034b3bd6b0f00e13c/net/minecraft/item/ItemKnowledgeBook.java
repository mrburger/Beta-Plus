package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemKnowledgeBook extends Item {
   private static final Logger LOGGER = LogManager.getLogger();

   public ItemKnowledgeBook(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      NBTTagCompound nbttagcompound = itemstack.getTag();
      if (!playerIn.abilities.isCreativeMode) {
         playerIn.setHeldItem(handIn, ItemStack.EMPTY);
      }

      if (nbttagcompound != null && nbttagcompound.contains("Recipes", 9)) {
         if (!worldIn.isRemote) {
            NBTTagList nbttaglist = nbttagcompound.getList("Recipes", 8);
            List<IRecipe> list = Lists.newArrayList();

            for(int i = 0; i < nbttaglist.size(); ++i) {
               String s = nbttaglist.getString(i);
               IRecipe irecipe = worldIn.getServer().getRecipeManager().getRecipe(new ResourceLocation(s));
               if (irecipe == null) {
                  LOGGER.error("Invalid recipe: {}", (Object)s);
                  return new ActionResult<>(EnumActionResult.FAIL, itemstack);
               }

               list.add(irecipe);
            }

            playerIn.unlockRecipes(list);
            playerIn.addStat(StatList.ITEM_USED.get(this));
         }

         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         LOGGER.error("Tag not valid: {}", (Object)nbttagcompound);
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }
}