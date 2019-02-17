package net.minecraft.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntitySign extends TileEntity implements ICommandSource {
   public final ITextComponent[] signText = new ITextComponent[]{new TextComponentString(""), new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};
   /**
    * The index of the line currently being edited. Only used on client side, but defined on both. Note this is only
    * really used when the > < are going to be visible.
    */
   public int lineBeingEdited = -1;
   private boolean isEditable = true;
   private EntityPlayer player;
   private final String[] field_212367_h = new String[4];

   public TileEntitySign() {
      super(TileEntityType.SIGN);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);

      for(int i = 0; i < 4; ++i) {
         String s = ITextComponent.Serializer.toJson(this.signText[i]);
         compound.setString("Text" + (i + 1), s);
      }

      return compound;
   }

   public void read(NBTTagCompound compound) {
      this.isEditable = false;
      super.read(compound);

      for(int i = 0; i < 4; ++i) {
         String s = compound.getString("Text" + (i + 1));
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(s);
         if (this.world instanceof WorldServer) {
            try {
               this.signText[i] = TextComponentUtils.updateForEntity(this.getCommandSource((EntityPlayerMP)null), itextcomponent, (Entity)null);
            } catch (CommandSyntaxException var6) {
               this.signText[i] = itextcomponent;
            }
         } else {
            this.signText[i] = itextcomponent;
         }

         this.field_212367_h[i] = null;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent func_212366_a(int p_212366_1_) {
      return this.signText[p_212366_1_];
   }

   public void func_212365_a(int p_212365_1_, ITextComponent p_212365_2_) {
      this.signText[p_212365_1_] = p_212365_2_;
      this.field_212367_h[p_212365_1_] = null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String func_212364_a(int p_212364_1_, Function<ITextComponent, String> p_212364_2_) {
      if (this.field_212367_h[p_212364_1_] == null && this.signText[p_212364_1_] != null) {
         this.field_212367_h[p_212364_1_] = p_212364_2_.apply(this.signText[p_212364_1_]);
      }

      return this.field_212367_h[p_212364_1_];
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 9, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      return this.write(new NBTTagCompound());
   }

   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   /**
    * Sets the sign's isEditable flag to the specified parameter.
    */
   @OnlyIn(Dist.CLIENT)
   public void setEditable(boolean isEditableIn) {
      this.isEditable = isEditableIn;
      if (!isEditableIn) {
         this.player = null;
      }

   }

   public void setPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean executeCommand(EntityPlayer playerIn) {
      for(ITextComponent itextcomponent : this.signText) {
         Style style = itextcomponent == null ? null : itextcomponent.getStyle();
         if (style != null && style.getClickEvent() != null) {
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               playerIn.getServer().getCommandManager().handleCommand(this.getCommandSource((EntityPlayerMP)playerIn), clickevent.getValue());
            }
         }
      }

      return true;
   }

   /**
    * Send a chat message to the CommandSender
    */
   public void sendMessage(ITextComponent component) {
   }

   public CommandSource getCommandSource(@Nullable EntityPlayerMP playerIn) {
      String s = playerIn == null ? "Sign" : playerIn.getName().getString();
      ITextComponent itextcomponent = (ITextComponent)(playerIn == null ? new TextComponentString("Sign") : playerIn.getDisplayName());
      return new CommandSource(this, new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D), Vec2f.ZERO, (WorldServer)this.world, 2, s, itextcomponent, this.world.getServer(), playerIn);
   }

   public boolean shouldReceiveFeedback() {
      return false;
   }

   public boolean shouldReceiveErrors() {
      return false;
   }

   public boolean allowLogging() {
      return false;
   }
}