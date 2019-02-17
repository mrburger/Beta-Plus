package net.minecraft.tileentity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements IInteractionObject, ITickable {
   public int field_195522_a;
   public float field_195523_f;
   public float field_195524_g;
   public float field_195525_h;
   public float field_195526_i;
   public float field_195527_j;
   public float field_195528_k;
   public float field_195529_l;
   public float field_195530_m;
   public float field_195531_n;
   private static final Random field_195532_o = new Random();
   private ITextComponent customname;

   public TileEntityEnchantmentTable() {
      super(TileEntityType.ENCHANTING_TABLE);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (this.hasCustomName()) {
         compound.setString("CustomName", ITextComponent.Serializer.toJson(this.customname));
      }

      return compound;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      if (compound.contains("CustomName", 8)) {
         this.customname = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

   }

   public void tick() {
      this.field_195528_k = this.field_195527_j;
      this.field_195530_m = this.field_195529_l;
      EntityPlayer entityplayer = this.world.getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0D, false);
      if (entityplayer != null) {
         double d0 = entityplayer.posX - (double)((float)this.pos.getX() + 0.5F);
         double d1 = entityplayer.posZ - (double)((float)this.pos.getZ() + 0.5F);
         this.field_195531_n = (float)MathHelper.atan2(d1, d0);
         this.field_195527_j += 0.1F;
         if (this.field_195527_j < 0.5F || field_195532_o.nextInt(40) == 0) {
            float f1 = this.field_195525_h;

            while(true) {
               this.field_195525_h += (float)(field_195532_o.nextInt(4) - field_195532_o.nextInt(4));
               if (f1 != this.field_195525_h) {
                  break;
               }
            }
         }
      } else {
         this.field_195531_n += 0.02F;
         this.field_195527_j -= 0.1F;
      }

      while(this.field_195529_l >= (float)Math.PI) {
         this.field_195529_l -= ((float)Math.PI * 2F);
      }

      while(this.field_195529_l < -(float)Math.PI) {
         this.field_195529_l += ((float)Math.PI * 2F);
      }

      while(this.field_195531_n >= (float)Math.PI) {
         this.field_195531_n -= ((float)Math.PI * 2F);
      }

      while(this.field_195531_n < -(float)Math.PI) {
         this.field_195531_n += ((float)Math.PI * 2F);
      }

      float f2;
      for(f2 = this.field_195531_n - this.field_195529_l; f2 >= (float)Math.PI; f2 -= ((float)Math.PI * 2F)) {
         ;
      }

      while(f2 < -(float)Math.PI) {
         f2 += ((float)Math.PI * 2F);
      }

      this.field_195529_l += f2 * 0.4F;
      this.field_195527_j = MathHelper.clamp(this.field_195527_j, 0.0F, 1.0F);
      ++this.field_195522_a;
      this.field_195524_g = this.field_195523_f;
      float f = (this.field_195525_h - this.field_195523_f) * 0.4F;
      float f3 = 0.2F;
      f = MathHelper.clamp(f, -0.2F, 0.2F);
      this.field_195526_i += (f - this.field_195526_i) * 0.9F;
      this.field_195523_f += this.field_195526_i;
   }

   public ITextComponent getName() {
      return (ITextComponent)(this.customname != null ? this.customname : new TextComponentTranslation("container.enchant"));
   }

   public boolean hasCustomName() {
      return this.customname != null;
   }

   public void setCustomName(@Nullable ITextComponent name) {
      this.customname = name;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customname;
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerEnchantment(playerInventory, this.world, this.pos);
   }

   public String getGuiID() {
      return "minecraft:enchanting_table";
   }
}