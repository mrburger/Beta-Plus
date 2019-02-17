package net.minecraft.tags;

import java.util.Collection;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemTags {
   private static TagCollection<Item> collection = new TagCollection<>((p_203643_0_) -> {
      return false;
   }, (p_203642_0_) -> {
      return null;
   }, "", false, "");
   private static int generation;
   public static final Tag<Item> WOOL = makeWrapperTag("wool");
   public static final Tag<Item> PLANKS = makeWrapperTag("planks");
   public static final Tag<Item> STONE_BRICKS = makeWrapperTag("stone_bricks");
   public static final Tag<Item> WOODEN_BUTTONS = makeWrapperTag("wooden_buttons");
   public static final Tag<Item> BUTTONS = makeWrapperTag("buttons");
   public static final Tag<Item> CARPETS = makeWrapperTag("carpets");
   public static final Tag<Item> WOODEN_DOORS = makeWrapperTag("wooden_doors");
   public static final Tag<Item> WOODEN_STAIRS = makeWrapperTag("wooden_stairs");
   public static final Tag<Item> WOODEN_SLABS = makeWrapperTag("wooden_slabs");
   public static final Tag<Item> WOODEN_PRESSURE_PLATES = makeWrapperTag("wooden_pressure_plates");
   public static final Tag<Item> WOODEN_TRAPDOORS = makeWrapperTag("wooden_trapdoors");
   public static final Tag<Item> DOORS = makeWrapperTag("doors");
   public static final Tag<Item> SAPLINGS = makeWrapperTag("saplings");
   public static final Tag<Item> LOGS = makeWrapperTag("logs");
   public static final Tag<Item> DARK_OAK_LOGS = makeWrapperTag("dark_oak_logs");
   public static final Tag<Item> OAK_LOGS = makeWrapperTag("oak_logs");
   public static final Tag<Item> BIRCH_LOGS = makeWrapperTag("birch_logs");
   public static final Tag<Item> ACACIA_LOGS = makeWrapperTag("acacia_logs");
   public static final Tag<Item> JUNGLE_LOGS = makeWrapperTag("jungle_logs");
   public static final Tag<Item> SPRUCE_LOGS = makeWrapperTag("spruce_logs");
   public static final Tag<Item> BANNERS = makeWrapperTag("banners");
   public static final Tag<Item> SAND = makeWrapperTag("sand");
   public static final Tag<Item> STAIRS = makeWrapperTag("stairs");
   public static final Tag<Item> SLABS = makeWrapperTag("slabs");
   public static final Tag<Item> ANVIL = makeWrapperTag("anvil");
   public static final Tag<Item> RAILS = makeWrapperTag("rails");
   public static final Tag<Item> LEAVES = makeWrapperTag("leaves");
   public static final Tag<Item> TRAPDOORS = makeWrapperTag("trapdoors");
   public static final Tag<Item> BOATS = makeWrapperTag("boats");
   public static final Tag<Item> FISHES = makeWrapperTag("fishes");

   public static void setCollection(TagCollection<Item> collectionIn) {
      collection = collectionIn;
      ++generation;
   }

   public static TagCollection<Item> getCollection() {
      return collection;
   }

   private static Tag<Item> makeWrapperTag(String p_199901_0_) {
      return new ItemTags.Wrapper(new ResourceLocation(p_199901_0_));
   }

   public static class Wrapper extends Tag<Item> {
      private int lastKnownGeneration = -1;
      private Tag<Item> cachedTag;

      public Wrapper(ResourceLocation resourceLocationIn) {
         super(resourceLocationIn);
      }

      public boolean contains(Item itemIn) {
         if (this.lastKnownGeneration != ItemTags.generation) {
            this.cachedTag = ItemTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = ItemTags.generation;
         }

         return this.cachedTag.contains(itemIn);
      }

      public Collection<Item> getAllElements() {
         if (this.lastKnownGeneration != ItemTags.generation) {
            this.cachedTag = ItemTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = ItemTags.generation;
         }

         return this.cachedTag.getAllElements();
      }

      public Collection<Tag.ITagEntry<Item>> getEntries() {
         if (this.lastKnownGeneration != ItemTags.generation) {
            this.cachedTag = ItemTags.collection.getOrCreate(this.getId());
            this.lastKnownGeneration = ItemTags.generation;
         }

         return this.cachedTag.getEntries();
      }
   }
}