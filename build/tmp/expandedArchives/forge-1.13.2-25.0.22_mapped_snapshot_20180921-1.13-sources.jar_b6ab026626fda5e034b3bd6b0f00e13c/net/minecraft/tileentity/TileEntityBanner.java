package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractBanner;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityBanner extends TileEntity implements INameable {
   private ITextComponent name;
   private EnumDyeColor baseColor = EnumDyeColor.WHITE;
   /** A list of all the banner patterns. */
   private NBTTagList patterns;
   private boolean patternDataSet;
   /** A list of all patterns stored on this banner. */
   private List<BannerPattern> patternList;
   /** A list of all the color values stored on this banner. */
   private List<EnumDyeColor> colorList;
   /** This is a String representation of this banners pattern and color lists, used for texture caching. */
   private String patternResourceLocation;

   public TileEntityBanner() {
      super(TileEntityType.BANNER);
   }

   public TileEntityBanner(EnumDyeColor p_i47731_1_) {
      this();
      this.baseColor = p_i47731_1_;
   }

   public void loadFromItemStack(ItemStack p_195534_1_, EnumDyeColor p_195534_2_) {
      this.patterns = null;
      NBTTagCompound nbttagcompound = p_195534_1_.getChildTag("BlockEntityTag");
      if (nbttagcompound != null && nbttagcompound.contains("Patterns", 9)) {
         this.patterns = nbttagcompound.getList("Patterns", 10).copy();
      }

      this.baseColor = p_195534_2_;
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = "";
      this.patternDataSet = true;
      this.name = p_195534_1_.hasDisplayName() ? p_195534_1_.getDisplayName() : null;
   }

   public ITextComponent getName() {
      return (ITextComponent)(this.name != null ? this.name : new TextComponentTranslation("block.minecraft.banner"));
   }

   public boolean hasCustomName() {
      return this.name != null;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.name;
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (this.patterns != null) {
         compound.setTag("Patterns", this.patterns);
      }

      if (this.name != null) {
         compound.setString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      return compound;
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      if (compound.contains("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

      if (this.hasWorld()) {
         this.baseColor = ((BlockAbstractBanner)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.patterns = compound.getList("Patterns", 10);
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = null;
      this.patternDataSet = true;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 6, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public NBTTagCompound getUpdateTag() {
      return this.write(new NBTTagCompound());
   }

   /**
    * Retrieves the amount of patterns stored on an ItemStack. If the tag does not exist this value will be 0.
    */
   public static int getPatterns(ItemStack stack) {
      NBTTagCompound nbttagcompound = stack.getChildTag("BlockEntityTag");
      return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getList("Patterns", 10).size() : 0;
   }

   /**
    * Retrieves the list of patterns for this tile entity. The banner data will be initialized/refreshed before this
    * happens.
    */
   @OnlyIn(Dist.CLIENT)
   public List<BannerPattern> getPatternList() {
      this.initializeBannerData();
      return this.patternList;
   }

   /**
    * Retrieves the list of colors for this tile entity. The banner data will be initialized/refreshed before this
    * happens.
    */
   @OnlyIn(Dist.CLIENT)
   public List<EnumDyeColor> getColorList() {
      this.initializeBannerData();
      return this.colorList;
   }

   @OnlyIn(Dist.CLIENT)
   public String getPatternResourceLocation() {
      this.initializeBannerData();
      return this.patternResourceLocation;
   }

   /**
    * Establishes all of the basic properties for the banner. This will also apply the data from the tile entities nbt
    * tag compounds.
    */
   @OnlyIn(Dist.CLIENT)
   private void initializeBannerData() {
      if (this.patternList == null || this.colorList == null || this.patternResourceLocation == null) {
         if (!this.patternDataSet) {
            this.patternResourceLocation = "";
         } else {
            this.patternList = Lists.newArrayList();
            this.colorList = Lists.newArrayList();
            EnumDyeColor enumdyecolor = this.getBaseColor(this::getBlockState);
            if (enumdyecolor == null) {
               this.patternResourceLocation = "banner_missing";
            } else {
               this.patternList.add(BannerPattern.BASE);
               this.colorList.add(enumdyecolor);
               this.patternResourceLocation = "b" + enumdyecolor.getId();
               if (this.patterns != null) {
                  for(int i = 0; i < this.patterns.size(); ++i) {
                     NBTTagCompound nbttagcompound = this.patterns.getCompound(i);
                     BannerPattern bannerpattern = BannerPattern.byHash(nbttagcompound.getString("Pattern"));
                     if (bannerpattern != null) {
                        this.patternList.add(bannerpattern);
                        int j = nbttagcompound.getInt("Color");
                        this.colorList.add(EnumDyeColor.byId(j));
                        this.patternResourceLocation = this.patternResourceLocation + bannerpattern.getHashname() + j;
                     }
                  }
               }
            }

         }
      }
   }

   /**
    * Removes all the banner related data from a provided instance of ItemStack.
    */
   public static void removeBannerData(ItemStack stack) {
      NBTTagCompound nbttagcompound = stack.getChildTag("BlockEntityTag");
      if (nbttagcompound != null && nbttagcompound.contains("Patterns", 9)) {
         NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);
         if (!nbttaglist.isEmpty()) {
            nbttaglist.remove(nbttaglist.size() - 1);
            if (nbttaglist.isEmpty()) {
               stack.removeChildTag("BlockEntityTag");
            }

         }
      }
   }

   public ItemStack getItem(IBlockState p_190615_1_) {
      ItemStack itemstack = new ItemStack(BlockBanner.forColor(this.getBaseColor(() -> {
         return p_190615_1_;
      })));
      if (this.patterns != null && !this.patterns.isEmpty()) {
         itemstack.getOrCreateChildTag("BlockEntityTag").setTag("Patterns", this.patterns.copy());
      }

      if (this.name != null) {
         itemstack.setDisplayName(this.name);
      }

      return itemstack;
   }

   public EnumDyeColor getBaseColor(Supplier<IBlockState> p_195533_1_) {
      if (this.baseColor == null) {
         this.baseColor = ((BlockAbstractBanner)p_195533_1_.get().getBlock()).getColor();
      }

      return this.baseColor;
   }
}