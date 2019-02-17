package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

public class GiveCommand {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("give").requires((p_198496_0_) -> {
         return p_198496_0_.hasPermissionLevel(2);
      }).then(Commands.argument("targets", EntityArgument.multiplePlayers()).then(Commands.argument("item", ItemArgument.itemStack()).executes((p_198493_0_) -> {
         return giveItem(p_198493_0_.getSource(), ItemArgument.getItemStack(p_198493_0_, "item"), EntityArgument.getPlayers(p_198493_0_, "targets"), 1);
      }).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((p_198495_0_) -> {
         return giveItem(p_198495_0_.getSource(), ItemArgument.getItemStack(p_198495_0_, "item"), EntityArgument.getPlayers(p_198495_0_, "targets"), IntegerArgumentType.getInteger(p_198495_0_, "count"));
      })))));
   }

   private static int giveItem(CommandSource source, ItemInput p_198497_1_, Collection<EntityPlayerMP> targets, int count) throws CommandSyntaxException {
      for(EntityPlayerMP entityplayermp : targets) {
         int i = count;

         while(i > 0) {
            int j = Math.min(p_198497_1_.getItem().getMaxStackSize(), i);
            i -= j;
            ItemStack itemstack = p_198497_1_.createStack(j, false);
            boolean flag = entityplayermp.inventory.addItemStackToInventory(itemstack);
            if (flag && itemstack.isEmpty()) {
               itemstack.setCount(1);
               EntityItem entityitem1 = entityplayermp.dropItem(itemstack, false);
               if (entityitem1 != null) {
                  entityitem1.makeFakeItem();
               }

               entityplayermp.world.playSound((EntityPlayer)null, entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayermp.getRNG().nextFloat() - entityplayermp.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               entityplayermp.inventoryContainer.detectAndSendChanges();
            } else {
               EntityItem entityitem = entityplayermp.dropItem(itemstack, false);
               if (entityitem != null) {
                  entityitem.setNoPickupDelay();
                  entityitem.setOwnerId(entityplayermp.getUniqueID());
               }
            }
         }
      }

      if (targets.size() == 1) {
         source.sendFeedback(new TextComponentTranslation("commands.give.success.single", count, p_198497_1_.createStack(count, false).getTextComponent(), targets.iterator().next().getDisplayName()), true);
      } else {
         source.sendFeedback(new TextComponentTranslation("commands.give.success.single", count, p_198497_1_.createStack(count, false).getTextComponent(), targets.size()), true);
      }

      return targets.size();
   }
}