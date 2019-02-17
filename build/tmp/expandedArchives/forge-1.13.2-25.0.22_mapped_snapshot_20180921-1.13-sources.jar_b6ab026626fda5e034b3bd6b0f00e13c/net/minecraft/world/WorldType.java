package net.minecraft.world;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldType implements net.minecraftforge.common.extensions.IForgeWorldType {
   /** List of world types. */
   public static WorldType[] WORLD_TYPES = new WorldType[16];
   /** Default world type. */
   public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
   /** Flat world type. */
   public static final WorldType FLAT = (new WorldType(1, "flat")).setCustomOptions(true);
   /** Large Biome world Type. */
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   /** amplified world type */
   public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).enableInfoNotice();
   public static final WorldType CUSTOMIZED = (new WorldType(4, "customized", "normal", 0)).setCustomOptions(true).setCanBeCreated(false);
   public static final WorldType BUFFET = (new WorldType(5, "buffet")).setCustomOptions(true);
   public static final WorldType DEBUG_ALL_BLOCK_STATES = new WorldType(6, "debug_all_block_states");
   /** Default (1.1) world type. */
   public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
   /** ID for this world type. */
   private final int id;
   private final String name;
   private final String field_211890_l;
   /** The int version of the ChunkProvider that generated this world. */
   private final int version;
   /** Whether this world type can be generated. Normally true; set to false for out-of-date generator versions. */
   private boolean canBeCreated;
   /** Whether this WorldType has a version or not. */
   private boolean versioned;
   private boolean hasInfoNotice;
   private boolean field_205395_p;

   public WorldType(String name) {
      this(getNextID(), name);
   }

   private WorldType(int id, String name) {
      this(id, name, name, 0);
   }

   private WorldType(int id, String name, int version) {
      this(id, name, name, version);
   }

   private WorldType(int p_i49778_1_, String p_i49778_2_, String p_i49778_3_, int p_i49778_4_) {
      if (p_i49778_2_.length() > 16 && DEBUG_ALL_BLOCK_STATES != null) throw new IllegalArgumentException("World type names must not be longer then 16: " + p_i49778_2_);
      this.name = p_i49778_2_;
      this.field_211890_l = p_i49778_3_;
      this.version = p_i49778_4_;
      this.canBeCreated = true;
      this.id = p_i49778_1_;
      WORLD_TYPES[p_i49778_1_] = this;
   }

   private static int getNextID() {
      for (int x = 0; x < WORLD_TYPES.length; x++) {
         if (WORLD_TYPES[x] == null) {
            return x;
         }
      }
      int old = WORLD_TYPES.length;
      WORLD_TYPES = java.util.Arrays.copyOf(WORLD_TYPES, old + 16);
      return old;
   }

   public String getName() {
      return this.name;
   }

   public String getSerialization() {
      return this.field_211890_l;
   }

   /**
    * Gets the translation key for the name of this world type.
    */
   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "generator." + this.name;
   }

   /**
    * Gets the translation key for the info text for this world type.
    */
   @OnlyIn(Dist.CLIENT)
   public String getInfoTranslationKey() {
      return this.getTranslationKey() + ".info";
   }

   /**
    * Returns generatorVersion.
    */
   public int getVersion() {
      return this.version;
   }

   public WorldType getWorldTypeForGeneratorVersion(int version) {
      return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomOptions() {
      return this.field_205395_p;
   }

   public WorldType setCustomOptions(boolean p_205392_1_) {
      this.field_205395_p = p_205392_1_;
      return this;
   }

   /**
    * Sets canBeCreated to the provided value, and returns this.
    */
   private WorldType setCanBeCreated(boolean enable) {
      this.canBeCreated = enable;
      return this;
   }

   /**
    * Gets whether this WorldType can be used to generate a new world.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean canBeCreated() {
      return this.canBeCreated;
   }

   /**
    * Flags this world type as having an associated version.
    */
   private WorldType setVersioned() {
      this.versioned = true;
      return this;
   }

   /**
    * Returns true if this world Type has a version associated with it.
    */
   public boolean isVersioned() {
      return this.versioned;
   }

   public static WorldType byName(String type) {
      for(WorldType worldtype : WORLD_TYPES) {
         if (worldtype != null && worldtype.name.equalsIgnoreCase(type)) {
            return worldtype;
         }
      }

      return null;
   }

   public int getId() {
      return this.id;
   }

   /**
    * returns true if selecting this worldtype from the customize menu should display the generator.[worldtype].info
    * message
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasInfoNotice() {
      return this.hasInfoNotice;
   }

   /**
    * enables the display of generator.[worldtype].info message on the customize world menu
    */
   private WorldType enableInfoNotice() {
      this.hasInfoNotice = true;
      return this;
   }
}