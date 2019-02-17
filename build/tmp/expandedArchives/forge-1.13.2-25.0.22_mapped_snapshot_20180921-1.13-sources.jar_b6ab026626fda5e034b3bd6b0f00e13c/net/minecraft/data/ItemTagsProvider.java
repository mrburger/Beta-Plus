package net.minecraft.data;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemTagsProvider extends TagsProvider<Item> {
   private static final Logger LOGGER = LogManager.getLogger();

   public ItemTagsProvider(DataGenerator generatorIn) {
      super(generatorIn, IRegistry.field_212630_s);
   }

   protected void registerTags() {
      this.copy(BlockTags.WOOL, ItemTags.WOOL);
      this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
      this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
      this.copy(BlockTags.CARPETS, ItemTags.CARPETS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.DOORS, ItemTags.DOORS);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
      this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
      this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
      this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
      this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
      this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
      this.copy(BlockTags.LOGS, ItemTags.LOGS);
      this.copy(BlockTags.SAND, ItemTags.SAND);
      this.copy(BlockTags.SLABS, ItemTags.SLABS);
      this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
      this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
      this.copy(BlockTags.RAILS, ItemTags.RAILS);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
      this.getBuilder(ItemTags.BANNERS).add(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER);
      this.getBuilder(ItemTags.BOATS).add(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT);
      this.getBuilder(ItemTags.FISHES).add(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
   }

   /**
    * Copies a block tag into an item tag
    *  
    * @param from The block tag to copy from
    * @param to The item tag to copy to
    */
   protected void copy(Tag<Block> from, Tag<Item> to) {
      Tag.Builder<Item> builder = this.getBuilder(to);

      for(Tag.ITagEntry<Block> itagentry : from.getEntries()) {
         Tag.ITagEntry<Item> itagentry1 = this.copyEntry(itagentry);
         builder.add(itagentry1);
      }

   }

   private Tag.ITagEntry<Item> copyEntry(Tag.ITagEntry<Block> entry) {
      if (entry instanceof Tag.TagEntry) {
         return new Tag.TagEntry<>(((Tag.TagEntry)entry).getSerializedId());
      } else if (entry instanceof Tag.ListEntry) {
         List<Item> list = Lists.newArrayList();

         for(Block block : ((Tag.ListEntry<Block>)entry).getTaggedItems()) {
            Item item = block.asItem();
            if (item == Items.AIR) {
               LOGGER.warn("Itemless block copied to item tag: {}", (Object)IRegistry.field_212618_g.getKey(block));
            } else {
               list.add(item);
            }
         }

         return new Tag.ListEntry<>(list);
      } else {
         throw new UnsupportedOperationException("Unknown tag entry " + entry);
      }
   }

   /**
    * Resolves a Path for the location to save the given tag.
    *  
    * @param id ID of the tag
    */
   protected Path makePath(ResourceLocation id) {
      return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "Item Tags";
   }

   protected void setCollection(TagCollection<Item> colectionIn) {
      ItemTags.setCollection(colectionIn);
   }
}