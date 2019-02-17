package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUpdateRecipes implements Packet<INetHandlerPlayClient> {
   private List<IRecipe> recipes;

   public SPacketUpdateRecipes() {
   }

   public SPacketUpdateRecipes(Collection<IRecipe> p_i48176_1_) {
      this.recipes = Lists.newArrayList(p_i48176_1_);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleUpdateRecipes(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.recipes = Lists.newArrayList();
      int i = buf.readVarInt();

      for(int j = 0; j < i; ++j) {
         this.recipes.add(RecipeSerializers.read(buf));
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.recipes.size());

      for(IRecipe irecipe : this.recipes) {
         RecipeSerializers.write(irecipe, buf);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<IRecipe> getRecipes() {
      return this.recipes;
   }
}